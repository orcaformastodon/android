package com.jeanbarrossilva.mastodonte.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jeanbarrossilva.loadable.list.ListLoadable
import com.jeanbarrossilva.loadable.list.flow.listLoadableFlow
import com.jeanbarrossilva.loadable.list.toSerializableList
import com.jeanbarrossilva.mastodonte.core.feed.FeedProvider
import com.jeanbarrossilva.mastodonte.core.toot.Toot
import com.jeanbarrossilva.mastodonte.core.toot.TootProvider
import com.jeanbarrossilva.mastodonte.platform.ui.core.context.ContextProvider
import com.jeanbarrossilva.mastodonte.platform.ui.core.context.share
import java.net.URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FeedViewModel(
    private val contextProvider: ContextProvider,
    private val feedProvider: FeedProvider,
    private val tootProvider: TootProvider,
    private val userID: String
) : ViewModel() {
    private val indexMutableFlow = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tootsLoadableFlow = indexMutableFlow
        .flatMapConcat {
            listLoadableFlow {
                feedProvider.provide(userID, it).map(List<Toot>::toSerializableList).collect(::load)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ListLoadable.Loading())

    fun favorite(tootID: String) {
        viewModelScope.launch {
            tootProvider.provide(tootID).first().toggleFavorite()
        }
    }

    fun reblog(tootID: String) {
        viewModelScope.launch {
            tootProvider.provide(tootID).first().toggleReblogged()
        }
    }

    fun share(url: URL) {
        contextProvider.provide().share("$url")
    }

    fun loadTootsAt(index: Int) {
        indexMutableFlow.value = index
    }

    companion object {
        fun createFactory(
            contextProvider: ContextProvider,
            feedProvider: FeedProvider,
            tootProvider: TootProvider,
            userID: String
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    FeedViewModel(contextProvider, feedProvider, tootProvider, userID)
                }
            }
        }
    }
}