/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics.plugin;

import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * Annotation processor that produces lenses for each component of an annotated
 * record class.
 */
@SupportedAnnotationTypes("unf.optics.RecordLenses")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public final class RecordLensesProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    return annotations.stream()
        // Find all elements annotated with our annotation
        .flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation)
            .stream())
        // Filter for non-private records
        .filter(this::isNonPrivateRecord)
        // Safely cast to TypeElement (should always succeed at this point)
        .filter(TypeElement.class::isInstance)
        .map(TypeElement.class::cast)
        // Process each record class
        .allMatch(this::processRecord);
  }

  private boolean isNonPrivateRecord(Element el) {
    if (el.getKind() == ElementKind.RECORD) {
      if (el.getModifiers().contains(Modifier.PRIVATE)) {
        printSkipWarning("private record", el);
        return false;
      } else {
        return true;
      }
    } else {
      printSkipWarning("non-record class", el);
      return false;
    }
  }

  private boolean processRecord(TypeElement recordElement) {
    final Messager messager = processingEnv.getMessager();
    final Filer filer = processingEnv.getFiler();

    final String packageName = getPackageName(recordElement);
    final String simpleName = getLensesSimpleName(recordElement, packageName);

    try {
      final JavaFileObject genFile = filer.createSourceFile(
          packageName + "." + simpleName,
          recordElement
      );
      LensesGenerator.generate(recordElement, genFile, packageName, simpleName);
      return true;
    } catch (IOException e) {
      messager.printError(e.getMessage(), recordElement);
      return false;
    }
  }

  private String getLensesSimpleName(TypeElement element, String packageName) {
    final String classNameWithEnclosing = element.getQualifiedName()
        .toString()
        // +1 for the "." after the package name
        .substring(packageName.length() + 1)
        // Replace the "." of inner classes with "$"
        .replace(".", "$");
    return classNameWithEnclosing + "Lenses";
  }

  /**
   * Resolve the package of an element.
   */
  private String getPackageName(Element element) {
    Element itr = element;
    while (itr != null) {
      itr = itr.getEnclosingElement();
      if (itr instanceof PackageElement pkgElement) {
        return pkgElement.getQualifiedName().toString();
      }
    }
    // Default package
    return "";
  }

  private void printSkipWarning(String reason, Element el) {
    final Messager messager = processingEnv.getMessager();
    messager.printWarning("Skipping Lenses generation for "
        + reason
        + " "
        + el.getSimpleName().toString());
  }
}
