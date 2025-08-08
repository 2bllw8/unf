/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package example.optics;

import unf.either.Either;
import unf.either.Left;
import unf.either.Right;
import unf.maybe.Just;
import unf.maybe.Maybe;
import unf.maybe.Nothing;
import unf.optics.Prism;

public final class PrismExample {

  public static void maybe() {
    final Prism<Maybe<String>, Maybe<String>, String, String>
        just = new Prism<>() {
      @Override
      public Either<Maybe<String>, String> getOrModify(Maybe<String> source) {
        return source.fold(Right::new, () -> new Left<>(source));
      }

      @Override
      public Maybe<String> preview(Maybe<String> source) {
        return source;
      }

      @Override
      public Maybe<String> review(String value) {
        return new Just<>(value);
      }
    };

    assert just.review("Flower")
        .equals(new Just<>("Flower"));
    assert just.preview(new Just<>("Bird"))
        .equals(new Just<>("Bird"));
    assert just.preview(new Nothing<>())
        .equals(new Nothing<>());
  }

  public static void either() {
    final Prism<Either<String, Integer>, Either<String, Integer>, String, String>
        left = new Prism<>() {
      @Override
      public Either<Either<String, Integer>, String> getOrModify(Either<String, Integer> source) {
        return source.fold(
            Right::new,
            r -> new Left<>(source)
        );
      }

      @Override
      public Maybe<String> preview(Either<String, Integer> source) {
        return source.fold(Just::new, l -> new Nothing<>());
      }

      @Override
      public Either<String, Integer> review(String value) {
        return new Left<>(value);
      }
    };

    assert left.review("Hello")
        .equals(new Left<>("Hello"));
    assert left.preview(new Left<>("World"))
        .equals(new Just<>("World"));
    assert left.preview(new Right<>(1))
        .equals(new Nothing<>());
  }

  private PrismExample() {
  }
}
