package com.jeanbarrossilva.mastodonte.core.mastodon.toot

import com.jeanbarrossilva.mastodonte.core.mastodon.client.MastodonHttpClient
import com.jeanbarrossilva.mastodonte.core.mastodon.client.authenticateAndGet
import com.jeanbarrossilva.mastodonte.core.mastodon.client.authenticateAndPost
import com.jeanbarrossilva.mastodonte.core.toot.Author
import com.jeanbarrossilva.mastodonte.core.toot.Toot
import io.ktor.client.call.body
import java.net.URL
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class MastodonToot(
    override val id: String,
    override val author: Author,
    override val content: String,
    override val publicationDateTime: ZonedDateTime,
    override val commentCount: Int,
    override val isFavorite: Boolean,
    override val favoriteCount: Int,
    override val isReblogged: Boolean,
    override val reblogCount: Int,
    override val url: URL
) : Toot() {
    override suspend fun setFavorite(isFavorite: Boolean) {
        @Suppress("SpellCheckingInspection")
        val route =
            if (isFavorite) "/api/v1/statuses/$id/favourite" else "/api/v1/statuses/$id/unfavourite"

        MastodonHttpClient.authenticateAndPost(route)
    }

    override suspend fun setReblogged(isReblogged: Boolean) {
        @Suppress("SpellCheckingInspection")
        val route =
            if (isReblogged) "/api/v1/statuses/:id/reblog" else "/api/v1/statuses/:id/unreblog"

        MastodonHttpClient.authenticateAndPost(route)
    }

    override suspend fun getComments(page: Int): Flow<List<Toot>> {
        return flow {
            MastodonHttpClient
                .authenticateAndGet("/api/v1/statuses/$id/context")
                .body<Context>()
                .descendants
                .map { it.toToot() }
                .also { emit(it) }
        }
    }
}
