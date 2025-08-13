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
      PRISM_JUST = new Prism<>() {
    @Override
    public Either<Maybe<String>, String> matching(Maybe<String> source) {
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
      PRISM_LEFT = new Prism<>() {
    @Override
    public Either<Either<String, Integer>, String> matching(Either<String, Integer> source) {
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
    final String s = "◕ ◡ ◕";

    Assert.assertEquals(
        new Just<>(s),
        PRISM_JUST.review(s)
    );
  }

  @Test
  public void maybeJustPreviewJust() {
    final String s = "●_●";

    Assert.assertEquals(
        new Just<>(s),
        PRISM_JUST.preview(new Just<>(s))
    );
  }

  @Test
  public void maybeJustPreviewNothing() {
    Assert.assertEquals(
        new Nothing<>(),
        PRISM_JUST.preview(new Nothing<>())
    );
  }

  @Test
  public void eitherLeftReviewLeft() {
    final String s = "(>_<)";

    Assert.assertEquals(
        new Left<>(s),
        PRISM_LEFT.review(s)
    );
  }

  @Test
  public void eitherLeftPreviewLeft() {
    final String s = "ʘ︵ʘ";

    Assert.assertEquals(
        new Just<>(s),
        PRISM_LEFT.preview(new Left<>(s))
    );
  }

  @Test
  public void eitherLeftPreviewRight() {
    Assert.assertEquals(
        new Nothing<>(),
        PRISM_LEFT.preview(new Right<>(1))
    );
  }
}
