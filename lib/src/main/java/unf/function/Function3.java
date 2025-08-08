/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.function;

/**
 * Function object that takes three arguments.
 *
 * @param <A> Type of the first argument of the function.
 * @param <B> Type of the second argument of the function.
 * @param <C> Type of the third argument of the function.
 * @param <D> Type of the value returned by the function.
 */
@FunctionalInterface
public interface Function3<A, B, C, D>
    extends Function2<A, B, Function1<C, D>> {

  D apply(A p0, B p1, C p2);

  @Override
  default Function1<C, D> apply(A p0, B p1) {
    return p2 -> apply(p0, p1, p2);
  }

  /**
   * @hidden
   */
  static <A, B, C, D> Function3<A, B, C, D> uncurry(Function1<A, Function1<B, Function1<C, D>>> f) {
    return (p0, p1, p2) -> f.apply(p0).apply(p1).apply(p2);
  }
}
