package com.example.catbreeds.db.shared

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.example.catbreeds.db.CatDatabase
import com.example.catbreeds.db.CatDatabaseQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<CatDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = CatDatabaseImpl.Schema

internal fun KClass<CatDatabase>.newInstance(driver: SqlDriver): CatDatabase =
    CatDatabaseImpl(driver)

private class CatDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), CatDatabase {
  override val catDatabaseQueries: CatDatabaseQueries = CatDatabaseQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE breeds_cache (
          |  id TEXT NOT NULL PRIMARY KEY,
          |  name TEXT NOT NULL,
          |  description TEXT NOT NULL,
          |  origin TEXT,
          |  temperament TEXT,
          |  life_span TEXT,
          |  image_url TEXT,
          |  updated_at INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE favorites (
          |  breed_id TEXT NOT NULL PRIMARY KEY,
          |  created_at INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE session (
          |  token TEXT NOT NULL,
          |  username TEXT NOT NULL,
          |  expires_at INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
