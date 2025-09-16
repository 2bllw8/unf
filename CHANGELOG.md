# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

- ...

## [2.0.0] - 2025-09-16

### Added

- Annotation processor now generates `Traversal` for record components of type
  `List`. The generated `Traversal` goes from the component of record `S` of
  type `List<A>` to a `Lens<S, T, A, B>`, allowing to easily compose and operate
  on individual elements of the list within other lenses.

### Changed

- Fix inconsistent name of `focus` methods
  - `Getter#then` -> `Getter#focus`
  - `Traversal#then` -> `Traversal#focus`
- `RecordLenses` renamed to `RecordOptics`
- Generated classes now have the `*Optics` suffix rather than `*Lenses` suffix

## [1.0.0] - 2025-08-13

- Initial release
