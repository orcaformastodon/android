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

package com.jeanbarrossilva.orca.platform.testing.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.createGraph
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import androidx.navigation.get
import com.jeanbarrossilva.orca.platform.testing.R
import com.jeanbarrossilva.orca.platform.testing.databinding.ActivitySingleDestinationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** [FragmentActivity] that hosts a single [Fragment]. */
abstract class SingleFragmentActivity : FragmentActivity() {
  /**
   * [ActivitySingleDestinationBinding] through which [View]s from the layout resource file can be
   * referenced. It's available after this [SingleFragmentActivity] is created and set to `null`
   * when it's destroyed.
   *
   * @see R.layout.activity_single_destination
   */
  private var binding: ActivitySingleDestinationBinding? = null

  /** [FragmentOnAttachListener] that sets [arguments] as the [Fragment]'s. */
  private val fragmentArgumentsSettingOnAttachListener = FragmentOnAttachListener { _, fragment ->
    fragment.arguments = arguments
  }

  /**
   * [NavController.OnDestinationChangedListener] that ensures the integrity of the [navGraph].
   *
   * @see ensureIntegrity
   */
  private val navGraphIntegrityInsuranceOnDestinationChangedListener =
    NavController.OnDestinationChangedListener { _, _, _ -> ensureIntegrity(navGraph) }

  /**
   * [navHostFragment]'s child [FragmentManager].
   *
   * @see NavHostFragment.getChildFragmentManager
   */
  private val navHostChildFragmentManager
    get() = navHostFragment.childFragmentManager

  /**
   * [NavHostFragment] of [binding]'s
   * [containerView][ActivitySingleDestinationBinding.containerView].
   */
  private val navHostFragment
    get() = requireNotNull(binding?.root).getFragment<NavHostFragment>()

  /** [NavGraph] to which the [NavDestination] pointing to the [Fragment] is added. */
  private var navGraph
    get() = navController.graph
    set(navGraph) {
      ensureIntegrity(navGraph)
      navGraphIntegrityInsuranceJob?.invokeOnCompletion { cause ->
        if (cause == null) {
          lifecycleScope.launch {
            navController.graph = navGraph
            onNavGraphChangeListeners.onEach(OnNavGraphChangeListener::onNavGraphChange).clear()
          }
        }
      }
    }

  /** [Job] that ensures the [navGraph]'s integrity. */
  internal var navGraphIntegrityInsuranceJob: Job? = null
    private set

  /**
   * [CoroutineScope] in which the [navGraph]'s integrity is ensured.
   *
   * @see navGraphIntegrityInsuranceJob
   */
  private val navGraphIntegrityInsuranceScope = CoroutineScope(Dispatchers.Default)

  /** Currently active [OnNavGraphChangeListener]s. */
  private val onNavGraphChangeListeners = mutableListOf<OnNavGraphChangeListener>()

  /** [navHostFragment]'s [navController][NavHostFragment.navController]. */
  internal val navController
    get() = navHostFragment.navController

  /**
   * Arguments to be passed to the [Fragment]. Defaults to this [SingleFragmentActivity]'s
   * [intent][getIntent]'s [extras][Intent.getExtras].
   */
  protected open val arguments: Bundle?
    get() = intent.extras

  /** [Route][NavDestination.route] of the [NavDestination]. */
  protected abstract val route: String

  /** Listener that's notified whenever the [navGraph] changes. */
  internal fun interface OnNavGraphChangeListener {
    /** Callback run whenever the [navGraph] changes. */
    fun onNavGraphChange()
  }

  /**
   * [IllegalStateException] thrown if a destination isn't added to this [SingleFragmentActivity].
   *
   * @see add
   */
  class NoDestinationException internal constructor() :
    IllegalStateException("SingleFragmentActivity should have a destination.")

  /**
   * [IllegalStateException] thrown if the added [NavDestination]'s [route][NavDestination.route]
   * doesn't match the [SingleFragmentActivity]'s.
   *
   * @param route Inequivalent [route][NavDestination.route] that's been assigned to the
   *   [NavDestination].
   * @see SingleFragmentActivity.route
   */
  class InequivalentDestinationRouteException internal constructor(route: String) :
    IllegalStateException("Destination route doesn't match SingleFragmentActivity's ($route).")

  /** [IllegalStateException] thrown if the added [NavDestination] doesn't lead to a [Fragment]. */
  class NonFragmentDestinationException internal constructor() :
    IllegalStateException(
      "SingleFragmentActivity should have a destination that leads to a Fragment."
    )

  /**
   * [IllegalStateException] thrown if multiple destinations are added to this
   * [SingleFragmentActivity].
   */
  class MultipleDestinationsException internal constructor() :
    IllegalStateException("SingleFragmentActivity should only have one destination.")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySingleDestinationBinding.inflate(layoutInflater)
    navHostChildFragmentManager.addFragmentOnAttachListener(
      fragmentArgumentsSettingOnAttachListener
    )
    navController.addOnDestinationChangedListener(
      navGraphIntegrityInsuranceOnDestinationChangedListener
    )
    navGraph = navController.createGraph(startDestination = route) { add() }
    setContentView(binding?.root)
  }

  override fun onDestroy() {
    navGraphIntegrityInsuranceScope.cancel()
    navController.removeOnDestinationChangedListener(
      navGraphIntegrityInsuranceOnDestinationChangedListener
    )
    navHostChildFragmentManager.removeFragmentOnAttachListener(
      fragmentArgumentsSettingOnAttachListener
    )
    binding = null
    super.onDestroy()
  }

  /**
   * Adds a [FragmentNavigator.Destination] whose [route][FragmentNavigator.Destination.route] is
   * the same as this [SingleFragmentActivity]'s.
   *
   * @see SingleFragmentActivity.route
   */
  protected inline fun <reified T : Fragment> NavGraphBuilder.fragment() {
    fragment<T>(this@SingleFragmentActivity.route) {}
  }

  /**
   * Adds the [NavDestination] to the [NavGraphBuilder].
   *
   * Note that its [route][NavDestination.route] has to be the same as this
   * [SingleFragmentActivity]'s.
   *
   * @see NavGraphBuilder.fragment
   * @see SingleFragmentActivity.route
   */
  protected abstract fun NavGraphBuilder.add()

  /**
   * Callback that's called if no [NavDestination] has been added through [add].
   *
   * Throws a [NoDestinationException] by default.
   */
  internal open fun onNoDestination() {
    throw NoDestinationException()
  }

  /**
   * Callback that's called if the [NavDestination]'s [route][NavDestination.route] doesn't match
   * this [SingleFragmentActivity]'s.
   */
  internal open fun onInequivalentDestinationRoute() {
    throw InequivalentDestinationRouteException(route)
  }

  /**
   * Callback that's called if multiple [NavDestination]s have been added through [add].
   *
   * Throws a [MultipleDestinationsException] by default.
   */
  internal open fun onMultipleDestinations() {
    throw MultipleDestinationsException()
  }

  /**
   * Callback that's called if the current [NavDestination] doesn't lead to a [Fragment].
   *
   * Throws a [NoDestinationException] by default.
   */
  internal open fun onNonFragmentDestination() {
    throw NonFragmentDestinationException()
  }

  /**
   * Notifies the [listener] when the [navGraph] is changed.
   *
   * @param listener [OnNavGraphChangeListener] to be notified.
   */
  internal fun doOnNavGraphChange(listener: OnNavGraphChangeListener) {
    if (navController.containsNavGraph) {
      listener.onNavGraphChange()
    } else {
      onNavGraphChangeListeners.add(listener)
    }
  }

  /**
   * Ensures that the state in which the [navGraph] currently is is an integral one.
   *
   * All operations are run in the [navGraphIntegrityInsuranceScope].
   *
   * @param navGraph [NavGraph] to perform the insurance on.
   * @throws NoDestinationException If no [NavDestination] has been added through [add].
   * @throws MultipleDestinationsException If multiple [NavDestination]s have been added through
   *   [add].
   * @throws NonFragmentDestinationException If the [NavDestination] doesn't lead to a [Fragment].
   */
  private fun ensureIntegrity(navGraph: NavGraph) {
    navGraphIntegrityInsuranceJob =
      navGraphIntegrityInsuranceScope
        .launch {
          ensureHasSingleDestination(navGraph)
          ensureDestinationRouteIsEquivalent(navGraph)
          ensureDestinationLeadsToFragment(navGraph)
        }
        .also { it.invokeOnCompletion { navGraphIntegrityInsuranceJob = null } }
  }

  /**
   * Ensures that the [navGraph] has exactly one [NavDestination].
   *
   * @param navGraph [NavGraph] to perform the insurance on.
   * @throws NoDestinationException If no [NavDestination] has been added through [add].
   * @throws MultipleDestinationsException If multiple [NavDestination]s have been added through
   *   [add].
   */
  private fun ensureHasSingleDestination(navGraph: NavGraph) {
    val destinationCount = navGraph.count()
    if (destinationCount == 0) {
      onNoDestination()
    } else if (destinationCount != 1) {
      onMultipleDestinations()
    }
  }

  /**
   * Ensures that [navGraph]'s [NavDestination]'s [route][NavDestination.route] matches this
   * [SingleFragmentActivity]'s.
   *
   * @throws InequivalentDestinationRouteException If the [NavDestination]'s
   *   [route][NavDestination.route] doesn't match this [SingleFragmentActivity]'s.
   * @see SingleFragmentActivity.route
   */
  private fun ensureDestinationRouteIsEquivalent(navGraph: NavGraph) {
    val destination = navGraph.findNode(route)
    val isRouteInequivalent = destination == null
    if (isRouteInequivalent) {
      onInequivalentDestinationRoute()
    }
  }

  /**
   * Ensures that [navGraph]'s [NavDestination] leads to a [Fragment].
   *
   * @param navGraph [NavGraph] to perform the insurance on.
   * @throws NonFragmentDestinationException If the [NavDestination] doesn't lead to a [Fragment].
   */
  private fun ensureDestinationLeadsToFragment(navGraph: NavGraph) {
    val destination = navGraph[navGraph.startDestinationId]
    val isNonFragmentDestination = destination.navigatorName != "fragment"
    if (isNonFragmentDestination) {
      onNonFragmentDestination()
    }
  }
}
