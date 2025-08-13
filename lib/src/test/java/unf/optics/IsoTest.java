package unf.optics;

import org.junit.Assert;
import org.junit.Test;

public final class IsoTest {

  private static final Iso<Rec, Rec, String, String> ISO_REC_S = new Iso<>() {
    @Override
    public String view(Rec source) {
      return source.s;
    }

    @Override
    public Rec review(String value) {
      return new Rec(value);
    }
  };
  private static final Iso<String, String, String, String>
      ISO_STR_PAR = new Iso<>() {
    @Override
    public String view(String source) {
      return "(" + source + ")";
    }

    @Override
    public String review(String value) {
      return value.substring(1, value.length() - 1);
    }
  };

  private record Rec(String s) {
  }

  @Test
  public void wellFormednessViewReview() {
    final Rec r = new Rec("(╯°□°)╯︵ ┻━┻");

    Assert.assertEquals(
        r,
        ISO_REC_S.review(ISO_REC_S.view(r))
    );
  }

  @Test
  public void wellFormednessReviewView() {
    final String s = "┬─┬ノ( º _ ºノ)";

    Assert.assertEquals(
        s,
        ISO_REC_S.view(ISO_REC_S.review(s))
    );
  }

  @Test
  public void testComposeView() {
    final Rec r = new Rec("•‿•");

    Assert.assertEquals(
        ISO_STR_PAR.view(r.s),
        ISO_REC_S.focus(ISO_STR_PAR).view(r)
    );
  }

  @Test
  public void testComposeReview() {
    final Rec r = new Rec("O=('-'Q)");

    Assert.assertEquals(
        r,
        ISO_REC_S.focus(ISO_STR_PAR).review(ISO_STR_PAR.view(r.s))
    );
  }
}
