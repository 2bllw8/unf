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
 * A {@link Maybe} containing no value.
 */
public record Nothing<T>() implements Maybe<T> {

  @Override
  public <S> Maybe<S> flatMap(Function1<T, Maybe<S>> f) {
    return new Nothing<>();
  }

  @Override
  public <S> Maybe<S> map(Function1<T, S> f) {
    return new Nothing<>();
  }

  @Override
  public <S> S fold(Function1<T, S> someCase, Function0<S> noneCase) {
    return noneCase.apply();
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public Stream<T> stream() {
    return Stream.empty();
  }
}
