package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequester
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import io.ktor.client.engine.mock.respondOk
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule

internal class DelayRequesterTests {
  @get:Rule val requesterRule = TestRequesterTestRule(TestRequester().delayedBy(2.minutes))

  @Test
  fun delaysGet() {
    runTest {
      requesterRule.on(coroutineContext).respond { respondOk("") }.requester.get<String>("api/v1")
      assertThat(@OptIn(ExperimentalCoroutinesApi::class) testScheduler.currentTime)
        .isEqualTo(2.minutes.inWholeMilliseconds)
    }
  }

  @Test
  fun delaysPost() {
    runTest {
      requesterRule.on(coroutineContext).requester.post("api/v1")
      assertThat(@OptIn(ExperimentalCoroutinesApi::class) testScheduler.currentTime)
        .isEqualTo(2.minutes.inWholeMilliseconds)
    }
  }
}