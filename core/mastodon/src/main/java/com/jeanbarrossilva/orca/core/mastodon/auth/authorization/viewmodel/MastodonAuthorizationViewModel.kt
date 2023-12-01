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

package com.jeanbarrossilva.orca.core.mastodon.auth.authorization.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jeanbarrossilva.loadable.Loadable
import com.jeanbarrossilva.loadable.flow.loadable
import com.jeanbarrossilva.loadable.ifLoaded
import com.jeanbarrossilva.orca.core.instance.Instance
import com.jeanbarrossilva.orca.core.instance.domain.Domain
import com.jeanbarrossilva.orca.core.mastodon.R
import com.jeanbarrossilva.orca.core.mastodon.auth.Mastodon
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.MastodonDomainsProvider
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.OnAccessTokenRequestListener
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.selectable.list.SelectableList
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.selectable.list.selectFirst
import com.jeanbarrossilva.orca.core.mastodon.instance.MastodonInstance
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.std.injector.Injector
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * [AndroidViewModel] that provides the [url] to be opened in the browser for authenticating the
 * user.
 *
 * @param application [Application] that allows [Context]-specific behavior.
 * @param onAccessTokenRequestListener [OnAccessTokenRequestListener] to be notified when an access
 *   token is requested.
 */
internal class MastodonAuthorizationViewModel
private constructor(
  application: Application,
  private val onAccessTokenRequestListener: OnAccessTokenRequestListener
) : AndroidViewModel(application) {
  /** [MutableStateFlow] to which the search query will be sent. */
  @OptIn(FlowPreview::class)
  private val searchQueryMutableFlow =
    emptyFlow<String>().debounce(256.milliseconds).mutableStateIn(viewModelScope, initialValue = "")

  /**
   * [MutableStateFlow] to which the [SelectableList]s of [Domain]s of [Instance]s wrapped by a
   * [Loadable] will be sent.
   */
  private val instanceDomainSelectablesLoadableMutableFlow =
    searchQueryMutableFlow
      .filterNot(String::isBlank)
      .map { query ->
        MastodonDomainsProvider.provide(query).sortedByDescending { domain ->
          domain.toString().startsWith(query)
        }
      }
      .onStart { emit(MastodonDomainsProvider.provide()) }
      .map(List<Domain>::selectFirst)
      .loadable(viewModelScope)

  /** [Application] with which this [MastodonAuthorizationViewModel] was created. */
  private val application
    @JvmName("_application") get() = getApplication<Application>()

  /** [StateFlow] version of the [searchQueryMutableFlow]. */
  val searchQueryFlow = searchQueryMutableFlow.asStateFlow()

  /** [StateFlow] version of [instanceDomainSelectablesLoadableMutableFlow]. */
  val instanceDomainSelectablesLoadableFlow =
    instanceDomainSelectablesLoadableMutableFlow.asStateFlow()

  /** [Url] to be opened in order to authenticate. */
  val url
    get() =
      URLBuilder()
        .takeFrom(
          (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance).url
        )
        .appendPathSegments("oauth", "authorize")
        .apply {
          with(application.getString(R.string.scheme)) {
            parameters["response_type"] = "code"
            parameters["client_id"] = Mastodon.CLIENT_ID
            parameters["redirect_uri"] = application.getString(R.string.redirect_uri, this)
            parameters["scope"] = Mastodon.SCOPES
          }
        }
        .build()

  /**
   * Searches for [Domain]s matching the given [query].
   *
   * @param query Query with which the [Domain]s will be filtered.
   */
  fun search(query: String) {
    searchQueryMutableFlow.value = query
  }

  /**
   * Selects the [instanceDomain].
   *
   * @param instanceDomain [Domain] to be selected.
   */
  fun select(instanceDomain: Domain) {
    with(instanceDomainSelectablesLoadableMutableFlow) {
      value.ifLoaded { value = Loadable.Loaded(select(instanceDomain)) }
    }
  }

  /**
   * Persists the currently selected [Domain], injects the derived [MastodonInstance] and notifies
   * the [onAccessTokenRequestListener].
   */
  fun authorize() {
    persistSelectedDomain()
    onAccessTokenRequestListener.onAccessTokenRequest()
  }

  /** Persists the [Domain] that's currently selected. */
  private fun persistSelectedDomain() {
    instanceDomainSelectablesLoadableFlow.value.ifLoaded {
      getPreferences(application).edit { putString(INSTANCE_DOMAIN_PREFERENCE_KEY, "$selected") }
    }
  }

  companion object {
    /**
     * Key through which the [Domain] of the [Instance] in which the user has been authorized can be
     * retrieved.
     */
    private const val INSTANCE_DOMAIN_PREFERENCE_KEY = "instance-domain"

    /**
     * Creates a [ViewModelProvider.Factory] that provides a [MastodonAuthorizationViewModel].
     *
     * @param application [Application] for [Context]-specific behavior.
     * @param onAccessTokenRequestListener [OnAccessTokenRequestListener] to be notified when an
     *   access token is requested.
     */
    fun createFactory(
      application: Application,
      onAccessTokenRequestListener: OnAccessTokenRequestListener
    ): ViewModelProvider.Factory {
      return viewModelFactory {
        initializer { MastodonAuthorizationViewModel(application, onAccessTokenRequestListener) }
      }
    }

    /**
     * Gets the [Domain] that's been persisted when the user was authorized.
     *
     * @throws IllegalStateException If this method is called before authorization has completed.
     */
    @Throws(IllegalStateException::class)
    fun getInstanceDomain(context: Context): Domain {
      return getPreferences(context)
        .getString(INSTANCE_DOMAIN_PREFERENCE_KEY, null)
        .let(::checkNotNull)
        .let(::Domain)
    }

    /**
     * Gets the [SharedPreferences] related to the authorization process.
     *
     * @param context [Context] from which the [SharedPreferences] will be obtained.
     */
    private fun getPreferences(context: Context): SharedPreferences {
      return context.getSharedPreferences("http-authorization", Context.MODE_PRIVATE)
    }
  }
}
