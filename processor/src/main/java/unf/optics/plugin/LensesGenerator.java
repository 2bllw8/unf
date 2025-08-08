/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics.plugin;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * Annotation processor that produces lenses for each component of an annotated
 * record class.
 */
final class LensesGenerator {

  public static final String FUNCTION_1_CLASS_NAME = "unf.function.Function1";

  private static final String LENS_CLASS_NAME = "unf.optics.Lens";

  private static final Pattern IS_IN_BASE_PKG = Pattern.compile(
      "^java\\.lang\\.\\w+$");

  private LensesGenerator() {
  }

  public static void generate(
      TypeElement recordElement,
      JavaFileObject file,
      String enclosingPackage,
      String lensesClassName
  ) throws IOException {
    final SequencedMap<String, String> components
        = buildComponentsNameTypeMapping(recordElement);

    final StringBuilder sb = new StringBuilder();

    final String sourceFullyQualifiedName = recordElement.getQualifiedName()
        .toString();
    classHeader(sb, enclosingPackage, sourceFullyQualifiedName);

    // "protected" is semantically equivalent to package-private (no visibility
    // modifier) for record classes because they cannot be extended: we only
    // care about whether the record is public or not and we want our *Lenses
    // class to match that
    final boolean isPublic = recordElement.getModifiers()
        .contains(Modifier.PUBLIC);
    final String sourceUnqualifiedName = recordElement.getSimpleName()
        .toString();
    classDefinition(
        sb,
        lensesClassName,
        isPublic,
        sourceUnqualifiedName,
        components
    );

    try (Writer writer = file.openWriter()) {
      writer.write(sb.toString());
    }
  }

  private static SequencedMap<String, String> buildComponentsNameTypeMapping(
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
            rce -> boxedType(rce.getAccessor().getReturnType().toString()),
            (a, b) -> {
              throw new IllegalStateException("Duplicate components?");
            },
            // Order matters because TypeElement#getRecordComponents provides
            // record components in the order used by the default record
            // constructor
            LinkedHashMap::new
        ));
  }

  private static void classHeader(
      StringBuilder sb,
      String enclosingPackage,
      String fullyQualifiedName
  ) {
    // package ${enclosingPackage};
    packageStatement(sb, enclosingPackage);

    sb.append('\n');

    // import ${FUNCTION_1_CLASS_NAME};
    importStatement(sb, FUNCTION_1_CLASS_NAME);
    // import ${LENS_CLASS_NAME};
    importStatement(sb, LENS_CLASS_NAME);
    // import ${fullyQualifiedName};
    importStatement(sb, fullyQualifiedName);

    sb.append('\n');
  }

  private static void classDefinition(
      StringBuilder sb,
      String className,
      boolean isPublic,
      String sourceName,
      SequencedMap<String, String> components
  ) {
    // Needs to be ordered to match the record constructor
    final SequencedSet<String> componentNames = components.sequencedKeySet();

    // [public] final class ${className}Lenses {
    classDefinitionOpening(sb, className, isPublic);

    //   public static final Lens<...> ... = ...;
    components.forEach((name, type) ->
        lensField(sb, name, type, sourceName, componentNames));

    // }
    sb.append("}\n");
  }

  private static void packageStatement(StringBuilder sb, String packageName) {
    sb.append("package ")
        .append(packageName)
        .append(";\n");
  }

  private static void importStatement(StringBuilder sb, String nameToImport) {
    sb.append("import ")
        .append(nameToImport)
        .append(";\n");
  }

  private static void classDefinitionOpening(
      StringBuilder sb,
      String className,
      boolean isPublic
  ) {
    if (isPublic) {
      sb.append("public ");
    }
    sb.append("final class ")
        .append(className)
        .append(" {\n\n")
        .append("  private ")
        .append(className)
        .append("() {\n  }\n");
  }

  private static void lensField(
      StringBuilder sb,
      String targetName,
      String targetType,
      String sourceType,
      SequencedSet<String> componentNames
  ) {
    final String overNewInstanceArguments = componentNames.stream()
        .map(compName -> {
          final String accessorInvocation = "source." + compName + "()";
          return compName.equals(targetName)
              ? "lift.apply(" + accessorInvocation + ")"
              : accessorInvocation;
        })
        .collect(Collectors.joining(", "));
    sb.append("\n");
    sb.append(String.format("""
              public static final Lens<%1$s, %1$s, %2$s, %2$s> %3$s = new Lens<>() {
                @Override
                public %2$s view(%1$s source) {
                  return source.%3$s();
                }

                @Override
                public %1$s over(Function1<%2$s, %2$s> lift, %1$s source) {
                  return new %1$s(%4$s);
                }
              };
            """, // Lower indentation on purpose!
        sourceType,              // 1: S, T
        targetType,              // 2: A, B
        targetName,              // 3: target component name
        overNewInstanceArguments // 4: Arguments for the new instance in over
    ));
  }

  /**
   * Return boxed version of the given type, if it's a primitive, or the type
   * itself.
   */
  private static String boxedType(String type) {
    return switch (type) {
      case "boolean" -> "Boolean";
      case "byte" -> "Byte";
      case "short" -> "Short";
      case "char" -> "Character";
      case "int" -> "Integer";
      case "long" -> "Long";
      case "float" -> "Float";
      case "double" -> "Double";
      // Remove "java.lang." prefix (first 10 chars)
      case String s when IS_IN_BASE_PKG.matcher(s).matches() -> s.substring(10);
      default -> type;
    };
  }
}
