/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.test.optics;

import java.lang.annotation.Annotation;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import unf.optics.Lens;
import unf.optics.Traversal;

public final class RecordLensTest {

  @Test
  public void composedLens() {
    final Lens<PkgRecord, PkgRecord, Integer, Integer> lens = PkgRecordLenses.pr
        .focus(PubRecordLenses.a);
    final PkgRecord rec = new PkgRecord(new PubRecord(0,
        0.1,
        () -> Annotation.class));

    Assert.assertEquals(
        Integer.valueOf(0),
        lens.view(rec)
    );
    Assert.assertEquals(
        new PkgRecord(new PubRecord(1, rec.pr().b(), rec.pr().an())),
        lens.set(1, rec)
    );
  }

  @Test
  public void parametricType() {
    final Lens<PubRecord.InnerRecord, PubRecord.InnerRecord, List<String>, List<String>>
        lens = PubRecord$InnerRecordLenses.words;
    final PubRecord.InnerRecord rec = new PubRecord.InnerRecord(List.of("hi"));

    Assert.assertEquals(
        List.of("hi"),
        lens.view(rec)
    );
    Assert.assertEquals(
        new PubRecord.InnerRecord(List.of()),
        lens.set(List.of(), rec)
    );
  }

  @Test
  public void listType() {
    final Traversal<PubRecord.InnerRecord, PubRecord.InnerRecord, Lens<PubRecord.InnerRecord, PubRecord.InnerRecord, String, String>, String>
        traversal = PubRecord$InnerRecordLenses.wordsElements;

    final PubRecord.InnerRecord rec = new PubRecord.InnerRecord(
        List.of("o.O", "^.^"));

    Assert.assertEquals(
        new PubRecord.InnerRecord(rec.words().stream()
            .map(it -> "(" + it + ")")
            .toList()),
        traversal.over(
            l -> "(" + l.view(rec) + ")",
            rec
        )
    );
  }
}
