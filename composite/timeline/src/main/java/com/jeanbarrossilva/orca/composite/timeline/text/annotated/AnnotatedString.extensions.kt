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

package com.jeanbarrossilva.orca.composite.timeline.text.annotated

import androidx.compose.ui.text.AnnotatedString
import com.jeanbarrossilva.orca.composite.timeline.text.annotated.span.StyleExtractor
import com.jeanbarrossilva.orca.std.styledstring.StyledString

/** Converts this [AnnotatedString] into a [StyledString]. */
fun AnnotatedString.toStyledString(): StyledString {
  val styles = spanStyles.flatMap { StyleExtractor.extractAll(it.item, it.start..it.end.dec()) }
  return StyledString(text, styles)
}