/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package example.optics;

import java.lang.annotation.Annotation;
import java.util.List;
import unf.optics.RecordLenses;

@RecordLenses
public record PubRecord(int a, Double b, Annotation an) {

  @RecordLenses
  protected record InnerRecord(List<String> words) {
  }
}
