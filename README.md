# unF

[![GitHub CI](https://github.com/2bllw8/unf/actions/workflows/main.yml/badge.svg)](https://github.com/2bllw8/unf/actions/workflows/main.yml)
[![Maven Central](https://img.shields.io/maven-central/v/cc.chokoka/unf.lib)](https://search.maven.org/artifact/cc.chokoka/unf.lib)

A functional programming library for modern Java (21+).

## Key features

- **Optics**: a first-class, composable notion of substructure.
  - Annotation Processor: automatically generate optics for record classes
    - For each record component, generate a `Lens`
    - For each record component of type `List`, also generate a `Traversal`
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
  implementation "cc.chokoka:unf.lib:2.0.0"
  // Optional annotation processor for automatic lens generation for record
  // classes
  annotationProcessor "cc.chokoka:unf.processor:2.0.0"
}
```

### Demo: Record Optics

```java
import unf.maybe.Just;
import unf.maybe.Maybe;
import unf.optics.RecordOptics;

@RecordOptics
record Rec1(int i, Maybe<String> s) {
}

@RecordOptics
record Rec0(Rec1 r, boolean b) {
}

public final class Demo {

  public static void main(String[] args) {
    final Rec0 r = new Rec0(new Rec1(1, new Just<>("one")), true);
    final Rec0 newR = Rec0Optics.r.i.over(i -> i + 2, r);

    System.out.println(newR);
    // Rec0[r=Rec1[i=3, s="one"], b=true]
  }
}
```

## Changelog

See the [CHANGELOG.md](./CHANGELOG.md) file.

## Development

- Build: `./gradlew assemble`
- Test: `./gradlew check`

### Project Structure

- `lib`: main module, the library itself.
- `plugin`: annotation processor that enables automatic generation of `Lens`
  instances for `record` classes.
