package unf.optics.plugin;

import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

final class ComponentTraversalsGenerator {

  private static final String METHOD_LENS_AT_INDEX = """
        private static <T> Lens<List<T>, List<T>, T, T> lensAtIndex(int idx) {
          return new Lens<>() {
            @Override
            public List<T> over(Function1<T, T> lift, List<T> source) {
              return IntStream.range(0, source.size())
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
      """; // Lower indentation on purpose!

  private ComponentTraversalsGenerator() {
  }

  /**
   * Generate a traversal that allows to focus on a lens for each element of a
   * record component of type list.
   */
  public static List<String> traversalsForComponents(
      Types types,
      TypeMirror targetRecordType,
      SequencedMap<String, TypeMirror> allComponents,
      SequencedSet<String> allComponentNames
  ) {
    return allComponents.sequencedEntrySet()
        .stream()
        .map(e -> traversalForComponent(
            types,
            targetRecordType,
            e.getKey(),
            getFirstTypeArgument(e.getValue()),
            allComponentNames
        )).toList();
  }

  /**
   * Generate a traversal that allows to focus on a lens for a single element of
   * a record component of type list.
   */
  private static String traversalForComponent(
      Types types,
      TypeMirror sourceType,
      String targetName,
      TypeMirror targetElementType,
      SequencedSet<String> allComponentNames
  ) {
    final String sourceTypeStr = Utils.formatType(types, sourceType);
    final String componentElementTypeStr
        = Utils.formatType(types, targetElementType);
    final String overImpl = Utils.newRecordInstanceExpr(
        sourceTypeStr,
        targetName,
        allComponentNames,
        ignored -> "newValue"
    );
    return String.format("""
              public static final Traversal<%1$s, %1$s, Lens<%1$s, %1$s, %2$s, %2$s>, %2$s> %3$sElements = new Traversal<>() {
                @Override
                public %1$s over(Function1<Lens<%1$s, %1$s, %2$s, %2$s>, %2$s> lift,
                                 %1$s source) {
                  final List<%2$s> newValue = IntStream.range(0, source.%3$s().size())
                      .mapToObj(i -> lift.apply(%3$s.focus(lensAtIndex(i))))
                      .toList();
                  return %4$s;
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
                    result = reducer.apply(
                      result,
                      map.apply(%3$s.focus(lensAtIndex(i++)))
                    );
                  }
                  return result;
                }
              };
            """, // Lower indentation on purpose!
        sourceTypeStr,    // S, T
        componentElementTypeStr, // A, B
        targetName,       // 3: target component name
        overImpl          // 4: new instance in over
    );
  }

  /**
   * Get the code that implements the method "lensAtIndex(int)".
   */
  public static String getMethodLensAtIndexImpl() {
    return METHOD_LENS_AT_INDEX;
  }

  /**
   * Get the {@link TypeMirror} of the first type parameter of the given
   * {@link TypeMirror}.
   */
  private static TypeMirror getFirstTypeArgument(TypeMirror typeMirror) {
    if (typeMirror instanceof DeclaredType declaredType) {
      final List<? extends TypeMirror> typeArgs
          = declaredType.getTypeArguments();
      if (!typeArgs.isEmpty()) {
        return typeArgs.getFirst();
      }
    }
    throw new IllegalArgumentException(
        "Provided type " + typeMirror + " has no type parameter"
    );
  }
}
