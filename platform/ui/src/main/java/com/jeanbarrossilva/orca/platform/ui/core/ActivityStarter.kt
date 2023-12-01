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

package com.jeanbarrossilva.orca.platform.ui.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

/**
 * Starts an [Activity] with the configured settings.
 *
 * @param T [Activity] to be started.
 * @param context [Context] from which the [Activity] will be started.
 * @param activityClass [KClass] of the [Activity].
 */
class ActivityStarter<T : Activity>
@PublishedApi
internal constructor(private val context: Context, private val activityClass: KClass<T>) {
  /** Arguments to be passed to the [Intent]'s [extras][Intent.getExtras]. */
  private val args = hashMapOf<String, Any?>()

  /** [Intent]'s [flags][Intent.getFlags]. */
  private var flags = 0

  /**
   * Defines the [Activity] as the first task of a new [Activity] group to be created. Useful for
   * when it isn't started from another [Activity].
   *
   * @see Intent.FLAG_ACTIVITY_NEW_TASK
   */
  fun asNewTask(): ActivityStarter<T> {
    return apply { flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK }
  }

  /**
   * Defines the arguments to be passed to the [Intent]'s [extras][Intent.getExtras].
   *
   * @param args Key-value [Pair]s representing the arguments for the [Activity].
   */
  fun with(vararg args: Pair<String, Any?>): ActivityStarter<T> {
    return apply { args.forEach { this.args[it.first] = it.second } }
  }

  /**
   * Starts the [Activity].
   *
   * @see Context.startActivity
   */
  fun start() {
    val argsAsArray = args.map { (key, value) -> key to value }.toTypedArray()
    val extras = bundleOf(*argsAsArray)
    val intent = Intent(context, activityClass.java).putExtras(extras).addFlags(flags)
    context.startActivity(intent)
  }
}
