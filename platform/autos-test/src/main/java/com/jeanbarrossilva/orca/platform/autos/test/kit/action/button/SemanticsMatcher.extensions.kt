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

package com.jeanbarrossilva.orca.platform.autos.test.kit.action.button

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import com.jeanbarrossilva.orca.platform.autos.kit.action.button.PRIMARY_BUTTON_TAG
import com.jeanbarrossilva.orca.platform.autos.kit.action.button.PrimaryButton
import com.jeanbarrossilva.orca.platform.autos.kit.action.button.SECONDARY_BUTTON_TAG
import com.jeanbarrossilva.orca.platform.autos.kit.action.button.SecondaryButton

/** [SemanticsMatcher] that matches a [PrimaryButton]. */
fun isPrimaryButton(): SemanticsMatcher {
  return hasTestTag(PRIMARY_BUTTON_TAG)
}

/** [SemanticsMatcher] that matches a [SecondaryButton]. */
fun isSecondaryButton(): SemanticsMatcher {
  return hasTestTag(SECONDARY_BUTTON_TAG)
}
