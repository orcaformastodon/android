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

package com.jeanbarrossilva.orca.platform.ui.core.navigation.transition

import androidx.fragment.app.FragmentTransaction

/** Creates a close [Transition]. */
fun closing(): Transition {
  return Transition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
}

/** Creates an open [Transition]. */
fun opening(): Transition {
  return Transition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
}

/** Creates a sudden [Transition]. */
fun suddenly(): Transition {
  return Transition(FragmentTransaction.TRANSIT_NONE)
}
