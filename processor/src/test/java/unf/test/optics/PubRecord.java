/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package unf.test.optics;

import java.lang.annotation.Annotation;
import java.util.List;
import unf.optics.RecordOptics;

@RecordOptics
public record PubRecord(int a, Double b, Annotation an) {

  @RecordOptics
  protected record InnerRecord(List<String> words) {
  }
}
