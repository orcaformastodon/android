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

package com.jeanbarrossilva.orca.core.feed.profile.post

import assertk.assertThat
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.post.content.Content
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.addable.AddableStat
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.toggleable.ToggleableStat
import com.jeanbarrossilva.orca.core.sample.feed.profile.post.Posts
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.post.withSample
import com.jeanbarrossilva.orca.core.test.TestAuthenticationLock
import com.jeanbarrossilva.testing.hasPropertiesEqualToThoseOf
import java.net.URL
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

internal class DeletablePostTests {
  @Test
  fun delegatesNonDeletionFunctionalityToDelegate() {
    assertThat(
        object : DeletablePost(Posts.withSample.single()) {
          override suspend fun delete() {}

          override fun clone(
            id: String,
            author: Author,
            content: Content,
            publicationDateTime: ZonedDateTime,
            comment: AddableStat<Post>,
            favorite: ToggleableStat<Profile>,
            repost: ToggleableStat<Profile>,
            url: URL
          ): Post {
            return this
          }
        }
      )
      .hasPropertiesEqualToThoseOf(Posts.withSample)
  }

  @Test
  fun returnsItselfWhenConvertingItIntoDeletablePost() {
    val authenticationLock = TestAuthenticationLock()
    val post =
      object : DeletablePost(Posts.withSample.single()) {
        override suspend fun delete() {}

        override fun clone(
          id: String,
          author: Author,
          content: Content,
          publicationDateTime: ZonedDateTime,
          comment: AddableStat<Post>,
          favorite: ToggleableStat<Profile>,
          repost: ToggleableStat<Profile>,
          url: URL
        ): Post {
          return this
        }
      }
    runTest { assertThat(post.asDeletableOrThis(authenticationLock)).isSameAs(post) }
  }

  @Test
  fun isDeleted() {
    var hasBeenDeleted = false
    runTest {
      object : DeletablePost(Posts.withSample.single()) {
          override suspend fun delete() {
            hasBeenDeleted = true
          }

          override fun clone(
            id: String,
            author: Author,
            content: Content,
            publicationDateTime: ZonedDateTime,
            comment: AddableStat<Post>,
            favorite: ToggleableStat<Profile>,
            repost: ToggleableStat<Profile>,
            url: URL
          ): Post {
            return this
          }
        }
        .delete()
    }
    assertThat(hasBeenDeleted).isTrue()
  }
}
