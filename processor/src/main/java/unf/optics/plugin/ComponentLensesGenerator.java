package unf.optics.plugin;

import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

final class ComponentLensesGenerator {

  private ComponentLensesGenerator() {
  }

  /**
   * Generate lenses for each record component.
   */
  public static List<String> lensesForComponents(
      Types types,
      TypeMirror targetRecordType,
      SequencedMap<String, TypeMirror> allComponents,
      SequencedSet<String> allComponentNames
  ) {
    return allComponents.sequencedEntrySet()
        .stream()
        .map(e -> lensForComponent(
            types,
            targetRecordType,
            e.getKey(),
            e.getValue(),
            allComponentNames
        )).toList();
  }

  /**
   * Generate a lens for a given record component.
   */
  private static String lensForComponent(
      Types types,
      TypeMirror sourceType,
      String targetName,
      TypeMirror targetType,
      SequencedSet<String> allComponentNames
  ) {
    final String sourceTypeStr = Utils.formatType(types, sourceType);
    final String componentTypeStr = Utils.formatType(types, targetType);
    final String overImpl = Utils.newRecordInstanceExpr(
        sourceTypeStr,
        targetName,
        allComponentNames,
        accessor -> "lift.apply(" + accessor + ")"
    );
    return String.format("""
              public static final Lens<%1$s, %1$s, %2$s, %2$s> %3$s = new Lens<>() {
                @Override
                public %1$s over(Function1<%2$s, %2$s> lift, %1$s source) {
                  return %4$s;
                }

                @Override
                public %2$s view(%1$s source) {
                  return source.%3$s();
                }
              };
            """, // Lower indentation on purpose!
        sourceTypeStr,    // S, T
        componentTypeStr, // A, B
        targetName,       // 3: target component name
        overImpl          // 4: new instance in over
    );
  }
}
