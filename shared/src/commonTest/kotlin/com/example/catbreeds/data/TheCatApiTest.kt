package com.example.catbreeds.data

import com.example.catbreeds.data.remote.ApiKeyProvider
import com.example.catbreeds.data.remote.TheCatApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TheCatApiTest {

    @Test
    fun `parses breeds response`() = kotlinx.coroutines.test.runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """[
                  { "id":"abys","name":"Abyssinian","description":"desc","origin":"Egypt","temperament":"Active","life_span":"14","image":{"url":"https://x/y.jpg"} }
                ]""",
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = TheCatApi(client, object : ApiKeyProvider { override fun apiKeyOrNull(): String? = null })
        val breeds = api.getBreeds(page = 0, limit = 20)

        assertEquals(1, breeds.size)
        assertEquals("abys", breeds.first().id)
        assertEquals("Abyssinian", breeds.first().name)
    }
}
