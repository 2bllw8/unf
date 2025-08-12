/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

/**
 * A Review is a backwards {@link Getter}, i.e. a function {@code B -> T}.
 *
 * @param <B> Source of the review
 * @param <T> Target of the review
 */
@FunctionalInterface
public interface Review<T, B> {

  /* https://hackage.haskell.org/package/optics-core-0.4/docs/Optics-Review.html */

  /*
   * Note on the lack of a Review#focus(Review) method.
   *
   * Review is a FunctionalInterface. This means that the focus method becomes
   * potentially ambiguous with AffineFold#focus(AffineFold) in Prism for the
   * compiler (both are functional interfaces with 1 parameter and the type
   * of that parameter is an unbounded generic).
   * We chose to forsake the "focus" of Review because it's less likely going to
   * be used than the "focus" of AffineFold. If needed it can be easily
   * implemented by the user.
   */

  /**
   * Retrieve the value targeted by a Review.
   */
  T review(B value);
}
