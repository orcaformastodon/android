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

package com.jeanbarrossilva.orca.std.buildable.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jeanbarrossilva.orca.std.buildable.Buildable

/** Gets the [KSClassDeclaration]s that have been annotated with [Buildable]. */
internal fun Resolver.getBuildableDeclarations(): Sequence<KSClassDeclaration> {
  return getSymbolsWithAnnotation(Buildable::class.java.name).filterIsInstance<KSClassDeclaration>()
}
