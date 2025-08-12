/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.function.Function1;
import unf.function.Function2;
import unf.maybe.Just;
import unf.maybe.Maybe;
import unf.maybe.Nothing;

/**
 * An AffineFold is a {@link Fold} that contains at most one element, or a
 * {@link Getter} where the function may be partial.
 *
 * @param <S> Source of the traversal
 * @param <A> Target of the traversal
 */
@FunctionalInterface
public interface AffineFold<S, A> extends Fold<S, A> {

  /* https://hackage.haskell.org/package/optics-core-0.4/docs/Optics-AffineFold.html */

  /**
   * Retrieve the targeted value.
   */
  Maybe<A> preview(S source);

  @Override
  default <R> R foldMap(R neutralElement, Function2<R, R, R> reducer, Function1<A, R> map, S source) {
    return preview(source).fold(
        it -> reducer.apply(neutralElement, map.apply(it)),
        () -> neutralElement
    );
  }

  /**
   * Combine with another AffineFold.
   */
  default <U> AffineFold<S, U> focus(AffineFold<A, U> other) {
    return source -> foldMap(
        new Nothing<>(),
        (acc, it) -> acc.fold(Just::new, () -> it),
        other::preview,
        source
    );
  }
}
