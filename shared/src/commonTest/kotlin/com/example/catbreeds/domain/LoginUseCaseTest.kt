package com.example.catbreeds.domain

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.catbreeds.data.repo.AuthRepositoryImpl
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.domain.usecase.LoginUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertTrue

class LoginUseCaseTest {

    private fun db(): CatDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        CatDatabase.Schema.create(driver)
        return CatDatabase(driver)
    }

    @Test
    fun `invalid username fails`() = runTest {
        // GIVEN
        val repo = AuthRepositoryImpl(db())
        val usecase = LoginUseCase(repo)


        // WHEN
        val res = usecase("a!", "pass123")

        // THEN
        assertTrue(res.isFailure)
    }

    @Test
    fun `valid credentials succeed`() = runTest {
        // GIVEN
        val repo = AuthRepositoryImpl(db())
        val usecase = LoginUseCase(repo)


        // WHEN
        val res = usecase("edgar_rod", "pass123")

        // THEN
        assertTrue(res.isSuccess)
    }

    @Test
    fun `invalid password login not allowed`() = runTest {
        // GIVEN
        val repo = AuthRepositoryImpl(db())
        val usecase = LoginUseCase(repo)


        // WHEN
        val res = usecase("edgar_rod", "password")

        // THEN
        assertTrue(res.isFailure)
    }
}
