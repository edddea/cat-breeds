package com.example.catbreeds.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.catbreeds.data.repo.FavoritesRepositoryImpl
import com.example.catbreeds.db.CatDatabase
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesRepositoryTest {

    private fun db(): CatDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        CatDatabase.Schema.create(driver)
        return CatDatabase(driver)
    }

    @Test
    fun `toggle favorite adds and removes`() = kotlinx.coroutines.test.runTest {
        val repo = FavoritesRepositoryImpl(db())

        val added = repo.toggleFavorite("abys").getOrThrow()
        assertTrue(added)

        val removed = repo.toggleFavorite("abys").getOrThrow()
        assertFalse(removed)
    }
}
