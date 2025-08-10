/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.optics;

import unf.either.Either;
import unf.either.Left;
import unf.either.Right;
import unf.function.Function1;
import unf.maybe.Maybe;

/**
 * An AffineTraversal is a Traversal that applies to at most one element.
 *
 * <p>These arise most frequently as the composition of a Lens with a Prism.
 *
 * @param <S> Source of the traversal
 * @param <T> Modified source of the traversal
 * @param <A> Target of the traversal
 * @param <B> Modified target of the traversal
 */
public interface AffineTraversal<S, T, A, B>
    extends AffineFold<S, A>, Traversal<S, T, A, B> {


  /**
   * Get the target or return the original value while allowing for the type to
   * change if it does not match.
   */
  Either<T, A> getOrModify(S source);

  @Override
  T set(B value, S source);

  @Override
  default T over(Function1<A, B> lift, S source) {
    return getOrModify(source).fold(
        Function1.identity(),
        r -> set(lift.apply(r), source)
    );
  }

  /**
   * Combine with another AffineTraversal.
   */
  default <A2, B2> AffineTraversal<S, T, A2, B2> focus(AffineTraversal<A, B, A2, B2> other) {
    return new AffineTraversal<>() {
      @Override
      public T set(B2 value, S source) {
        return AffineTraversal.this.getOrModify(source).fold(
            Function1.identity(),
            a -> AffineTraversal.this.set(other.set(value, a), source)
        );
      }

      @Override
      public Either<T, A2> getOrModify(S source) {
        return AffineTraversal.this.getOrModify(source).fold(
            Left::new,
            a -> other.getOrModify(a).fold(
                b -> new Left<>(AffineTraversal.this.set(b, source)),
                Right::new
            )
        );
      }

      @Override
      public Maybe<A2> preview(S source) {
        return AffineTraversal.this.preview(source).flatMap(other::preview);
      }
    };
  }
}
