package com.example.catbreeds.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.catbreeds.data.repo.FavoritesRepositoryImpl
import com.example.catbreeds.db.CatDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesRepositoryTest {

    private fun db(): CatDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        CatDatabase.Schema.create(driver)
        return CatDatabase(driver)
    }

    @Test
    fun `toggle favorite adds and removes`() = runTest {
        // GIVEN
        val repo = FavoritesRepositoryImpl(db())

        // WHEN / THEN
        val added = repo.toggleFavorite("abys").getOrThrow()
        assertTrue(added)

        val removed = repo.toggleFavorite("abys").getOrThrow()
        assertFalse(removed)
    }
}
