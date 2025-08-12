/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.either.Either;
import unf.either.Left;
import unf.either.Right;
import unf.function.Function1;
import unf.function.Function2;
import unf.maybe.Maybe;

/**
 * A Prism generalises the notion of a constructor (just as a {@link Lens}
 * generalises the notion of a field).
 *
 * @param <S> Source of the prism
 * @param <T> Modified source of the prism
 * @param <A> Field of the prism
 * @param <B> Modified field of the prism
 */
public interface Prism<S, T, A, B>
    extends AffineTraversal<S, T, A, B>, Review<T, B> {

  /* https://hackage.haskell.org/package/optics-core-0.4/docs/Optics-Prism.html */

  @Override
  default <R> R foldMap(R neutralElement, Function2<R, R, R> reducer, Function1<A, R> map, S source) {
    return getOrModify(source).fold($ -> neutralElement, map);
  }

  @Override
  default T over(Function1<A, B> lift, S source) {
    return getOrModify(source).fold(Function1.identity(),
        r -> review(lift.apply(r)));
  }

  @Override
  default T set(B value, S source) {
    return over($ -> value, source);
  }

  /**
   * Combine with another Prism.
   */
  default <A2, B2> Prism<S, T, A2, B2> focus(Prism<A, B, A2, B2> other) {
    return new Prism<>() {
      @Override
      public Either<T, A2> getOrModify(S source) {
        return Prism.this.getOrModify(source).fold(
            Left::new,
            a -> other.getOrModify(a).fold(
                b -> new Left<>(Prism.this.set(b, source)),
                Right::new
            )
        );
      }

      @Override
      public Maybe<A2> preview(S source) {
        return Prism.this.preview(source).flatMap(other::preview);
      }

      @Override
      public T review(B2 value) {
        return Prism.this.review(other.review(value));
      }
    };
  }
}
