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

package com.jeanbarrossilva.orca.core.feed.profile.post

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Provides [Post]s. */
abstract class PostProvider {
  /**
   * [AuthenticationLock] for distinguishing standard [Post]s from those that can be deleted when
   * providing them.
   */
  protected abstract val authenticationLock: SomeAuthenticationLock

  /**
   * Provides the [Post] identified as [id].
   *
   * @param id ID of the [Post] to be provided.
   * @see Post.id
   */
  suspend fun provide(id: String): Flow<Post> {
    return onProvide(id).map { it.asDeletableOrThis(authenticationLock) }
  }

  /**
   * Callback to be called when a [Post] identified as [id] is requested to be provided.
   *
   * @param id ID of the [Post] to be provided.
   * @see Post.id
   */
  protected abstract suspend fun onProvide(id: String): Flow<Post>
}
