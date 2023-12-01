/*
 * Copyright © 2023 Orca
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

import com.jeanbarrossilva.orca.namespaceFor

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)

  kotlin("plugin.serialization")
}

android {
  buildFeatures.compose = true
  composeOptions.kotlinCompilerExtensionVersion = libs.versions.android.compose.get()
  namespace = namespaceFor("std.imageloader.compose")
}

dependencies {
  api(project(":std:image-loader"))
  api(libs.android.compose.ui.tooling)

  implementation(project(":platform:autos"))
  implementation(project(":std:image-loader:local"))
  implementation(libs.android.core)
  implementation(libs.coil)
  implementation(libs.loadable.placeholder)
}
