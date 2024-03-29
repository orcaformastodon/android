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

package com.jeanbarrossilva.orca.core.mastodon.auth.authentication

import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeMastodonInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.std.image.ImageLoader
import com.jeanbarrossilva.orca.std.image.SomeImageLoaderProvider
import com.jeanbarrossilva.orca.std.injector.Injector
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import java.net.URL
import kotlinx.serialization.Serializable

/**
 * Structure returned by the Mastodon API that holds the access token that's been given when
 * authorization was successfully granted to the user.
 *
 * @param accessToken Token that gives Orca user-level access to the API resources.
 */
@Serializable
internal data class MastodonAuthenticationToken(val accessToken: String) {
  /**
   * Converts this [MastodonAuthenticationToken] into an [authenticated][Actor.Authenticated]
   * [Actor].
   *
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   avatar will be loaded from a [URL].
   */
  suspend fun toActor(avatarLoaderProvider: SomeImageLoaderProvider<URL>): Actor.Authenticated {
    return toActor(
      avatarLoaderProvider,
      (Injector.from<CoreModule>().instanceProvider().provide() as SomeMastodonInstance)
        .client
        .get("/api/v1/accounts/verify_credentials") { bearerAuth(accessToken) }
        .body<MastodonAuthenticationVerification>()
    )
  }

  /**
   * Converts this [MastodonAuthenticationToken] into an [authenticated][Actor.Authenticated]
   * [Actor].
   *
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   avatar will be loaded from a [URL].
   * @param verification Result of verifying the user's credentials.
   */
  fun toActor(
    avatarLoaderProvider: SomeImageLoaderProvider<URL>,
    verification: MastodonAuthenticationVerification
  ): Actor.Authenticated {
    return verification.toActor(avatarLoaderProvider, accessToken)
  }
}
