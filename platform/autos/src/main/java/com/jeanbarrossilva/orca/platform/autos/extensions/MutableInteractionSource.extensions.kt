package com.jeanbarrossilva.orca.platform.autos.extensions

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * [MutableInteractionSource] that ignores all specified [Interaction]s.
 *
 * @param ignored [KClass]es of the [Interaction]s to be ignored.
 */
@Suppress("FunctionName")
fun IgnoringMutableInteractionSource(
  vararg ignored: KClass<out Interaction>
): MutableInteractionSource {
  return object : MutableInteractionSource {
    /** Whether this [Interaction] is ignored. */
    private val Interaction.isIgnored
      get() = ignored.any(this::class::isSubclassOf)

    override val interactions = MutableSharedFlow<Interaction>()

    override suspend fun emit(interaction: Interaction) {
      if (!interaction.isIgnored) {
        this.interactions.emit(interaction)
      }
    }

    override fun tryEmit(interaction: Interaction): Boolean {
      return !interaction.isIgnored && interactions.tryEmit(interaction)
    }
  }
}
