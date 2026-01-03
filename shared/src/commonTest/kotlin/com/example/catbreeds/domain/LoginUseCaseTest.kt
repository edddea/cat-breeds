package com.example.catbreeds.domain

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.catbreeds.data.repo.AuthRepositoryImpl
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.domain.usecase.LoginUseCase
import kotlin.test.Test
import kotlin.test.assertTrue

class LoginUseCaseTest {

    private fun db(): CatDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        CatDatabase.Schema.create(driver)
        return CatDatabase(driver)
    }

    @Test
    fun `invalid username fails`() = kotlinx.coroutines.test.runTest {
        //
        val repo = AuthRepositoryImpl(db())
        val usecase = LoginUseCase(repo)

        val res = usecase("a!", "pass123")
        assertTrue(res.isFailure)
    }

    @Test
    fun `valid credentials succeed`() = kotlinx.coroutines.test.runTest {
        val repo = AuthRepositoryImpl(db())
        val usecase = LoginUseCase(repo)

        val res = usecase("edgar_rod", "pass123")
        assertTrue(res.isSuccess)
    }
}
