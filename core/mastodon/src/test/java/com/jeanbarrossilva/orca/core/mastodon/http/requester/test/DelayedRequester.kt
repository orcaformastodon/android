package com.jeanbarrossilva.orca.core.mastodon.http.requester.test

import io.ktor.client.statement.HttpResponse
import io.ktor.http.content.PartData
import kotlin.time.Duration
import kotlinx.coroutines.delay

/**
 * [TestRequester] that performs all of the [delegate]'s operations with an initial [delay].
 *
 * @param delegate [TestRequester] this [DelayedRequester]'s functionality will be delegated.
 * @param delay [Duration] to wait for before sending a request.
 */
internal class DelayedRequester(val delegate: SomeTestRequester, val delay: Duration) :
  TestRequester<DelayedRequester>() {
  override val retained = delegate.retained
  override val client = delegate.client

  override suspend fun onGet(route: String): HttpResponse {
    delay()
    return delegate.get(route)
  }

  override suspend fun onPost(route: String, form: List<PartData>): HttpResponse {
    delay()
    return delegate.post(route, form)
  }

  /** Suspends for the specified amount of [delay]. */
  private suspend fun delay() {
    delay(delay)
  }
}
