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

package com.jeanbarrossilva.orca.std.injector.module.injection

import com.jeanbarrossilva.orca.std.injector.module.Module

/**
 * Creates an [Injection].
 *
 * @param T Dependency to be injected.
 * @param creation Returns the dependency to be injected that can be lazily retrieved afterwards.
 */
inline fun <reified T : Any> injectionOf(crossinline creation: Module.() -> T): Injection<T> {
  return object : Injection<T>() {
    override val dependencyClass = T::class

    override fun Module.create(): T {
      return creation()
    }
  }
}
