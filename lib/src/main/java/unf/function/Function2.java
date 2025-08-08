/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.function;

/**
 * Function object that takes two arguments.
 *
 * @param <A> Type of the first argument of the function.
 * @param <B> Type of the second argument of the function.
 * @param <C> Type of the value returned by the function.
 */
@FunctionalInterface
public interface Function2<A, B, C>
    extends Function1<A, Function1<B, C>> {

  C apply(A p0, B p1);

  @Override
  default Function1<B, C> apply(A p0) {
    return p1 -> apply(p0, p1);
  }

  /**
   * @hidden
   */
  static <A, B, C> Function2<A, B, C> uncurry(Function1<A, Function1<B, C>> f) {
    return (p0, p1) -> f.apply(p0).apply(p1);
  }
}
