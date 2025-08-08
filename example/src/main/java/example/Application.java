/*
 * SPDX-FileCopyrightText: 2025 2bllw8
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package example;

import example.optics.PrismExample;
import example.optics.RecordLensExample;

public final class Application {

  public static void main(String[] args) {
    PrismExample.either();
    PrismExample.maybe();

    RecordLensExample.composedLens();
    RecordLensExample.parametricType();
  }

  private Application() {
  }
}
