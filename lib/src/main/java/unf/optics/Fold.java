/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.function.Function1;
import unf.function.Function2;

/**
 * A Fold has the ability to extract some number of elements of type {@code A}
 * from a container of type {@code S}. For example, toListOf can be used to
 * obtain the contained elements as a list.
 *
 * <p>Unlike a {@link Traversal}, there is no way to set or update elements.
 *
 * @param <S> Container source of the traversal
 * @param <A> Target of the traversal
 */
public interface Fold<S, A> {

  /**
   * Map each target to {@code R} and fold the results.
   */
  <R> R foldMap(R neutralElement, Function2<R, R, R> reducer, Function1<A, R> map, S source);

  /**
   * Fold the targets.
   */
  default A fold(A neutralElement, Function2<A, A, A> reducer, S source) {
    return foldMap(neutralElement, reducer, Function1.identity(), source);
  }

  /**
   * Combine with another Focus.
   */
  default <U> Fold<S, U> focus(Fold<A, U> other) {
    return new Fold<>() {
      @Override
      public <R> R foldMap(R neutralElement, Function2<R, R, R> reducer, Function1<U, R> map, S source) {
        return Fold.this.foldMap(
            neutralElement,
            reducer,
            a -> other.foldMap(neutralElement, reducer, map, a),
            source
        );
      }
    };
  }
}
