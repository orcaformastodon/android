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

package com.jeanbarrossilva.orca.composite.timeline.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.jeanbarrossilva.orca.std.styledstring.StyledString
import com.jeanbarrossilva.orca.std.styledstring.style.Style
import com.jeanbarrossilva.orca.std.styledstring.style.type.Bold
import com.jeanbarrossilva.orca.std.styledstring.style.type.Italic

/**
 * Extracts a [StyledString]'s [Style] from the [SpanStyle] of an [AnnotatedString].
 *
 * @see extract
 */
internal enum class StyleExtractor {
  /**
   * Extracts an italic [Style] from a [SpanStyle].
   *
   * @see Italic
   */
  Italic {
    override fun extract(spanStyle: SpanStyle, indices: IntRange): Style? {
      return if (ItalicSpanStyle in spanStyle) {
        Italic(indices)
      } else {
        null
      }
    }
  },
  /**
   * Extracts a bold [Style] from a [SpanStyle].
   *
   * @see Bold
   */
  Bold {
    override fun extract(spanStyle: SpanStyle, indices: IntRange): Style? {
      return if (BoldSpanStyle in spanStyle) {
        Bold(indices)
      } else {
        null
      }
    }
  };

  /**
   * Extracts the [Style] that matches this [StyleExtractor] from the given [spanStyle].
   *
   * @param [spanStyle] [SpanStyle] to extract a [Style] from.
   * @param indices Indices at which the [spanStyle] is applied.
   */
  abstract fun extract(spanStyle: SpanStyle, indices: IntRange): Style?

  companion object {
    /**
     * Extract all [Style] that match the existing [StyleExtractor]s from the given [spanStyle].
     *
     * @param spanStyle [SpanStyle] to extract the [Style]s from.
     * @param indices Indices at which the [spanStyle] is applied.
     */
    fun extractAll(spanStyle: SpanStyle, indices: IntRange): List<Style> {
      return entries.mapNotNull { it.extract(spanStyle, indices) }
    }
  }
}
