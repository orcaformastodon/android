package com.jeanbarrossilva.orca.core.mastodon.http.requester

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.mastodon.http.client.CoreHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.content.PartData
import kotlin.reflect.KClass
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

/**
 * Manages the sending, continuity and repetition of asynchronous HTTP requests.
 *
 * @see Request
 * @see Request.Get
 * @see Request.Post
 */
internal abstract class Requester {
  /** Requests that have been interrupted unexpectedly and are retained for later execution. */
  protected open val retained = hashSetOf<Request>()

  /** [CoroutineScope] in which the requests are performed. */
  open val coroutineScope = CoroutineScope(Dispatchers.IO)

  /** [CoreHttpClient] to which requests are sent. */
  abstract val client: HttpClient

  /**
   * [Requester] that sends HTTP requests as an [unauthenticated][Actor.Unauthenticated] [Actor].
   */
  open class Unauthenticated(override val client: HttpClient) : Requester() {
    override suspend fun <T> onGet(route: String, resourceClass: KClass<T & Any>): T {
      return client.get(route).body(resourceClass)
    }

    override suspend fun onPost(route: String, form: List<PartData>): HttpResponse {
      return client.post(route, form)
    }

    /**
     * Returns a version of this [Requester] that sends requests as an
     * [authenticated][Actor.Authenticated] [Actor].
     *
     * @param lock [AuthenticationLock] by which authentication will be required.
     */
    fun authenticated(lock: SomeAuthenticationLock): Requester {
      return object : Requester() {
        override val client = this@Unauthenticated.client

        override suspend fun <T> onGet(route: String, resourceClass: KClass<T & Any>): T {
          return client.get(route) { authenticate() }.body(resourceClass)
        }

        override suspend fun onPost(route: String, form: List<PartData>): HttpResponse {
          return client.post(route, form) { authenticate() }
        }

        /**
         * Provides the [authenticated][Actor.Authenticated] [Actor]'s access token to the
         * [Authorization][HttpHeaders.Authorization] header through the [lock].
         */
        private suspend fun HttpMessageBuilder.authenticate() {
          lock.requestUnlock { bearerAuth(it.accessToken) }
        }
      }
    }
  }

  /**
   * Sends a GET request to the [route].
   *
   * @param T Resource to be obtained.
   * @param route [String] that's the path for the resource to be obtained.
   */
  suspend inline fun <reified T : Any> get(route: String): T {
    return get(route, T::class)
  }

  /**
   * Sends a GET request to the [route].
   *
   * @param T Resource to be obtained.
   * @param route [String] that's the path for the resource to be obtained.
   * @param resourceClass [KClass] of the resource to be obtained.
   */
  suspend fun <T> get(route: String, resourceClass: KClass<T & Any>): T {
    return coroutineScope
      .async { onGet(route, resourceClass) }
      .retainOnCancellation { Request.Get(route, resourceClass) }
      .await()
  }

  /**
   * Sends a POST request to the [route].
   *
   * @param route [String] that's the path to which the request will be sent.
   * @param form Multipart form data.
   */
  suspend fun post(route: String, form: List<PartData> = emptyList()): HttpResponse {
    return coroutineScope
      .async { onPost(route, form) }
      .retainOnCancellation { Request.Post(route, form) }
      .await()
  }

  /**
   * Tries to re-send all requests that are currently retained.
   *
   * @see retained
   */
  suspend fun resume() {
    retained.forEach { it.sendTo(this) }
  }

  /**
   * Callback run whenever a GET request is sent to the [route].
   *
   * @param T Resource to be obtained.
   * @param route [String] that's the path for the resource to be obtained.
   * @param resourceClass [KClass] of the resource to be obtained.
   */
  protected abstract suspend fun <T> onGet(route: String, resourceClass: KClass<T & Any>): T

  /**
   * Callback run whenever a POST request is sent to the [route].
   *
   * @param route [String] that's the path to which the request will be sent.
   * @param form Multipart form data.
   */
  protected abstract suspend fun onPost(route: String, form: List<PartData>): HttpResponse

  /**
   * Retains the result of [request] if this [Job] gets cancelled.
   *
   * @param T Job whose completion will be listened to.
   * @param request Lazily creates the [Request] to be retained.
   */
  private fun <T : Job> T.retainOnCancellation(request: () -> Request): T {
    invokeOnCompletion {
      if (it is CancellationException) {
        retained.add(request())
      }
    }
    return this
  }

  companion object {
    /**
     * Creates a [Requester] that sends HTTP requests as an [unauthenticated][Actor.Unauthenticated]
     * [Actor] by default through the given [client].
     *
     * @param client [CoreHttpClient] through which requests will be sent.
     * @see Unauthenticated
     * @see Unauthenticated.authenticated
     */
    fun through(client: HttpClient): Unauthenticated {
      return Unauthenticated(client)
    }
  }
}