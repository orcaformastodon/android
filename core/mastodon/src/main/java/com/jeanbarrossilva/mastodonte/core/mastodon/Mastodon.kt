package com.jeanbarrossilva.mastodonte.core.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

internal object Mastodon {
    @Suppress("SpellCheckingInspection")
    const val CLIENT_ID = "F2Rx9d7C3x45KRVJ9rU4IjIJgrsjzaq74bSLo__VUG0"

    val scopes = arrayOf("read", "write", "follow").joinToString("+")

    val clientSecret: String
        get() = System.getenv("MASTODON_CLIENT_SECRET")

    val HttpClient = HttpClient(CIO) {
        defaultRequest { url("https://mastodon.social") }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true

                    @OptIn(ExperimentalSerializationApi::class)
                    namingStrategy = JsonNamingStrategy.SnakeCase
                }
            )
        }
    }
}
