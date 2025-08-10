/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.function.Function1;

/**
 * A Setter has the ability to lift a function of type {@code A -> B} over a
 * function of type {@code S -> T}, applying the function to update all the
 * {@code A}s contained in {@code S}.
 *
 * <p>This can be used to set all the {@code A}s to a single value
 * (by lifting a constant function).
 *
 * @param <S> Source of the setter
 * @param <T> Modified source of the setter
 * @param <A> Target of the setter
 * @param <B> Modified target of the setter
 */
@FunctionalInterface
public interface Setter<S, T, A, B> {

  /**
   * Apply a setter as a modifier.
   */
  T over(Function1<A, B> lift, S source);

  /**
   * Apply a setter.
   */
  default T set(B value, S source) {
    return over($ -> value, source);
  }

  /**
   * Combine this Setter with another one.
   */
  default <A2, B2> Setter<S, T, A2, B2> focus(Setter<A, B, A2, B2> other) {
    return (lift, source) -> over(a -> other.over(lift, a), source);
  }
}
