/*
 * Copyright © 2024 Orca
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see https://www.gnu.org/licenses.
 */

package com.jeanbarrossilva.orca.platform.starter.lifecycle

import androidx.test.core.app.launchActivity
import assertk.assertThat
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.platform.starter.lifecycle.test.doOnDestroy
import org.junit.Test

internal class ActivityExtensionsTests {
  @Test
  fun runsActionOnDestroy() {
    var hasActionBeenRun = false
    launchActivity<CompleteLifecycleActivity>().use { scenario ->
      scenario.onActivity { activity ->
        activity.doOnDestroy { hasActionBeenRun = true }
        activity.finish()
      }
    }
    assertThat(hasActionBeenRun).isTrue()
  }
}
