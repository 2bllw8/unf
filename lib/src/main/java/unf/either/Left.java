/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.either;

import java.util.stream.Stream;
import unf.function.Function1;

/**
 * An {@link Either} with a left value.
 */
public record Left<L, R>(L value) implements Either<L, R> {

  @Override
  public <T> T fold(Function1<L, T> leftCase, Function1<R, T> rightCase) {
    return leftCase.apply(value);
  }

  @Override
  public <T> Either<T, R> mapLeft(Function1<L, T> mapper) {
    return new Left<>(mapper.apply(value));
  }

  @Override
  public <T> Either<L, T> mapRight(Function1<R, T> mapper) {
    return new Left<>(value);
  }

  @Override
  public <T> Either<T, R> flatMapLeft(Function1<L, Either<T, R>> mapper) {
    return mapper.apply(value);
  }

  @Override
  public <T> Either<L, T> flatMapRight(Function1<R, Either<L, T>> mapper) {
    return new Left<>(value);
  }

  @Override
  public Stream<L> streamLeft() {
    return Stream.of(value);
  }

  @Override
  public Stream<R> streamRight() {
    return Stream.empty();
  }

  @Override
  public boolean isRight() {
    return false;
  }
}
