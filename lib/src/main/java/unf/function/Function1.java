/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.function;


/**
 * Function object that takes one argument.
 *
 * @param <A> Type of the argument of the function.
 * @param <B> Type of the value returned by the function.
 */
@FunctionalInterface
public interface Function1<A, B> {

  B apply(A p0);

  default <C> Function1<A, C> compose(Function1<B, C> other) {
    return p0 -> other.apply(apply(p0));
  }

  /**
   * Returns a function that always returns its input argument.
   *
   * @param <T> the type of both the input and output of the function
   */
  static <T> Function1<T, T> identity() {
    return x -> x;
  }
}
