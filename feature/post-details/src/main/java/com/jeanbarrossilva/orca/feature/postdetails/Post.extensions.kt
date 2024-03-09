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

@file:JvmName("PostExtensions")

package com.jeanbarrossilva.orca.feature.postdetails

import androidx.compose.runtime.Composable
import com.jeanbarrossilva.orca.autos.colors.Colors
import com.jeanbarrossilva.orca.composite.timeline.post.figure.Figure
import com.jeanbarrossilva.orca.composite.timeline.post.figure.gallery.disposition.Disposition
import com.jeanbarrossilva.orca.composite.timeline.stat.details.asStatsDetails
import com.jeanbarrossilva.orca.composite.timeline.stat.details.asStatsDetailsFlow
import com.jeanbarrossilva.orca.composite.timeline.text.toAnnotatedString
import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.feed.profile.post.Post
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import com.jeanbarrossilva.orca.std.image.compose.ComposableImageLoader
import com.jeanbarrossilva.orca.std.image.compose.SomeComposableImageLoader
import java.net.URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Converts this [Post] into [PostDetails].
 *
 * @param actor Authenticated [Actor] whose avatar will be loaded by its [ComposableImageLoader].
 * @param onLinkClick Lambda to be run whenever the [Figure.Link]'s content is clicked.
 * @param onThumbnailClickListener [Disposition.OnThumbnailClickListener] that is notified of clicks
 *   on thumbnails.
 * @see Actor.Authenticated
 * @see Actor.Authenticated.avatarLoader
 */
@Composable
internal fun Post.toPostDetails(
  actor: Actor.Authenticated,
  onLinkClick: (URL) -> Unit = {},
  onThumbnailClickListener: Disposition.OnThumbnailClickListener =
    Disposition.OnThumbnailClickListener.empty
): PostDetails {
  return toPostDetails(actor, AutosTheme.colors, onLinkClick, onThumbnailClickListener)
}

/**
 * Converts this [Post] into [PostDetails].
 *
 * @param actor Authenticated [Actor] whose avatar will be loaded by its [ComposableImageLoader].
 * @param colors [Colors] by which the emitted [PostDetails]' [PostDetails.text] can be colored.
 * @param onLinkClick Lambda to be run whenever the [Figure.Link]'s content is clicked.
 * @param onThumbnailClickListener [Disposition.OnThumbnailClickListener] that is notified of clicks
 *   on thumbnails.
 * @see Actor.Authenticated
 * @see Actor.Authenticated.avatarLoader
 */
internal fun Post.toPostDetails(
  actor: Actor.Authenticated,
  colors: Colors,
  onLinkClick: (URL) -> Unit = {},
  onThumbnailClickListener: Disposition.OnThumbnailClickListener =
    Disposition.OnThumbnailClickListener.empty
): PostDetails {
  return PostDetails(
    id,
    author.avatarLoader as SomeComposableImageLoader,
    author.name,
    author.account,
    content.text.toAnnotatedString(colors),
    Figure.of(id, author.name, content, onLinkClick, onThumbnailClickListener),
    publicationDateTime,
    asStatsDetails(),
    actor.asCommenting(),
    url
  )
}

/**
 * Converts this [Post] into a [Flow] of [PostDetails].
 *
 * @param authenticationLock [AuthenticationLock] by which authentication will be requested for the
 *   avatar [ComposableImageLoader] of the [authenticated][Actor.Authenticated] [Actor] to be
 *   obtained.
 * @param colors [Colors] by which the emitted [PostDetails]' [PostDetails.text] can be colored.
 * @param onLinkClick Lambda to be run whenever the [Figure.Link]'s content is clicked.
 * @param onThumbnailClickListener [Disposition.OnThumbnailClickListener] that is notified of clicks
 *   on thumbnails.
 * @see Actor.Authenticated.avatarLoader
 */
internal suspend fun Post.toPostDetailsFlow(
  authenticationLock: SomeAuthenticationLock,
  colors: Colors,
  onLinkClick: (URL) -> Unit = {},
  onThumbnailClickListener: Disposition.OnThumbnailClickListener =
    Disposition.OnThumbnailClickListener.empty
): Flow<PostDetails> {
  return authenticationLock.scheduleUnlock {
    toPostDetailsFlow(it, colors, onLinkClick, onThumbnailClickListener)
  }
}

/**
 * Converts this [Post] into a [Flow] of [PostDetails].
 *
 * @param actor Authenticated [Actor] whose avatar will be loaded by its [ComposableImageLoader].
 * @param colors [Colors] by which the emitted [PostDetails]' [PostDetails.text] can be colored.
 * @param onLinkClick Lambda to be run whenever the [Figure.Link]'s content is clicked.
 * @param onThumbnailClickListener [Disposition.OnThumbnailClickListener] that is notified of clicks
 *   on thumbnails.
 * @see Actor.Authenticated
 * @see Actor.Authenticated.avatarLoader
 */
internal fun Post.toPostDetailsFlow(
  actor: Actor.Authenticated,
  colors: Colors,
  onLinkClick: (URL) -> Unit = {},
  onThumbnailClickListener: Disposition.OnThumbnailClickListener =
    Disposition.OnThumbnailClickListener.empty
): Flow<PostDetails> {
  return asStatsDetailsFlow().map {
    toPostDetails(actor, colors, onLinkClick, onThumbnailClickListener)
  }
}
