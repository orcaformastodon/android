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

package com.jeanbarrossilva.orca.feature.postdetails.conversion

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.composite.timeline.stat.details.StatsDetails
import com.jeanbarrossilva.orca.feature.postdetails.toPostDetails
import kotlin.test.Test

internal class DetailingTests {
  @Test
  fun createsDetailsWithSpecifiedCommentCount() {
    runPostDetailsConversionTest {
      assertThat(detailed().withCommentCountOf(1).stats.formattedCommentCount)
        .isEqualTo(StatsDetails.sample.copy(commentCount = 1).formattedCommentCount)
    }
  }

  @Test
  fun createsFavoritedDetails() {
    runPostDetailsConversionTest { assertThat(detailed().favorited().stats.isFavorite).isTrue() }
  }

  @Test
  fun createsUnfavoritedDetails() {
    runPostDetailsConversionTest { assertThat(detailed().unfavorited().stats.isFavorite).isFalse() }
  }

  @Test
  fun createsRepostedDetails() {
    runPostDetailsConversionTest { assertThat(detailed().reposted().stats.isReposted).isTrue() }
  }

  @Test
  fun createsUnrepostedDetails() {
    runPostDetailsConversionTest { assertThat(detailed().unreposted().stats.isReposted).isFalse() }
  }

  @Test
  fun createsDetailsFlow() {
    runPostDetailsConversionTest {
      detailed().asFlow().test {
        assertThat(awaitItem())
          .isEqualTo(post().toPostDetails(colors, onLinkClick, onThumbnailClickListener))
      }
    }
  }
}
