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

package com.jeanbarrossilva.orca.core.sample.feed.profile.post

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.feed.profile.post.Post
import com.jeanbarrossilva.orca.core.sample.image.SampleImageSource
import com.jeanbarrossilva.orca.std.image.ImageLoader
import com.jeanbarrossilva.orca.std.image.SomeImageLoaderProvider

/**
 * [List] of [Post]s that have been added from a [Builder.AdditionScope].
 *
 * @param additionScope [Builder.AdditionScope] in which the [Post]s were added.
 * @param delegate [List] to which the functionality will be delegated.
 */
class Posts
private constructor(
  internal val additionScope: Builder.AdditionScope,
  private val delegate: List<Post>
) : List<Post> by delegate {
  /**
   * Configures and builds [Posts].
   *
   * @param authenticationLock [AuthenticationLock] that will lock authentication-dependent
   *   functionality behind a "wall".
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   [authenticated][Actor.Authenticated] [Actor]'s avatar will be loaded from a
   *   [SampleImageSource].
   * @see build
   * @see Actor.Authenticated.avatarLoader
   */
  class Builder
  internal constructor(
    private val authenticationLock: SomeAuthenticationLock,
    private val avatarLoaderProvider: SomeImageLoaderProvider<SampleImageSource>
  ) {
    /** [AdditionScope] in which this [Builder] will add [Post]s. */
    private val additionScope = AdditionScope(authenticationLock, avatarLoaderProvider)

    /** [List] on top of which [Posts] will be created. */
    private val delegate = mutableListOf<Post>()

    /**
     * Scope in which a [Post] is added.
     *
     * @param authenticationLock [AuthenticationLock] that will lock authentication-dependent
     *   functionality behind a "wall".
     * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which
     *   the avatar of the [authenticated][Actor.Authenticated] [Actor] will be loaded from a
     *   [SampleImageSource].
     * @see Actor.Authenticated.avatarLoader
     */
    class AdditionScope
    internal constructor(
      internal val authenticationLock: SomeAuthenticationLock,
      internal val avatarLoaderProvider: SomeImageLoaderProvider<SampleImageSource>
    ) {
      /**
       * [SamplePostWriter.Provider] that provides the [SamplePostWriter] for the [Post] to perform
       * its write operations.
       */
      internal val writerProvider = SamplePostWriter.Provider()

      /**
       * Marks the [Post] addition process as finished, specifying a [SamplePostWriter] with the
       * given [posts] to be provided by the [writerProvider].
       *
       * @param posts [Posts] with which the [SamplePostWriter] will be provided.
       */
      internal fun finish(posts: Posts) {
        val postProvider = SamplePostProvider(authenticationLock, posts)
        val writer = SamplePostWriter(avatarLoaderProvider, postProvider)
        writerProvider.provide(writer)
      }
    }

    /**
     * Adds multiple [Post]s.
     *
     * @param addition Returns the [Post]s to be added within the [additionScope].
     */
    fun addAll(addition: AdditionScope.() -> Collection<Post>): Builder {
      val posts = additionScope.addition()
      delegate.addAll(posts)
      return this
    }

    /**
     * Adds a [Post].
     *
     * @param addition Returns the [Post] to be added within the [additionScope].
     */
    fun add(addition: AdditionScope.() -> Post): Builder {
      val post = additionScope.addition()
      delegate.add(post)
      return this
    }

    /** Builds [Posts]. */
    internal fun build(): Posts {
      val delegateAsList = delegate.toList()
      return Posts(additionScope, delegateAsList).also(additionScope::finish)
    }
  }

  /**
   * Creates [Posts] with the given [Post] appended to it.
   *
   * @param other [Post] to be added.
   */
  internal operator fun plus(other: Post): Posts {
    return only(delegate + other)
  }

  /**
   * Creates [Posts] without the given [Post].
   *
   * @param other [Post] to be removed.
   */
  internal operator fun minus(other: Post): Posts {
    return only(delegate - other)
  }

  /**
   * Creates [Posts] containing only the given [replacements], discarding the existing ones.
   *
   * @param replacements [Post]s to be contained by the resulting [Posts].
   */
  private fun only(replacements: List<Post>): Posts {
    return Posts(additionScope.authenticationLock, additionScope.avatarLoaderProvider) {
      addAll { replacements }
    }
  }

  companion object
}
