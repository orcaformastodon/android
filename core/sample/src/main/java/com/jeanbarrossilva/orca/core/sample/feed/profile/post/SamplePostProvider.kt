/*
 * Copyright © 2023-2024 Orca
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

package com.jeanbarrossilva.orca.core.sample.feed.profile.post

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.feed.profile.post.Post
import com.jeanbarrossilva.orca.core.feed.profile.post.PostProvider
import com.jeanbarrossilva.orca.core.sample.auth.createSample
import com.jeanbarrossilva.orca.core.sample.image.SampleImageSource
import com.jeanbarrossilva.orca.std.image.ImageLoader
import com.jeanbarrossilva.orca.std.image.SomeImageLoaderProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * [PostProvider] that provides sample [Posts].
 *
 * @param defaultPosts [Post]s that are present by default.
 */
class SamplePostProvider
internal constructor(
  override val authenticationLock: SomeAuthenticationLock,
  internal val defaultPosts: Posts
) : PostProvider() {
  /** [MutableStateFlow] that provides the [Post]s. */
  internal val postsFlow = MutableStateFlow(defaultPosts)

  override suspend fun onProvide(id: String): Flow<Post> {
    return postsFlow.mapNotNull { posts -> posts.find { post -> post.id == id } }
  }

  /** Obtains the [AuthenticationLock] with which this [SamplePostProvider] was created. */
  internal fun getAuthenticationLock(): SomeAuthenticationLock {
    return authenticationLock
  }

  /**
   * Provides the [Post]s made by the author whose ID equals to the given one.
   *
   * @param authorID ID of the author whose [Post]s will be provided.
   */
  internal fun provideBy(authorID: String): Flow<List<Post>> {
    return postsFlow.map { posts -> posts.filter { post -> post.author.id == authorID } }
  }

  companion object {
    /**
     * Creates a [SamplePostProvider] whose [AuthenticationLock]'s and default [Posts]' avatars are
     * loaded by the [ImageLoader] provided by the given [ImageLoader.Provider].
     *
     * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which
     *   the avatar of the [authenticated][Actor.Authenticated] [Actor] will be loaded from a
     *   [SampleImageSource].
     * @see Actor.Authenticated.avatarLoader
     */
    internal fun from(
      avatarLoaderProvider: SomeImageLoaderProvider<SampleImageSource>
    ): SamplePostProvider {
      val authenticationLock = AuthenticationLock.createSample(avatarLoaderProvider)
      val defaultPosts = Posts(authenticationLock, avatarLoaderProvider)
      return SamplePostProvider(authenticationLock, defaultPosts)
    }

    /**
     * Creates a [SamplePostProvider] whose [AuthenticationLock]'s and default [Posts]' avatars are
     * loaded by the [ImageLoader] provided by the given [ImageLoader.Provider].
     *
     * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which
     *   the avatar of the [authenticated][Actor.Authenticated] [Actor] will be loaded from a
     *   [SampleImageSource].
     * @param defaultPosts [Posts] that are present by default.
     * @see Actor.Authenticated.avatarLoader
     */
    internal fun from(
      avatarLoaderProvider: SomeImageLoaderProvider<SampleImageSource>,
      defaultPosts: Posts
    ): SamplePostProvider {
      val authenticationLock = AuthenticationLock.createSample(avatarLoaderProvider)
      return SamplePostProvider(authenticationLock, defaultPosts)
    }
  }
}
