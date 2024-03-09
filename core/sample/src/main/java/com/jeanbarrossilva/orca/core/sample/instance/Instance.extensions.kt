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

package com.jeanbarrossilva.orca.core.sample.instance

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.Authenticator
import com.jeanbarrossilva.orca.core.feed.profile.post.Post
import com.jeanbarrossilva.orca.core.instance.Instance
import com.jeanbarrossilva.orca.core.sample.auth.createSample
import com.jeanbarrossilva.orca.core.sample.feed.profile.post.Posts
import com.jeanbarrossilva.orca.core.sample.feed.profile.post.createSamples
import com.jeanbarrossilva.orca.core.sample.image.SampleImageSource
import com.jeanbarrossilva.orca.std.image.ImageLoader
import com.jeanbarrossilva.orca.std.image.SomeImageLoaderProvider

/**
 * Creates a [SampleInstance].
 *
 * @param imageLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which images
 *   can be loaded from a [SampleImageSource].
 * @param defaultPosts [Posts] that are provided by default within the [SampleInstance].
 */
fun Instance.Companion.createSample(
  imageLoaderProvider: SomeImageLoaderProvider<SampleImageSource>,
  defaultPosts: Posts
): SampleInstance {
  val authenticationLock = AuthenticationLock.createSample(imageLoaderProvider)
  return SampleInstance(authenticationLock, imageLoaderProvider, defaultPosts)
}

/**
 * Creates a [SampleInstance].
 *
 * @param authenticationLock [Instance]-specific [AuthenticationLock] that will lock
 *   authentication-dependent functionality behind a "wall".
 * @param imageLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which images
 *   can be loaded from a [SampleImageSource].
 * @param defaultPosts [Posts] that are provided by default within the [SampleInstance].
 */
fun Instance.Companion.createSample(
  authenticationLock: AuthenticationLock<Authenticator>,
  imageLoaderProvider: SomeImageLoaderProvider<SampleImageSource>,
  defaultPosts: Posts =
    Posts(authenticationLock, imageLoaderProvider) {
      addAll { Post.createSamples(imageLoaderProvider) }
    }
): SampleInstance {
  return SampleInstance(authenticationLock, imageLoaderProvider, defaultPosts)
}
