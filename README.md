# unF

A functional programming library for modern Java (21+).

## Key features

- **Optics**: a first-class, composable notion of substructure.
- **Either** monad: represent values with two possibilities.
- **Maybe** monad: encapsulate an optional value.
- **Function** interfaces with partial application.

## Usage

```groovy
dependencies {
  implementation cc.chokoka.unf.lib
  annotationProcessor cc.chokoka.unf.processor
}
```

## Development

Build:

```shell
./gradlew assemble
```

Test:

```shell
./gradlew check
```

## Structure

- `lib`: main module, the library itself.
- `plugin`: annotation processor that enables automatic generation of `Lens`
  instances for `record` classes.
- `example`: exemplar usage of the library
