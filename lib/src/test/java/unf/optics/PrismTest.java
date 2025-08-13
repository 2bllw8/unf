/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import org.junit.Assert;
import org.junit.Test;
import unf.either.Either;
import unf.either.Left;
import unf.either.Right;
import unf.maybe.Just;
import unf.maybe.Maybe;
import unf.maybe.Nothing;


public final class PrismTest {

  private static final Prism<Maybe<String>, Maybe<String>, String, String>
      JUST_PRISM = new Prism<>() {
    @Override
    public Either<Maybe<String>, String> getOrModify(Maybe<String> source) {
      return source.fold(Right::new, () -> new Left<>(source));
    }

    @Override
    public Maybe<String> preview(Maybe<String> source) {
      return source;
    }

    @Override
    public Maybe<String> review(String value) {
      return new Just<>(value);
    }
  };

  private static final Prism<Either<String, Integer>, Either<String, Integer>, String, String>
      LEFT_PRISM = new Prism<>() {
    @Override
    public Either<Either<String, Integer>, String> getOrModify(Either<String, Integer> source) {
      return source.fold(
          Right::new,
          r -> new Left<>(source)
      );
    }

    @Override
    public Maybe<String> preview(Either<String, Integer> source) {
      return source.fold(Just::new, l -> new Nothing<>());
    }

    @Override
    public Either<String, Integer> review(String value) {
      return new Left<>(value);
    }
  };

  @Test
  public void maybeJustReviewJust() {
    Assert.assertEquals(
        new Just<>("Flower"),
        JUST_PRISM.review("Flower")
    );
  }

  @Test
  public void maybeJustPreviewJust() {
    Assert.assertEquals(
        new Just<>("Bird"),
        JUST_PRISM.preview(new Just<>("Bird"))
    );
  }

  @Test
  public void maybeJustPreviewNothing() {
    Assert.assertEquals(
        new Nothing<>(),
        JUST_PRISM.preview(new Nothing<>())
    );
  }

  @Test
  public void eitherLeftReviewLeft() {
    Assert.assertEquals(
        new Left<>("Hello"),
        LEFT_PRISM.review("Hello")
    );
  }

  @Test
  public void eitherLeftPreviewLeft() {
    Assert.assertEquals(
        new Just<>("World"),
        LEFT_PRISM.preview(new Left<>("World"))
    );
  }

  @Test
  public void eitherLeftPreviewRight() {
    Assert.assertEquals(
        new Nothing<>(),
        LEFT_PRISM.preview(new Right<>(1))
    );
  }
}
