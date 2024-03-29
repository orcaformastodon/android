/*
 * Copyright © 2023 Orca
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

package com.jeanbarrossilva.orca.composite.timeline.test.refresh

import androidx.compose.ui.test.junit4.createComposeRule
import com.jeanbarrossilva.orca.composite.timeline.Timeline
import com.jeanbarrossilva.orca.composite.timeline.refresh.Refresh
import com.jeanbarrossilva.orca.composite.timeline.test.onRefreshIndicator
import org.junit.Rule
import org.junit.Test

internal class SemanticsNodeInteractionExtensionsTests {
  @get:Rule val composeRule = createComposeRule()

  @Test(expected = AssertionError::class)
  fun throwsWhenAssertingThatNodeIsNotInProgressWhenItIs() {
    composeRule.setContent { Timeline(onNext = {}, refresh = Refresh.Indefinite) {} }
    composeRule.onRefreshIndicator().assertIsNotInProgress()
  }

  @Test
  fun doesNotThrowWhenAssertingThatNodeIsNotInProgressWhenItIsNot() {
    composeRule.setContent { Timeline(onNext = {}) {} }
    composeRule.onRefreshIndicator().assertIsNotInProgress()
  }
}
