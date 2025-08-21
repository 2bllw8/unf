/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics.plugin;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnknownTypeException;
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

  private static final Pattern IS_IN_BASE_PKG = Pattern.compile(
      "^java\\.lang\\.\\w+$");

  private LensesGenerator() {
  }

  public static void generate(
      Elements elements,
      Types types,
      TypeElement recordElement,
      JavaFileObject file,
      String enclosingPackage,
      String lensesClassName
  ) throws IOException {
    final SequencedMap<String, String> components
        = buildComponentsNameTypeMapping(recordElement);
    final SequencedMap<String, String> listComponents =
        buildListComponentsNameTypeMapping(
            elements,
            types,
            recordElement
        );

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
        components,
        listComponents
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
            rce -> formatType(rce.getAccessor().getReturnType()),
            (a, b) -> {
              throw new IllegalStateException("Duplicate components?");
            },
            // Order matters because TypeElement#getRecordComponents provides
            // record components in the order used by the default record
            // constructor
            LinkedHashMap::new
        ));
  }

  private static SequencedMap<String, String>
  buildListComponentsNameTypeMapping(
      Elements elements,
      Types types,
      TypeElement recordElement
  ) {
    // Need to erase, otherwise a type like List<String> would not be
    // considered assignable to it.
    final TypeMirror listTypeMirror = types.erasure(
        elements.getTypeElement(List.class.getTypeName()).asType()
    );
    return recordElement.getRecordComponents()
        .stream()
        .filter(rce -> listTypeMirror.equals(
            types.erasure(rce.getAccessor().getReturnType())
        ))
        .collect(Collectors.toMap(
            rce -> rce.getSimpleName().toString(),
            rce -> formatType(
                getFirstTypeParameter(rce.getAccessor().getReturnType())
            ),
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
    // import ${FUNCTION_2_CLASS_NAME};
    importStatement(sb, FUNCTION_2_CLASS_NAME);
    // import ${LENS_CLASS_NAME};
    importStatement(sb, LENS_CLASS_NAME);
    // import ${TRAVERSAL_CLASS_NAME};
    importStatement(sb, TRAVERSAL_CLASS_NAME);
    // import ${fullyQualifiedName};
    importStatement(sb, fullyQualifiedName);
    // import java.util.List;
    importStatement(sb, List.class.getName());

    sb.append('\n');
  }

  private static void classDefinition(
      StringBuilder sb,
      String className,
      boolean isPublic,
      String sourceName,
      SequencedMap<String, String> components,
      SequencedMap<String, String> listComponents
  ) {
    // Needs to be ordered to match the record constructor
    final SequencedSet<String> componentNames = components.sequencedKeySet();

    // [public] final class ${className}Lenses {
    classDefinitionOpening(sb, className, isPublic);

    //   public static final Lens<...> ... = ...;
    components.forEach((name, type) ->
        lensField(sb, name, type, sourceName, componentNames));

    // public static final Traversal<...> ...Elements = ....;
    listComponents.forEach((name, types) ->
        traversalField(sb, name, types, sourceName, componentNames));

    if (!listComponents.isEmpty()) {
      // private static <T> Lens<List<T>, List<T>, T, T> lensAtIndex(int i)...
      lensAtIndex(sb);
    }

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

  private static void traversalField(
      StringBuilder sb,
      String targetName,
      String elementType,
      String sourceType,
      SequencedSet<String> componentNames
  ) {
    final String overNewInstanceArguments = componentNames.stream()
        .map(compName -> {
          final String accessorInvocation = "source." + compName + "()";
          return compName.equals(targetName)
              ? "newValue"
              : accessorInvocation;
        })
        .collect(Collectors.joining(", "));
    sb.append("\n");
    sb.append(String.format("""
              public static final Traversal<%1$s, %1$s, Lens<%1$s, %1$s, %2$s, %2$s>, %2$s> %3$sElements = new Traversal<>() {
                @Override
                public %1$s over(Function1<Lens<%1$s, %1$s, %2$s, %2$s>, %2$s> lift,
                                 %1$s source) {
                  final List<%2$s> oldValue = source.%3$s();
                  final List<%2$s> newValue = java.util.stream.IntStream.range(0, oldValue.size())
                      .mapToObj(i -> lift.apply(%3$s.focus(lensAtIndex(i))))
                      .toList();
                  return new %1$s(%4$s);
                }

                @Override
                public <R> R foldMap(R neutralElement,
                                     Function2<R, R, R> reducer,
                                     Function1<Lens<%1$s, %1$s, %2$s, %2$s>, R> map,
                                     %1$s source) {
                  R result = neutralElement;
                  final int n = source.%3$s().size();
                  int i = 0;
                  while (i < n) {
                    result = reducer.apply(result, map.apply(%3$s.focus(lensAtIndex(i))));
                    i++;
                  }
                  return result;
                }
              };
            """, // Lower indentation on purpose!
        sourceType,              // 1: S, T
        elementType,             // 2. A, B
        targetName,              // 3: target component name
        overNewInstanceArguments // 4: Arguments for the new instance in over
    ));
  }

  private static void lensAtIndex(StringBuilder sb) {
    sb.append("\n");
    sb.append("""
          private static <T> Lens<List<T>, List<T>, T, T> lensAtIndex(int idx) {
            return new Lens<>() {
              @Override
              public List<T> over(Function1<T, T> lift, List<T> source) {
                return java.util.stream.IntStream.range(0, source.size())
                  .mapToObj(i -> i == idx
                      ? lift.apply(source.get(i))
                      : source.get(i))
                   .toList();
              }

              @Override
              public T view(List<T> source) {
                return source.get(idx);
              }
            };
          }
        """); // Lower indentation on purpose!
  }

  /**
   * Get the {@link TypeMirror} of the first type parameter of the give
   * {@link TypeMirror}.
   */
  private static TypeMirror getFirstTypeParameter(TypeMirror listTypeMirror) {
    return Optional.of(listTypeMirror)
        // Safely cast to DeclaredType
        .filter(DeclaredType.class::isInstance)
        .map(DeclaredType.class::cast)
        // Get the only type parameter of List to figure out the type
        // of the elements of the list
        .map(DeclaredType::getTypeArguments)
        .flatMap(args -> args.isEmpty()
            ? Optional.empty()
            : Optional.of(args.getFirst()))
        .orElseThrow(() -> new UnknownTypeException(
            listTypeMirror,
            "Cannot determine type of the elements of " + listTypeMirror
        ));
  }

  /**
   * Return boxed version of the given type, if it's a primitive, or the type
   * itself.
   */
  private static String formatType(TypeMirror type) {
    return switch (type.toString()) {
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
      default -> type.toString();
    };
  }
}
