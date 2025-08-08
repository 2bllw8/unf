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
 * A {@link Maybe} containing some value.
 */
public record Just<T>(T value) implements Maybe<T> {

  @Override
  public <S> Maybe<S> flatMap(Function1<T, Maybe<S>> f) {
    return f.apply(value);
  }

  @Override
  public <S> Maybe<S> map(Function1<T, S> f) {
    return new Just<>(f.apply(value));
  }

  @Override
  public <S> S fold(Function1<T, S> someCase, Function0<S> noneCase) {
    return someCase.apply(value);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Stream<T> stream() {
    return Stream.of(value);
  }
}
