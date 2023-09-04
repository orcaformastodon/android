package com.jeanbarrossilva.orca.core.feed.profile.toot.style.type

import com.jeanbarrossilva.orca.core.feed.profile.toot.style.Style
import java.net.URL

/**
 * [Style] for referencing to a username.
 *
 * @param indices Indices at which both the style symbols and the target are in the whole [String].
 * @param url [URL] that leads to the owner of the mentioned username.
 **/
data class Mention(override val indices: IntRange, val url: URL) : Style() {
    /** [Style.Delimiter] for [Mention]s. **/
    sealed class Delimiter : Style.Delimiter() {
        /**
         * [Delimiter] that's the [parent] of all [Variant]s and considers [Mention]s parts of a
         * [String] starting with a [Mention.SYMBOL] and determines its end by the absence of
         * alphanumeric or some other special characters.
         **/
        class Default private constructor() : Delimiter() {
            override val parent = null

            override fun getRegex(): Regex {
                return Regex(SYMBOL + "$targetRegex")
            }

            override fun onGetTarget(match: String): String {
                return match.removePrefix("$SYMBOL")
            }

            override fun onTarget(target: String): String {
                return SYMBOL + target
            }

            companion object {
                /** Single [Default] instance. **/
                internal val instance = Default()
            }
        }

        /** [Delimiter] that's a child of [Default]. **/
        abstract class Variant : Delimiter() {
            final override val parent = Default.instance
        }
    }

    companion object {
        /** [Char] that indicates the start of a [Mention] by default. **/
        internal const val SYMBOL = '@'

        /** [Regex] that matches a [Mention]'s target. **/
        val targetRegex = Regex("[a-zA-Z0-9._%+-]+")
    }
}
