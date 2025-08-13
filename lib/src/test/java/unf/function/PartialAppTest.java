package unf.function;

import org.junit.Assert;
import org.junit.Test;

public final class PartialAppTest {

  @Test
  public void f2() {
    final Function2<Integer, String, Boolean> f2 =
        (i, s) -> s.length() == i;
    final Function1<String, Boolean> f1 = f2.apply(2);
    final String s = "Hi";
    Assert.assertEquals(f2.apply(2, s), f1.apply(s));
    Assert.assertEquals(
        f2.apply(2, s),
        Function2.uncurry(f2).apply(2, s)
    );
  }

  @Test
  public void f3() {
    final Function3<Integer, Integer, String, Boolean> f3 =
        (min, max, s) -> s.length() > min && s.length() < max;
    final Function1<Integer, Function1<String, Boolean>> f2
        = f3.apply(3);
    final Function1<String, Boolean> f1 =
        f2.apply(5);
    final String s = "Hello";
    Assert.assertEquals(
        f1.apply(s),
        f3.apply(3, 5, s)
    );
    Assert.assertEquals(
        Function3.uncurry(f3).apply(3, 5, s),
        f3.apply(3, 5, s)
    );
  }

  @Test
  public void f4() {
    final Function4<String, Integer, Integer, String, Boolean>
        f4 = (s1, begin, end, s2) -> s1.substring(begin, end).equals(s2);
    final String s1 = "function";
    final Function1<Integer, Function1<Integer, Function1<String, Boolean>>>
        f3 = f4.apply(s1);
    final Function1<Integer, Function1<String, Boolean>> f2 = f3.apply(0);
    final Function1<String, Boolean> f1 = f2.apply(4);
    final String s2 = "fun";
    Assert.assertEquals(
        f1.apply(s2),
        f4.apply(s1, 0, 4, s2)
    );
    Assert.assertEquals(
        Function4.uncurry(f4).apply(s1, 0, 4, s2),
        f4.apply(s1, 0, 4, s2)
    );
  }
}
