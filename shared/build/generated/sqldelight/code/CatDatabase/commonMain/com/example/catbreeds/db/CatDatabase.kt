package com.example.catbreeds.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.example.catbreeds.db.shared.newInstance
import com.example.catbreeds.db.shared.schema
import kotlin.Unit

public interface CatDatabase : Transacter {
  public val catDatabaseQueries: CatDatabaseQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = CatDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): CatDatabase =
        CatDatabase::class.newInstance(driver)
  }
}
