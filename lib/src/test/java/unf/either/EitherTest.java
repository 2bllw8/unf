/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.either;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

public final class EitherTest {

  @Test
  public void leftFlatMapLeft() {
    Assert.assertEquals(new Right<>(0),
        new Left<>(0).flatMapLeft($ -> new Right<>(0)));
  }

  @Test
  public void leftFlatMapRight() {
    Assert.assertEquals(new Left<>(0),
        new Left<>(0).flatMapRight($ -> new Right<>(0)));
  }

  @Test
  public void rightFlatMapLeft() {
    Assert.assertEquals(new Right<>(0),
        new Right<>(0).flatMapLeft($ -> new Left<>(0)));
  }

  @Test
  public void rightFlatMapRight() {
    Assert.assertEquals(new Left<>(0),
        new Right<>(0).flatMapRight($ -> new Left<>(0)));
  }

  @Test
  public void leftMapLeft() {
    Assert.assertEquals(new Left<>(1), new Left<>(0).mapLeft($ -> 1));
  }

  @Test
  public void leftMapRight() {
    Assert.assertEquals(new Left<>(0), new Left<>(0).mapRight($ -> 1));
  }

  @Test
  public void rightMapleft() {
    Assert.assertEquals(new Right<>(1), new Right<>(1).mapLeft($ -> 0));
  }

  @Test
  public void rightMapRight() {
    Assert.assertEquals(new Right<>(0), new Right<>(1).mapRight($ -> 0));
  }

  @Test
  public void rightFold() {
    final AtomicInteger i = new AtomicInteger(0);
    Assert.assertEquals(6,
        (long) new Right<>(5).fold($ -> 0, x -> x + i.incrementAndGet()));
    Assert.assertEquals(1, i.get());
  }

  @Test
  public void leftFold() {
    final AtomicInteger i = new AtomicInteger(0);
    Assert.assertEquals(5,
        (long) new Left<>(4).fold(x -> x + i.incrementAndGet(), $ -> 0));
    Assert.assertEquals(1, i.get());
  }

  @Test
  public void leftIsNotRight() {
    Assert.assertFalse(new Left<>(0).isRight());
  }

  @Test
  public void rightIsRight() {
    Assert.assertTrue(new Right<>(0).isRight());
  }

  @Test
  public void leftStreamLeftLenOne() {
    Assert.assertEquals(1, new Left<>(0).streamLeft().count());
  }

  @Test
  public void leftStreamRightLenZero() {
    Assert.assertEquals(0, new Left<>(0).streamRight().count());
  }

  @Test
  public void rightStreamLeftLenZero() {
    Assert.assertEquals(0, new Right<>(0).streamLeft().count());
  }

  @Test
  public void rightStreamRightLenOne() {
    Assert.assertEquals(1, new Right<>(0).streamRight().count());
  }
}
