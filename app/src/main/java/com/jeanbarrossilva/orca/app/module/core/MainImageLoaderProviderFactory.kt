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

package com.jeanbarrossilva.orca.app.module.core

import com.jeanbarrossilva.orca.core.sharedpreferences.actor.mirror.image.ImageLoaderProviderFactory
import com.jeanbarrossilva.orca.platform.core.image.sample
import com.jeanbarrossilva.orca.std.image.SomeImageLoaderProvider
import com.jeanbarrossilva.orca.std.image.compose.ComposableImageLoader
import com.jeanbarrossilva.orca.std.image.compose.async.AsyncImageLoader
import java.net.URL

internal object MainImageLoaderProviderFactory : ImageLoaderProviderFactory() {
  override fun createForSampleImageSource(): SomeImageLoaderProvider<Any> {
    @Suppress("UNCHECKED_CAST")
    return ComposableImageLoader.Provider.sample as SomeImageLoaderProvider<Any>
  }

  override fun createForURL(): SomeImageLoaderProvider<URL> {
    return AsyncImageLoader.Provider
  }
}
