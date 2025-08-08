/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.maybe.Just;
import unf.maybe.Maybe;

/**
 * A Getter is simply a function considered as an Optic.
 *
 * @param <S> Source of the getter
 * @param <A> Target of the getter
 */
@FunctionalInterface
public interface Getter<S, A> extends AffineFold<S, A> {

  /**
   * View the value pointed to by a getter.
   */
  A view(S source);

  @Override
  default Maybe<A> preview(S source) {
    return new Just<>(view(source));
  }

  /**
   * Combine this getter with another one.
   */
  default <A2> Getter<S, A2> then(Getter<A, A2> other) {
    return source -> other.view(view(source));
  }
}
