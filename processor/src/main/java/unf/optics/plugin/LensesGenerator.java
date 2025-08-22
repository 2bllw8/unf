/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics.plugin;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

/**
 * Annotation processor that produces lenses for each component of an annotated
 * record class.
 */
final class LensesGenerator {

  public static final String FUNCTION_1_CLASS_NAME = "unf.function.Function1";

  public static final String FUNCTION_2_CLASS_NAME = "unf.function.Function2";

  private static final String LENS_CLASS_NAME = "unf.optics.Lens";

  private static final String TRAVERSAL_CLASS_NAME = "unf.optics.Traversal";

  private LensesGenerator() {
  }

  public static void generate(
      Elements elements,
      Types types,
      TypeElement targetRecordElement,
      JavaFileObject file,
      String className
  ) throws IOException {
    final TypeMirror targetRecordType = targetRecordElement.asType();
    final SequencedMap<String, TypeMirror> allComponents
        = getRecordComponents(targetRecordElement);
    final SequencedMap<String, TypeMirror> listComponents
        = filterListComponents(elements, types, allComponents);
    final SequencedSet<String> allComponentNames
        = allComponents.sequencedKeySet();

    // Lenses for each component
    final List<String> lensesDeclarations
        = ComponentLensesGenerator.lensesForComponents(
        types,
        targetRecordType,
        allComponents,
        allComponentNames
    );

    // Traversals for components of type List
    final List<String> traversalsDeclarations
        = ComponentTraversalsGenerator.traversalsForComponents(
        types,
        targetRecordType,
        listComponents,
        allComponentNames
    );

    // All types that are used in the implementation of this class
    final SequencedSet<String> typesToImport = getUsedTypes(
        types,
        targetRecordType,
        allComponents.values()
    );

    // "protected" is semantically equivalent to package-private (no visibility
    // modifier) for record classes because they cannot be extended: we only
    // care about whether the record is public or not and we want our *Lenses
    // class to match that
    final boolean isPublic = targetRecordElement.getModifiers()
        .contains(Modifier.PUBLIC);

    // Write file
    try (Writer writer = file.openWriter()) {
      writeClassFile(
          writer,
          elements.getPackageOf(targetRecordElement),
          typesToImport,
          className,
          isPublic,
          lensesDeclarations,
          traversalsDeclarations
      );
    }
  }

  /**
   * Get a mapping from name to {@link TypeMirror} of all components of a given
   * record.
   */
  private static SequencedMap<String, TypeMirror> getRecordComponents(
      TypeElement recordElement
  ) {
    return recordElement.getRecordComponents()
        .stream()
        .collect(Collectors.toMap(
            rce -> rce.getSimpleName().toString(),
            // Note 1: Need the boxed version of the type for type parameters!
            // Note 2: we use fully qualified types because at this point we
            //         cannot reliably determine whether a "segment" of the
            //         fully qualified name corresponds to a package or an
            //         outer class.
            rce -> rce.getAccessor().getReturnType(),
            LensesGenerator::noDuplicateKeys,
            // Order matters because TypeElement#getRecordComponents provides
            // record components in the order used by the default record
            // constructor
            LinkedHashMap::new
        ));
  }

  /**
   * Filter the given mapping from name to {@link TypeMirror} of record
   * components to only those that are of type {@link List}.
   */
  private static SequencedMap<String, TypeMirror> filterListComponents(
      Elements elements,
      Types types,
      SequencedMap<String, TypeMirror> allComponents
  ) {
    // Need to erase, otherwise a type like List<String> would not be
    // considered assignable to it.
    final TypeMirror listTypeMirror = types.erasure(
        elements.getTypeElement(List.class.getTypeName()).asType()
    );
    return allComponents.sequencedEntrySet()
        .stream()
        .filter(e -> listTypeMirror.equals(types.erasure(e.getValue())))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            LensesGenerator::noDuplicateKeys,
            LinkedHashMap::new
        ));
  }

  /**
   * Throw an {@link IllegalStateException} when handling two entries of a map
   * with the same key.
   */
  private static <T> T noDuplicateKeys(T ignoredA, T ignoredB) {
    throw new IllegalStateException("Duplicate elements are not allowed");
  }

  private static SequencedSet<String> getUsedTypes(
      Types types,
      TypeMirror targetRecordType,
      Collection<TypeMirror> componentTypes
  ) {
    final boolean hasListComponent = componentTypes.stream()
        .anyMatch(t -> t.toString().startsWith(List.class.getName()));
    final SequencedSet<String> usedTypes = new LinkedHashSet<>();

    // Function types
    usedTypes.add(FUNCTION_1_CLASS_NAME);
    if (hasListComponent) {
      usedTypes.add(FUNCTION_2_CLASS_NAME);
    }

    // Optic types
    usedTypes.add(LENS_CLASS_NAME);
    if (hasListComponent) {
      usedTypes.add(TRAVERSAL_CLASS_NAME);
      usedTypes.add(IntStream.class.getName());
    }

    // Record types
    usedTypes.add(targetRecordType.toString());
    componentTypes.stream()
        // Skip primitives
        .filter(t -> !(t instanceof PrimitiveType))
        // Erase type parameters
        .map(types::erasure)
        .forEach(t -> usedTypes.add(t.toString()));

    return Collections.unmodifiableSequencedSet(usedTypes);
  }

  private static void writeClassFile(
      Writer writer,
      PackageElement packageElement,
      SequencedSet<String> typesToImport,
      String className,
      boolean isPublic,
      List<String> lensesDeclarations,
      List<String> traversalsDeclarations
  ) throws IOException {
    // Package statement
    writer.append("package ")
        .append(packageElement.toString())
        .append(";\n\n");

    // Imports
    for (final String type : typesToImport) {
      writer.append("import ")
          .append(type)
          .append(";\n");
    }
    writer.append('\n');

    // Class declaration statement
    if (isPublic) {
      writer.append("public ");
    }
    writer.append("final class ")
        .append(className)
        .append(" {\n\n");

    // Private default constructor
    writer.append("  private ")
        .append(className)
        .append("() {\n  }\n");

    // Lenses fields
    for (final String lensDeclaration : lensesDeclarations) {
      writer.append('\n')
          .append(lensDeclaration);
    }

    // Traversals fields
    if (!traversalsDeclarations.isEmpty()) {
      for (final String traversalDeclaration : traversalsDeclarations) {
        writer.append('\n')
            .append(traversalDeclaration);
      }

      // Traversal helper method
      writer.append('\n')
          .write(ComponentTraversalsGenerator.getMethodLensAtIndexImpl());
    }

    writer.write("}\n");
  }
}
