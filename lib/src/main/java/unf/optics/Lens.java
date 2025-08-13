/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.either.Either;
import unf.either.Right;
import unf.function.Function1;
import unf.function.Function2;

/**
 * A Lens is a generalised or first-class field.
 *
 * <p>It combines a {@link Getter} and a {@link Setter}.
 *
 * @param <S> Source of the lens
 * @param <T> Modified source of the lens
 * @param <A> Field of the lens
 * @param <B> Modified field of the lens
 */
public interface Lens<S, T, A, B>
    extends AffineTraversal<S, T, A, B>, Getter<S, A> {

  /* https://hackage.haskell.org/package/optics-core-0.4/docs/Optics-Lens.html */

  @Override
  T over(Function1<A, B> lift, S source);

  @Override
  default Either<T, A> matching(S source) {
    return new Right<>(view(source));
  }

  @Override
  default <R> R foldMap(R neutralElement, Function2<R, R, R> reducer, Function1<A, R> map, S source) {
    return matching(source).fold($ -> neutralElement, map);
  }

  @Override
  default T set(B value, S source) {
    return over($ -> value, source);
  }

  /**
   * Combine with another Lens.
   */
  default <A2, B2> Lens<S, T, A2, B2> focus(Lens<A, B, A2, B2> other) {
    return new Lens<>() {
      @Override
      public A2 view(S source) {
        return other.view(Lens.this.view(source));
      }

      @Override
      public T over(Function1<A2, B2> lift, S source) {
        return Lens.this.over(a -> other.over(lift, a), source);
      }
    };
  }
}
