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

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.post.withSample
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.post.withSamples
import com.jeanbarrossilva.orca.core.sample.test.image.TestSampleImageLoader
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

internal class SamplePostWriterTests {
  @Test(expected = IllegalArgumentException::class)
  fun throwsWhenAddingDuplicatePost() {
    SamplePostWriter(
        TestSampleImageLoader.Provider,
        SamplePostProvider.from(TestSampleImageLoader.Provider, defaultPosts = Posts.withSample)
      )
      .add(Posts.withSample.single())
  }

  @Test
  fun addsUniquePost() {
    SamplePostWriter(TestSampleImageLoader.Provider).add(Posts.withSample.single())
  }

  @Test
  fun resetsCommentsInPosts() {
    val defaultPosts = Posts.withSample
    val postProvider = SamplePostProvider.from(TestSampleImageLoader.Provider, defaultPosts)
    val comment = Posts.withSamples.last()
    val post = defaultPosts.single()
    val previousCommentCount = post.comment.count
    val writer = SamplePostWriter(TestSampleImageLoader.Provider, postProvider)
    runTest {
      postProvider.provide(post.id).test {
        awaitItem().comment.add(comment)
        writer.reset()
        assertThat(awaitItem().comment.count).isEqualTo(previousCommentCount)
      }
    }
  }
}
