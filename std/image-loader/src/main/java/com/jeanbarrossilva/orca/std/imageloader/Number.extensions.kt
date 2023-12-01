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

package com.jeanbarrossilva.orca.std.imageloader

/**
 * Returns [max] if this [Int] is less than [min] or [min] if this is greater than [max]; otherwise,
 * this [Int] itself is returned.
 *
 * @param min Minimum [Int] to be returned if this one exceeds [max].
 * @param max Maximum [Int] to be returned if this one is less than [min].
 */
internal fun Int.mirror(min: Int, max: Int): Int {
  return if (this < min) max else if (this > max) min else this
}
