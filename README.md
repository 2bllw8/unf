# unF

[![GitHub CI](https://github.com/2bllw8/unf/actions/workflows/main.yml/badge.svg)](https://github.com/2bllw8/unf/actions/workflows/main.yml)
[![Maven Central](https://img.shields.io/maven-central/v/cc.chokoka/unf.lib)](https://search.maven.org/artifact/cc.chokoka/unf.lib)

A functional programming library for modern Java (21+).

## Key features

- **Optics**: a first-class, composable notion of substructure.
  - With an optional annotation processor to automatically generate lenses for
    record classes.
- **Either** monad: represent values with two possibilities.
- **Maybe** monad: encapsulate an optional value.
- **Function** interfaces with partial application.

## Usage

- Available on
  [Maven Central](https://central.sonatype.com/artifact/cc.chokoka/unf.lib)
- JavaDoc available on [GitHub pages](https://2bllw8.github.io/unf)

```groovy
dependencies {
  // Core library
  implementation "cc.chokoka:unf.lib:1.0.0"
  // Optional annotation processor for automatic lens generation for record
  // classes
  annotationProcessor "cc.chokoka:unf.processor:1.0.0"
}
```

### Demo: Record Lenses

```java
import unf.maybe.Just;
import unf.maybe.Maybe;
import unf.optics.RecordLenses;

@RecordLenses
record Rec1(int i, Maybe<String> s) {
}

@RecordLenses
record Rec0(Rec1 r, boolean b) {
}

public final class Demo {

  public static void main(String[] args) {
    final Rec0 r = new Rec0(new Rec1(1, new Just<>("one")), true);
    final Rec0 newR = Rec0Lenses.r.i.over(i -> i + 2, r);

    System.out.println(newR);
    // Rec0[r=Rec1[i=3, s="one"], b=true]
  }
}
```

## Development

- Build: `./gradlew assemble`
- Test: `./gradlew check`

### Project Structure

- `lib`: main module, the library itself.
- `plugin`: annotation processor that enables automatic generation of `Lens`
  instances for `record` classes.
