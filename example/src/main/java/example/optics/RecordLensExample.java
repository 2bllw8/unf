/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package example.optics;

import java.lang.annotation.Annotation;
import java.util.List;
import unf.optics.Lens;

public final class RecordLensExample {

  public static void composedLens() {
    final Lens<PkgRecord, PkgRecord, Integer, Integer> lens = PkgRecordLenses.pr
        .focus(PubRecordLenses.a);
    final PkgRecord rec = new PkgRecord(new PubRecord(0,
        0.1,
        () -> Annotation.class));

    assert lens.view(rec)
        .equals(0);
    assert lens.set(1, rec)
        .equals(new PkgRecord(new PubRecord(1, rec.pr().b(), rec.pr().an())));
  }

  public static void parametricType() {
    final Lens<PubRecord.InnerRecord, PubRecord.InnerRecord, List<String>, List<String>>
        lens
        = PubRecord$InnerRecordLenses.words;
    final PubRecord.InnerRecord rec = new PubRecord.InnerRecord(List.of("hi"));

    assert lens.view(rec)
        .equals(List.of("hi"));
    assert lens.set(List.of(), rec)
        .equals(new PubRecord.InnerRecord(List.of()));
  }

  private RecordLensExample() {
  }
}
