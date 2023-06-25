package com.jeanbarrossilva.mastodonte.platform.ui.timeline.toot

import android.icu.text.CompactDecimalFormat
import java.util.Locale

/** Formats this [Int] so that it is more user-friendly. **/
internal val Int.formatted: String
    get() = CompactDecimalFormat
        .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)
