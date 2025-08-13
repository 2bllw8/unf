package unf.optics;

import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import unf.either.Either;
import unf.either.Left;
import unf.either.Right;
import unf.maybe.Just;
import unf.maybe.Maybe;
import unf.maybe.Nothing;

public final class AffineTraversalTest {

  private static final AffineTraversal<List<Character>, List<Character>, Character, Character>
      AFFINE_TRAVERSAL_LIST_FIRST = new AffineTraversal<>() {

    @Override
    public Maybe<Character> preview(List<Character> source) {
      return source.isEmpty() ? new Nothing<>() : new Just<>(source.getFirst());
    }

    @Override
    public Either<List<Character>, Character> matching(List<Character> source) {
      return source.isEmpty()
          ? new Left<>(source)
          : new Right<>(source.getFirst());
    }

    @Override
    public List<Character> set(Character value, List<Character> source) {
      return Stream.concat(Stream.of(value), source.stream().skip(1)).toList();
    }
  };

  @Test
  public void set() {
    final List<Character> s = List.of('h', 'i');

    Assert.assertEquals(
        List.of('H', 'i'),
        AFFINE_TRAVERSAL_LIST_FIRST.set('H', s)
    );
  }

  @Test
  public void previewEmpty() {
    final List<Character> s = List.of();

    Assert.assertEquals(
        new Nothing<>(),
        AFFINE_TRAVERSAL_LIST_FIRST.preview(s)
    );
  }

  @Test
  public void previewNonEmpty() {
    final List<Character> s = List.of(':');

    Assert.assertEquals(
        new Just<>(':'),
        AFFINE_TRAVERSAL_LIST_FIRST.preview(s)
    );
  }
}
