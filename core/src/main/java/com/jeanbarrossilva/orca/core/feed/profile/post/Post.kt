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

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.post.content.Content
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.Stat
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.addable.AddableStat
import com.jeanbarrossilva.orca.core.feed.profile.post.stat.toggleable.ToggleableStat
import java.net.URL
import java.time.ZonedDateTime

/** Content that's been posted by a user, the [author]. */
interface Post {
  /** Unique identifier. */
  val id: String

  /** [Author] that has authored this [Post]. */
  val author: Author

  /** [Content] that's been composed by the [author]. */
  val content: Content

  /** Zoned moment in time in which this [Post] was published. */
  val publicationDateTime: ZonedDateTime

  /** [Stat] for comments. */
  val comment: AddableStat<Post>

  /** [Stat] for favorites. */
  val favorite: ToggleableStat<Profile>

  /** [Stat] for reposts. */
  val repost: ToggleableStat<Profile>

  /** [URL] that leads to this [Post]. */
  val url: URL

  /** Creates a [DeletablePost] from this [Post]. */
  fun asDeletable(): DeletablePost

  companion object
}
