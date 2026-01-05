package com.example.catbreeds.domain

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.catbreeds.data.remote.ApiKeyProvider
import com.example.catbreeds.data.remote.TheCatApi
import com.example.catbreeds.data.repo.BreedRepositoryImpl
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.domain.model.Breed
import com.example.catbreeds.domain.usecase.GetBreedsPageUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

class GetBreedsPageUseCaseTest {

    private fun db(): CatDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        CatDatabase.Schema.create(driver)
        return CatDatabase(driver)
    }

    lateinit var engine: MockEngine
    lateinit var errorEngine: MockEngine
    lateinit var client: HttpClient
    lateinit var api: TheCatApi

    @Before
    fun setUp() {
        engine = MockEngine { _ ->
            respond(
                content = """[
                  { "id":"abys","name":"Abyssinian","description":"desc","origin":"Egypt","temperament":"Active","life_span":"14","image":{"url":"https://x/y.jpg"} }
                ]""",
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }

        errorEngine = MockEngine { _ ->
            respond(
                content = """{ "error": "error" }""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }
    }


    @Test
    fun `get cat breeds successfully`() = runTest {
        // GIVEN
        client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }


        // WHEN
        api = TheCatApi(client, object : ApiKeyProvider {
            override fun apiKeyOrNull(): String? = null
        })

        val repo = BreedRepositoryImpl(api = api, db = db())
        val useCase = GetBreedsPageUseCase(repo)

        // WHEN
        val breedsFlow = useCase(page = 0, pageSize = 20)
        val result = breedsFlow.first { result ->
            result.isSuccess && result.getOrThrow().isNotEmpty()
        }

        // THEN
        assert(result.isSuccess)
        assert(result.getOrThrow().first().origin == "Egypt")
    }

    @Test
    fun `get cat breeds fetch error`() = runTest {
        // GIVEN
        client = HttpClient(errorEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // WHEN
        api = TheCatApi(client, object : ApiKeyProvider {
            override fun apiKeyOrNull(): String? = null
        })

        val repo = BreedRepositoryImpl(api = api, db = db())
        val useCase = GetBreedsPageUseCase(repo)

        // WHEN
        val breedsFlow = useCase(page = 0, pageSize = 20)
        val result = breedsFlow.first { it.isFailure }

        // THEN
        assert(result.isFailure)
    }
}