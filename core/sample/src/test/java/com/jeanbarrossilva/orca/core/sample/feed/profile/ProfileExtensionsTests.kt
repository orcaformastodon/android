package com.jeanbarrossilva.orca.core.sample.feed.profile

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.toot.Author
import com.jeanbarrossilva.orca.core.feed.profile.toot.Toot
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.sample
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.toot.sample
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.toot.samples
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

internal class ProfileExtensionsTests {
  @Test
  fun `GIVEN a sample profile WHEN getting its toots THEN they are the sample ones`() {
    runTest {
      assertContentEquals(
        Toot.samples.filter { it.author == Author.sample }.take(SampleProfile.TOOTS_PER_PAGE),
        Profile.sample.getToots(0).first()
      )
    }
  }
}
