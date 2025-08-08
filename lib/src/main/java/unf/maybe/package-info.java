/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * The {@link unf.maybe.Maybe} type encapsulates an optional value.
 *
 * <p>A value of type {@code Maybe<A, B>} either contains a value of type
 * {@code A} (represented as {@code Just<A>}), or it is empty (represented as
 * {@code Nothing<A>}). Using Maybe is a good way to deal with errors or
 * exceptional cases without resorting to drastic measures such as {@code null},
 * {@link java.lang.Exception}s or sentinels.
 *
 * <p>The Maybe type is also a monad. It is a simple kind of error monad, where
 * all errors are represented by {@code Nothing}. A richer error monad can be
 * built using the {@link unf.either.Either} type.
 *
 * @see unf.maybe.Maybe
 * @see unf.maybe.Just
 * @see unf.maybe.Nothing
 * @see unf.either.Either
 */
package unf.maybe;
