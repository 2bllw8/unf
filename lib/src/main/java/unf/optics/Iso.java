/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.function.Function1;
import unf.function.Function2;

/**
 * An Isomorphism expresses the fact that two types have the same structure, and
 * hence can be converted from one to the other in either direction.
 *
 * @param <S> Source of the isomorphism
 * @param <T> Modified source of the isomorphism
 * @param <A> Field of the isomorphism
 * @param <B> Modified field of the isomorphism
 */
public interface Iso<S, T, A, B>
    extends Lens<S, T, A, B>, Prism<S, T, A, B>, Review<T, B> {

  /* https://hackage.haskell.org/package/optics-core-0.4/docs/Optics-Iso.html */

  @Override
  default <R> R foldMap(R neutralElement, Function2<R, R, R> reducer, Function1<A, R> map, S source) {
    return matching(source).fold($ -> neutralElement, map);
  }

  @Override
  default T over(Function1<A, B> lift, S source) {
    return review(lift.apply(view(source)));
  }

  @Override
  default T set(B value, S source) {
    return review(value);
  }

  /**
   * Combine with another Iso.
   */
  default <A2, B2> Iso<S, T, A2, B2> focus(Iso<A, B, A2, B2> other) {
    return new Iso<>() {
      @Override
      public A2 view(S source) {
        return other.view(Iso.this.view(source));
      }

      @Override
      public T review(B2 value) {
        return Iso.this.review(other.review(value));
      }
    };
  }
}
