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

package com.jeanbarrossilva.orca.composite.timeline.post.figure.gallery.thumbnail

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.composite.timeline.avatar.sample
import com.jeanbarrossilva.orca.composite.timeline.test.post.figure.gallery.thumbnail.onThumbnail
import com.jeanbarrossilva.orca.core.feed.profile.post.Author
import com.jeanbarrossilva.orca.core.feed.profile.post.content.Attachment
import com.jeanbarrossilva.orca.core.sample.feed.profile.post.content.sample
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import org.junit.Rule
import org.junit.Test

internal class ThumbnailTests {
  @get:Rule val composeRule = createComposeRule()

  @Test
  fun callsOnClick() {
    var hasOnClickBeenCalled = false
    composeRule
      .apply {
        setContent {
          AutosTheme {
            Thumbnail(
              Author.sample.name,
              Attachment.sample,
              position = 1,
              onClick = { hasOnClickBeenCalled = true }
            )
          }
        }
      }
      .onThumbnail()
      .performClick()
    assertThat(hasOnClickBeenCalled).isTrue()
  }
}
