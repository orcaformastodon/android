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

package com.jeanbarrossilva.orca.feature.registration.ui.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf

/** Scope from which items can be added onto a [Stack]. */
internal class StackScope {
  /** Contents that have been added onto the [Stack]. */
  private val contents = mutableStateListOf<@Composable () -> Unit>()

  /** Gets an immutable [List] containing the contents that have been added onto the [Stack]. */
  fun contents(): List<@Composable () -> Unit> {
    return contents.toList()
  }

  /**
   * Adds an item onto the [Stack].
   *
   * @param content Content to be shown.
   */
  fun item(content: @Composable () -> Unit) {
    contents.add(content)
  }
}
