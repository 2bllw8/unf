package unf.optics.plugin.generator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

final class ListComponentTraversalsGenerator extends OpticsGenerator {

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

  public ListComponentTraversalsGenerator(
      Types types,
      TypeMirror targetRecordType,
      SequencedMap<String, TypeMirror> components,
      SequencedSet<String> allComponentNames
  ) {
    super(types, targetRecordType, components, allComponentNames);
  }

  /**
   * Generate a traversal that allows to focus on a lens for each element of a
   * record component of type list.
   */
  @Override
  public List<String> generate() {
    final Stream<String> traversals = components.sequencedEntrySet()
        .stream()
        .map(e -> traversalForComponent(
            e.getKey(),
            getFirstTypeArgument(e.getValue())
        ));
    return Stream.concat(
        traversals,
        Stream.of(METHOD_LENS_AT_INDEX)
    ).toList();
  }

  @Override
  public Set<String> usedTypes() {
    final Set<String> typesSet = new HashSet<>();
    // unf types
    typesSet.add(Utils.FUNCTION_1_CLASS_NAME);
    typesSet.add(Utils.FUNCTION_2_CLASS_NAME);
    typesSet.add(Utils.LENS_CLASS_NAME);
    typesSet.add(Utils.TRAVERSAL_CLASS_NAME);
    // Java types
    typesSet.add(IntStream.class.getName());
    typesSet.add(List.class.getName());
    // Record types
    typesSet.add(types.erasure(targetRecordType).toString());
    for (final TypeMirror componentType : components.values()) {
      addTypesToSet(componentType, typesSet);
    }
    return Collections.unmodifiableSet(typesSet);
  }

  /**
   * Generate a traversal that allows to focus on a lens for a single element of
   * a record component of type list.
   */
  private String traversalForComponent(
      String targetName,
      TypeMirror targetElementType
  ) {
    final String sourceTypeStr = Utils.formatType(types, targetRecordType);
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
