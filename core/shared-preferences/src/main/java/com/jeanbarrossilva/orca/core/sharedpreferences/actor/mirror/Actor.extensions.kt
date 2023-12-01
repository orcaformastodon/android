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

package com.jeanbarrossilva.orca.core.sharedpreferences.actor.mirror

import com.jeanbarrossilva.orca.core.auth.actor.Actor

/** Converts this [Actor] into a [MirroredActor]. */
internal fun Actor.toMirroredActor(): MirroredActor {
  return when (this) {
    is Actor.Unauthenticated -> MirroredActor.unauthenticated()
    is Actor.Authenticated -> MirroredActor.authenticated(id, accessToken)
  }
}
