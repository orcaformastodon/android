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

package com.jeanbarrossilva.orca.platform.autos.kit.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.jeanbarrossilva.orca.platform.autos.border
import com.jeanbarrossilva.orca.platform.autos.colors.asColor
import com.jeanbarrossilva.orca.platform.autos.forms.asShape
import com.jeanbarrossilva.orca.platform.autos.kit.scaffold.bar.top.text.AutoSizeText
import com.jeanbarrossilva.orca.platform.autos.kit.sheet.controller.SheetController
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import com.jeanbarrossilva.orca.platform.autos.theme.MultiThemePreview

/** Tag that identifies a [Sheet] for testing purposes. */
const val SHEET_TAG = "sheet"

/** Default values used by a [Sheet]. */
internal object SheetDefaults {
  /** [Color] by which the container of a [Sheet] is colored by default. */
  val containerColor
    @Composable get() = AutosTheme.colors.background.container.asColor
}

/**
 * Component that overlays the entire UI, intended to display important, relevant information.
 *
 * Can be controlled through the current [SheetController], that should be first set in order for it
 * to be usable.
 *
 * @param modifier [Modifier] to be applied to the underlying [Box].
 * @see SheetController.current
 * @see SheetController.setCurrent
 */
@Composable
fun Sheet(modifier: Modifier = Modifier) {
  SheetController.current?.content?.let {
    @OptIn(ExperimentalMaterial3Api::class)
    Sheet(modifier, SheetController.rememberCurrentOrHiddenState()) { it(this) }
  }
}

/**
 * Component that overlays the entire UI, intended to display important, relevant information.
 *
 * Can be controlled through the current [SheetController], that should be first set in order for it
 * to be usable.
 *
 * @param modifier [Modifier] to be applied to the underlying [Box].
 * @param state [SheetState] by which changes on visibility will be performed.
 * @param content Content to be shown in this [Sheet].
 * @see SheetController.current
 * @see SheetController.setCurrent
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun Sheet(
  modifier: Modifier = Modifier,
  state: SheetState = rememberModalBottomSheetState(),
  content: SheetScope.() -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = {},
    modifier.testTag(SHEET_TAG),
    state,
    containerColor = SheetDefaults.containerColor,
    tonalElevation = 0.dp,
    windowInsets = WindowInsets.Zero
  ) {
    remember(::SheetScope).apply(content).Content()
  }
}

/** Preview of a [Sheet]. */
@Composable
@MultiThemePreview
private fun SheetPreview() {
  AutosTheme {
    @OptIn(ExperimentalMaterial3Api::class)
    Sheet {
      title { AutoSizeText("Title") }

      content {
        Box(
          Modifier.border(AutosTheme.forms.large.asShape)
            .clip(AutosTheme.forms.large.asShape)
            .fillMaxSize(),
          Alignment.Center
        ) {
          Text("Content", style = AutosTheme.typography.titleMedium)
        }
      }
    }
  }
}
