package com.example.catbreeds.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

class TheCatApi(
    private val client: HttpClient,
    private val apiKeyProvider: ApiKeyProvider,
) {
    suspend fun getBreeds(page: Int, limit: Int): List<BreedDto> {
        // TheCatAPI supports pagination on /v1/breeds using page + limit.
        // If key is missing, request still often works but may be rate limited.
        val key = apiKeyProvider.apiKeyOrNull()
        return client.get("https://api.thecatapi.com/v1/breeds") {
            parameter("page", page)
            parameter("limit", limit)
            if (!key.isNullOrBlank()) header("x-api-key", key)
        }.body()
    }
}

interface ApiKeyProvider {
    fun apiKeyOrNull(): String?
}

@Serializable
data class BreedDto(
    val id: String,
    val name: String,
    val description: String = "",
    val origin: String? = null,
    val temperament: String? = null,
    val life_span: String? = null,
    val image: ImageDto? = null,
)

@Serializable
data class ImageDto(
    val url: String? = null
)
