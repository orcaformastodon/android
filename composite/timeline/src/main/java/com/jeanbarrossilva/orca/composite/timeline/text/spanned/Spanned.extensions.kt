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

package com.jeanbarrossilva.orca.composite.timeline.text.spanned

import android.text.Spanned
import androidx.core.text.getSpans
import com.jeanbarrossilva.orca.std.styledstring.StyledString

/** [Part]s by which this [Spanned] is composed. */
internal val Spanned.parts
  get() =
    mergeSpansIntoParts().fold(emptyList<Part>()) { accumulator, part ->
      if (accumulator.isNotEmpty() && part.indices.first > accumulator.last().indices.last.inc()) {
        accumulator + Part(accumulator.last().indices.last.inc()..part.indices.first.dec()) + part
      } else {
        accumulator + part
      }
    }

/** Converts this [Spanned] into a [StyledString]. */
fun Spanned.toStyledString(): StyledString {
  val text = toString()
  val styles = parts.filterIsInstance<Part.Spanned>().flatMap(Part.Spanned::toStyles)
  return StyledString(text, styles)
}

/**
 * Merges all spans that have been applied to this [Spanned] into ordered spanned [Part]s, from
 * which the spans and also their respective indices can be obtained.
 *
 * @see Part.Spanned
 * @see Part.Spanned.getIndices
 */
private fun Spanned.mergeSpansIntoParts(): List<Part.Spanned> {
  return getSpans<Any>().map { Part(getSpanStart(it)..getSpanEnd(it).dec()).span(it) }
}
