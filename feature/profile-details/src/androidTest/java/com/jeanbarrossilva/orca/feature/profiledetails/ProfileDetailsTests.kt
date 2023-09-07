package com.jeanbarrossilva.orca.feature.profiledetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.platform.app.InstrumentationRegistry
import com.jeanbarrossilva.loadable.Loadable
import com.jeanbarrossilva.loadable.list.toListLoadable
import com.jeanbarrossilva.loadable.list.toSerializableList
import com.jeanbarrossilva.orca.platform.theme.OrcaTheme
import com.jeanbarrossilva.orca.platform.ui.component.timeline.TIMELINE_TAG
import com.jeanbarrossilva.orca.platform.ui.component.timeline.toot.TootPreview
import com.jeanbarrossilva.orca.platform.ui.test.component.timeline.onTimeline
import java.util.UUID
import kotlin.test.Test
import org.junit.Rule

internal class ProfileDetailsTests {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsUsernameWhenScrollingPastHeader() {
        val screenHeightInPx = InstrumentationRegistry
            .getInstrumentation()
            .context
            .resources
            .displayMetrics
            .heightPixels
        composeRule.setContent {
            OrcaTheme {
                ProfileDetails(
                    Loadable.Loaded(ProfileDetails.sample),
                    tootPreviewsLoadable = List(size = screenHeightInPx) {
                        TootPreview.sample.copy(id = "${UUID.randomUUID()}")
                    }
                        .toSerializableList()
                        .toListLoadable()
                )
            }
        }
        composeRule.onTimeline().performScrollToIndex(1)
        composeRule.onNodeWithTag(TIMELINE_TAG).assertIsDisplayed()
    }
}