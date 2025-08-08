/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.function;

/**
 * Function object that takes four arguments.
 *
 * @param <A> Type of the first argument of the function.
 * @param <B> Type of the second argument of the function.
 * @param <C> Type of the third argument of the function.
 * @param <D> Type of the fourth argument of the function.
 * @param <E> Type of the value returned by the function.
 */
@FunctionalInterface
public interface Function4<A, B, C, D, E>
    extends Function3<A, B, C, Function1<D, E>> {

  E apply(A p0, B p1, C p2, D p3);

  @Override
  default Function1<D, E> apply(A p0, B p1, C p2) {
    return p3 -> apply(p0, p1, p2, p3);
  }

  /**
   * @hidden
   */
  static <A, B, C, D, E> Function4<A, B, C, D, E> uncurry(Function1<A, Function1<B, Function1<C, Function1<D, E>>>> f) {
    return (p0, p1, p2, p3) -> f.apply(p0).apply(p1).apply(p2).apply(p3);
  }
}
