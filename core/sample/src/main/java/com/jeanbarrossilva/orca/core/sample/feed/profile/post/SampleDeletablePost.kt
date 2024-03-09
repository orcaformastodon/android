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

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.post.Author
import com.jeanbarrossilva.orca.core.feed.profile.post.DeletablePost
import com.jeanbarrossilva.orca.core.feed.profile.post.Post
import com.jeanbarrossilva.orca.core.feed.profile.post.content.Content
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.addable.AddableStat
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.toggleable.ToggleableStat
import java.net.URL
import java.time.ZonedDateTime

/**
 * [DeletablePost] whose deletion is performed by the [writer].
 *
 * @param delegate [SamplePost] to which this [SampleDeletablePost]'s functionality will be
 *   delegated.
 */
internal data class SampleDeletablePost(private val delegate: SamplePost) :
  DeletablePost(delegate) {
  override suspend fun delete() {
    delegate.writerProvider.provide().delete(id)
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
    return delegate.clone(id, author, content, publicationDateTime, comment, favorite, repost, url)
  }
}
