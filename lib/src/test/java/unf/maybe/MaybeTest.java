/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.maybe;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

public final class MaybeTest {

  @Test
  public void justFlatMap() {
    Assert.assertEquals(new Just<>(2),
        new Just<>(1).flatMap(x -> new Just<>(x + 1)));
    Assert.assertEquals(new Nothing<>(),
        new Just<>(1).flatMap($ -> new Nothing<>()));
  }

  @Test
  public void nothingFlatMap() {
    Assert.assertEquals(new Nothing<>(),
        new Nothing<>().flatMap($ -> new Just<>(1)));
    Assert.assertEquals(new Nothing<>(),
        new Nothing<>().flatMap($ -> new Nothing<>()));
  }

  @Test
  public void justMap() {
    Assert.assertEquals(new Just<>(0), new Just<>(1).map($ -> 0));
  }

  @Test
  public void nothingMap() {
    Assert.assertEquals(new Nothing<>(), new Nothing<>().map($ -> 1));
  }

  @Test
  public void justFold() {
    final AtomicInteger i = new AtomicInteger(0);
    Assert.assertEquals(6,
        (long) new Just<>(5).fold(x -> x + i.incrementAndGet(), () -> 0));
    Assert.assertEquals(1, i.get());
  }

  @Test
  public void nothingFold() {
    final AtomicInteger i = new AtomicInteger(0);
    Assert.assertEquals(0,
        (long) new Nothing<Integer>().fold(x -> x + i.incrementAndGet(),
            () -> 0));
    Assert.assertEquals(0, i.get());
  }

  @Test
  public void justIsNotEmpty() {
    Assert.assertFalse(new Just<>(0).isEmpty());
  }

  @Test
  public void nothingIsEmpty() {
    Assert.assertTrue(new Nothing<>().isEmpty());
  }

  @Test
  public void justStreamLenOne() {
    Assert.assertEquals(1, new Just<>(0).stream().count());
  }

  @Test
  public void nothingStreamLenOne() {
    Assert.assertEquals(0, new Nothing<>().stream().count());
  }
}
