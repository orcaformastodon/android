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

package com.jeanbarrossilva.orca.core.sharedpreferences.actor

import com.jeanbarrossilva.orca.core.feed.profile.post.content.TermMuter
import com.jeanbarrossilva.orca.core.sharedpreferences.actor.mirror.image.NoOpImageLoaderProviderFactory
import com.jeanbarrossilva.orca.core.sharedpreferences.feed.profile.post.content.SharedPreferencesTermMuter
import com.jeanbarrossilva.orca.platform.testing.context
import org.junit.rules.ExternalResource

internal class SharedPreferencesCoreTestRule : ExternalResource() {
  lateinit var actorProvider: SharedPreferencesActorProvider
    private set

  lateinit var termMuter: TermMuter
    private set

  override fun before() {
    actorProvider = SharedPreferencesActorProvider(context, NoOpImageLoaderProviderFactory)
    termMuter = SharedPreferencesTermMuter(context)
  }

  override fun after() {
    actorProvider.reset()
    SharedPreferencesTermMuter.reset(context)
  }
}
