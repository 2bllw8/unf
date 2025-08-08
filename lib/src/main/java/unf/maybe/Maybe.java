/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.maybe;

import java.util.stream.Stream;
import unf.function.Function0;
import unf.function.Function1;

/**
 * Maybe type is used to handle errors.
 */
public sealed interface Maybe<T> permits Just, Nothing {

  /**
   * Apply a function to this option.
   *
   * @return Returns the result of applying the given function to the value of
   * this option if this option is non-empty.
   */
  <S> Maybe<S> flatMap(Function1<T, Maybe<S>> f);

  /**
   * Apply a function to this option.
   *
   * @return Returns an option containing the result of applying f to this
   * option's value if this option is non-empty.
   */
  <S> Maybe<S> map(Function1<T, S> f);

  /**
   * Obtain a value depending on whether this option is empty or not.
   *
   * @return Returns the result of applying the someCase function to the value
   * of this option if this option is non-empty or noneCase if this option is
   * empty.
   */
  <S> S fold(Function1<T, S> someCase, Function0<S> noneCase);

  /**
   * Determine whether this option is empty.
   */
  boolean isEmpty();

  /**
   * Produce a stream.
   *
   * @return An empty stream if this option is empty or a stream with a single
   * value if this option is non-empty.
   */
  Stream<T> stream();
}
