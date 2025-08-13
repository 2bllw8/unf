package unf.function;

import org.junit.Assert;
import org.junit.Test;

public final class Function1Test {

  @Test
  public void composeTest() {
    final Function1<Double, Double> twice = x -> x * 2.0;
    final Function1<Double, Double> plusOne = x -> x + 1.0;
    final double i = 16;

    Assert.assertEquals(
        34.0,
        twice.compose(plusOne).apply(i),
        0.00001
    );
  }

  @Test
  public void thenTest() {
    final Function1<Double, Double> twice = x -> x * 2.0;
    final Function1<Double, Double> plusOne = x -> x + 1.0;
    final double i = 18;

    Assert.assertEquals(
        37.0,
        twice.then(plusOne).apply(i),
        0.00001
    );
  }

  @Test
  public void idTest() {
    final Object o = new int[10];
    Assert.assertSame(o, Function1.identity().apply(o));
  }
}
