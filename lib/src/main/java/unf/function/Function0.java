/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.function;

/**
 * A Function object that takes no argument.
 *
 * @param <A> Type of the value returned by the function.
 */
@FunctionalInterface
public interface Function0<A> {

  A apply();
}
