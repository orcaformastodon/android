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

package com.jeanbarrossilva.orca.feature.postdetails

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import com.jeanbarrossilva.loadable.Loadable
import com.jeanbarrossilva.loadable.ifLoaded
import com.jeanbarrossilva.loadable.list.ListLoadable
import com.jeanbarrossilva.orca.composite.timeline.Timeline
import com.jeanbarrossilva.orca.composite.timeline.avatar.SmallAvatar
import com.jeanbarrossilva.orca.composite.timeline.post.PostPreview
import com.jeanbarrossilva.orca.composite.timeline.post.figure.Figure
import com.jeanbarrossilva.orca.composite.timeline.refresh.Refresh
import com.jeanbarrossilva.orca.composite.timeline.stat.details.StatsDetails
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.feed.profile.account.Account
import com.jeanbarrossilva.orca.core.sample.feed.profile.post.Posts
import com.jeanbarrossilva.orca.feature.postdetails.ui.header.Header
import com.jeanbarrossilva.orca.feature.postdetails.ui.header.formatted
import com.jeanbarrossilva.orca.feature.postdetails.viewmodel.PostDetailsViewModel
import com.jeanbarrossilva.orca.platform.autos.kit.input.text.CompositionTextField
import com.jeanbarrossilva.orca.platform.autos.kit.input.text.TextFieldDefaults
import com.jeanbarrossilva.orca.platform.autos.kit.scaffold.Scaffold
import com.jeanbarrossilva.orca.platform.autos.kit.scaffold.bar.top.TopAppBarDefaults
import com.jeanbarrossilva.orca.platform.autos.kit.scaffold.bar.top.TopAppBarWithBackNavigation
import com.jeanbarrossilva.orca.platform.autos.kit.scaffold.bar.top.text.AutoSizeText
import com.jeanbarrossilva.orca.platform.autos.reactivity.OnBottomAreaAvailabilityChangeListener
import com.jeanbarrossilva.orca.platform.autos.reactivity.scroll.BottomAreaAvailabilityNestedScrollConnection
import com.jeanbarrossilva.orca.platform.autos.reactivity.scroll.rememberBottomAreaAvailabilityNestedScrollConnection
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import com.jeanbarrossilva.orca.platform.autos.theme.MultiThemePreview
import com.jeanbarrossilva.orca.platform.core.sample
import com.jeanbarrossilva.orca.platform.core.withSample
import com.jeanbarrossilva.orca.platform.ime.state.rememberImeAsState
import com.jeanbarrossilva.orca.std.image.compose.SomeComposableImageLoader
import java.io.Serializable
import java.net.URL
import java.time.ZonedDateTime
import java.util.Objects

@Immutable
internal data class PostDetails(
  val id: String,
  val avatarLoader: SomeComposableImageLoader,
  val name: String,
  private val account: Account,
  val text: AnnotatedString,
  val figure: Figure?,
  private val publicationDateTime: ZonedDateTime,
  val stats: StatsDetails,
  val commenting: Commenting,
  val url: URL
) : Serializable {
  val formattedPublicationDateTime = publicationDateTime.formatted
  val formattedUsername = account.username.toString()

  class Commenting
  private constructor(
    val commenterAvatarLoader: SomeComposableImageLoader,
    val comment: TextFieldValue
  ) {
    constructor(
      commenterAvatarLoader: SomeComposableImageLoader
    ) : this(commenterAvatarLoader, TextFieldValue())

    override fun equals(other: Any?): Boolean {
      return other is Commenting &&
        commenterAvatarLoader == other.commenterAvatarLoader &&
        comment == other.comment
    }

    override fun hashCode(): Int {
      return Objects.hash(commenterAvatarLoader, comment)
    }

    override fun toString(): String {
      return "Commenting(commenterAvatarLoader=$commenterAvatarLoader, comment=$comment)"
    }

    fun copy(comment: TextFieldValue): Commenting {
      return Commenting(commenterAvatarLoader, comment)
    }
  }

  companion object {
    val sample
      @Composable get() = Posts.withSample.single().toPostDetails(Actor.Authenticated.sample)
  }
}

@Composable
internal fun PostDetails(
  viewModel: PostDetailsViewModel,
  boundary: PostDetailsBoundary,
  onBottomAreaAvailabilityChangeListener: OnBottomAreaAvailabilityChangeListener,
  modifier: Modifier = Modifier
) {
  val detailsLoadable by viewModel.detailsLoadableFlow.collectAsState()
  val commentsLoadable by viewModel.commentsLoadableFlow.collectAsState()
  var isTimelineRefreshing by remember { mutableStateOf(false) }
  val bottomAreaAvailabilityNestedScrollConnection =
    rememberBottomAreaAvailabilityNestedScrollConnection(onBottomAreaAvailabilityChangeListener)

  PostDetails(
    detailsLoadable,
    onDetailsChange = viewModel::setDetails,
    commentsLoadable,
    isTimelineRefreshing,
    onTimelineRefresh = {
      isTimelineRefreshing = true
      viewModel.requestRefresh { isTimelineRefreshing = false }
    },
    onComment = viewModel::comment,
    onFavorite = viewModel::favorite,
    onRepost = viewModel::repost,
    onShare = viewModel::share,
    onNavigateToDetails = boundary::navigateToPostDetails,
    onNext = viewModel::loadCommentsAt,
    onBackwardsNavigation = boundary::pop,
    bottomAreaAvailabilityNestedScrollConnection,
    modifier
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PostDetails(
  detailsLoadable: Loadable<PostDetails>,
  onDetailsChange: (details: PostDetails) -> Unit,
  commentsLoadable: ListLoadable<PostPreview>,
  isTimelineRefreshing: Boolean,
  onTimelineRefresh: () -> Unit,
  onComment: (comment: AnnotatedString) -> Unit,
  onFavorite: (postID: String) -> Unit,
  onRepost: (postID: String) -> Unit,
  onShare: (URL) -> Unit,
  onNavigateToDetails: (postID: String) -> Unit,
  onNext: (index: Int) -> Unit,
  onBackwardsNavigation: () -> Unit,
  bottomAreaAvailabilityNestedScrollConnection: BottomAreaAvailabilityNestedScrollConnection,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current
  val topAppBarScrollBehavior = TopAppBarDefaults.scrollBehavior
  val ime by rememberImeAsState()
  val compositionFieldSpacing = TextFieldDefaults.compositionSpacing
  val adjustedBottomAreaHeight =
    with(bottomAreaAvailabilityNestedScrollConnection) {
      remember(this, density) {
        listener.height - with(density) { compositionFieldSpacing.roundToPx() }
      }
    }
  val bottomAreaYOffset by bottomAreaAvailabilityNestedScrollConnection.getYOffsetAsState()
  val bottomAreaYOffsetAsInt = remember(bottomAreaYOffset, bottomAreaYOffset::toInt)
  val isBottomAreaReceding =
    remember(bottomAreaYOffset, adjustedBottomAreaHeight) {
      bottomAreaYOffset >= adjustedBottomAreaHeight
    }
  val commentFieldYOffset by
    animateIntAsState(
      adjustedBottomAreaHeight - bottomAreaYOffsetAsInt,
      spring(Spring.DampingRatioNoBouncy),
      label = "CommentFieldYOffset"
    )

  Scaffold(
    modifier,
    topAppBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      TopAppBarWithBackNavigation(
        onNavigation = onBackwardsNavigation,
        title = { AutoSizeText(stringResource(R.string.feature_post_details)) },
        scrollBehavior = topAppBarScrollBehavior
      )
    },
    buttonBar = {
      detailsLoadable.ifLoaded {
        Column(
          Modifier.offset {
            if (ime.isOpen && isBottomAreaReceding) {
              IntOffset(x = 0, y = commentFieldYOffset)
            } else {
              IntOffset.Zero
            }
          }
        ) {
          HorizontalDivider()

          CompositionTextField(
            commenting.comment,
            onValueChange = { onDetailsChange(copy(commenting = commenting.copy(comment = it))) },
            leadingIcon = { SmallAvatar(commenting.commenterAvatarLoader, name) },
            onSend = { onComment(commenting.comment.annotatedString) }
          ) {
            Text(stringResource(R.string.feature_post_details_comment))
          }
        }
      }
    }
  ) {
    Timeline(
      commentsLoadable,
      onFavorite,
      onRepost,
      onShare,
      onClick = onNavigateToDetails,
      onNext,
      Modifier.nestedScroll(bottomAreaAvailabilityNestedScrollConnection)
        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
      contentPadding = it,
      refresh =
        Refresh(isTimelineRefreshing, indicatorOffset = it.calculateTopPadding(), onTimelineRefresh)
    ) {
      when (detailsLoadable) {
        is Loadable.Loading -> Header()
        is Loadable.Loaded ->
          Header(
            detailsLoadable.content,
            onFavorite = { onFavorite(detailsLoadable.content.id) },
            onRepost = { onRepost(detailsLoadable.content.id) },
            onShare = { onShare(detailsLoadable.content.url) }
          )
        is Loadable.Failed -> Unit
      }
    }
  }
}

@Composable
@MultiThemePreview
private fun LoadingPostDetailsPreview() {
  AutosTheme { PostDetails(Loadable.Loading(), commentsLoadable = ListLoadable.Loading()) }
}

@Composable
@MultiThemePreview
private fun LoadedPostDetailsWithoutComments() {
  AutosTheme {
    PostDetails(Loadable.Loaded(PostDetails.sample), commentsLoadable = ListLoadable.Empty())
  }
}

@Composable
@MultiThemePreview
private fun LoadedPostDetailsPreview() {
  AutosTheme {
    PostDetails(Loadable.Loaded(PostDetails.sample), commentsLoadable = ListLoadable.Loading())
  }
}

@Composable
private fun PostDetails(
  detailsLoadable: Loadable<PostDetails>,
  commentsLoadable: ListLoadable<PostPreview>,
  modifier: Modifier = Modifier
) {
  PostDetails(
    detailsLoadable,
    onDetailsChange = {},
    commentsLoadable,
    isTimelineRefreshing = false,
    onTimelineRefresh = {},
    onComment = {},
    onFavorite = {},
    onRepost = {},
    onShare = {},
    onNavigateToDetails = {},
    onNext = {},
    onBackwardsNavigation = {},
    BottomAreaAvailabilityNestedScrollConnection.empty,
    modifier
  )
}
