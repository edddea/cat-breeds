package com.example.catbreeds.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public class CatDatabaseQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectPage(
    `value`: Long,
    value_: Long,
    mapper: (
      id: String,
      name: String,
      description: String,
      origin: String?,
      temperament: String?,
      life_span: String?,
      image_url: String?,
      updated_at: Long,
    ) -> T,
  ): Query<T> = SelectPageQuery(value, value_) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getLong(7)!!
    )
  }

  public fun selectPage(value_: Long, value__: Long): Query<Breeds_cache> = selectPage(value_,
      value__) { id, name, description, origin, temperament, life_span, image_url, updated_at ->
    Breeds_cache(
      id,
      name,
      description,
      origin,
      temperament,
      life_span,
      image_url,
      updated_at
    )
  }

  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    name: String,
    description: String,
    origin: String?,
    temperament: String?,
    life_span: String?,
    image_url: String?,
    updated_at: Long,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getLong(7)!!
    )
  }

  public fun selectById(id: String): Query<Breeds_cache> = selectById(id) { id_, name, description,
      origin, temperament, life_span, image_url, updated_at ->
    Breeds_cache(
      id_,
      name,
      description,
      origin,
      temperament,
      life_span,
      image_url,
      updated_at
    )
  }

  public fun selectFavoriteIds(): Query<String> = Query(789_678_579, arrayOf("favorites"), driver,
      "CatDatabase.sq", "selectFavoriteIds", "SELECT breed_id FROM favorites") { cursor ->
    cursor.getString(0)!!
  }

  public fun isFavorite(breed_id: String): Query<Boolean> = IsFavoriteQuery(breed_id) { cursor ->
    cursor.getBoolean(0)!!
  }

  public fun <T : Any> selectFavorites(mapper: (
    id: String,
    name: String,
    description: String,
    origin: String?,
    temperament: String?,
    life_span: String?,
    image_url: String?,
    updated_at: Long,
  ) -> T): Query<T> = Query(-1_621_522_802, arrayOf("breeds_cache", "favorites"), driver,
      "CatDatabase.sq", "selectFavorites", """
  |SELECT b.id, b.name, b.description, b.origin, b.temperament, b.life_span, b.image_url, b.updated_at FROM breeds_cache b
  |JOIN favorites f ON f.breed_id = b.id
  |ORDER BY f.created_at DESC
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getLong(7)!!
    )
  }

  public fun selectFavorites(): Query<Breeds_cache> = selectFavorites { id, name, description,
      origin, temperament, life_span, image_url, updated_at ->
    Breeds_cache(
      id,
      name,
      description,
      origin,
      temperament,
      life_span,
      image_url,
      updated_at
    )
  }

  public fun <T : Any> getSession(mapper: (
    token: String,
    username: String,
    expires_at: Long,
  ) -> T): Query<T> = Query(1_928_646_829, arrayOf("session"), driver, "CatDatabase.sq",
      "getSession",
      "SELECT session.token, session.username, session.expires_at FROM session LIMIT 1") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!
    )
  }

  public fun getSession(): Query<Session> = getSession { token, username, expires_at ->
    Session(
      token,
      username,
      expires_at
    )
  }

  public fun upsertBreed(
    id: String,
    name: String,
    description: String,
    origin: String?,
    temperament: String?,
    life_span: String?,
    image_url: String?,
    updated_at: Long,
  ) {
    driver.execute(-141_103_912, """
        |INSERT OR REPLACE INTO breeds_cache(
        |  id, name, description, origin, temperament, life_span, image_url, updated_at
        |) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          bindString(0, id)
          bindString(1, name)
          bindString(2, description)
          bindString(3, origin)
          bindString(4, temperament)
          bindString(5, life_span)
          bindString(6, image_url)
          bindLong(7, updated_at)
        }
    notifyQueries(-141_103_912) { emit ->
      emit("breeds_cache")
    }
  }

  public fun deleteAllBreeds() {
    driver.execute(569_171_688, """DELETE FROM breeds_cache""", 0)
    notifyQueries(569_171_688) { emit ->
      emit("breeds_cache")
    }
  }

  public fun addFavorite(breed_id: String, created_at: Long) {
    driver.execute(547_007_664,
        """INSERT OR REPLACE INTO favorites(breed_id, created_at) VALUES (?, ?)""", 2) {
          bindString(0, breed_id)
          bindLong(1, created_at)
        }
    notifyQueries(547_007_664) { emit ->
      emit("favorites")
    }
  }

  public fun removeFavorite(breed_id: String) {
    driver.execute(1_557_366_285, """DELETE FROM favorites WHERE breed_id = ?""", 1) {
          bindString(0, breed_id)
        }
    notifyQueries(1_557_366_285) { emit ->
      emit("favorites")
    }
  }

  public fun saveSession(
    token: String,
    username: String,
    expires_at: Long,
  ) {
    driver.execute(-1_148_823_124, """
        |INSERT OR REPLACE INTO session(token, username, expires_at)
        |VALUES (?, ?, ?)
        """.trimMargin(), 3) {
          bindString(0, token)
          bindString(1, username)
          bindLong(2, expires_at)
        }
    notifyQueries(-1_148_823_124) { emit ->
      emit("session")
    }
  }

  public fun clearSession() {
    driver.execute(-633_284_970, """DELETE FROM session""", 0)
    notifyQueries(-633_284_970) { emit ->
      emit("session")
    }
  }

  private inner class SelectPageQuery<out T : Any>(
    public val `value`: Long,
    public val value_: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("breeds_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("breeds_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_545_682_936, """
    |SELECT breeds_cache.id, breeds_cache.name, breeds_cache.description, breeds_cache.origin, breeds_cache.temperament, breeds_cache.life_span, breeds_cache.image_url, breeds_cache.updated_at FROM breeds_cache
    |ORDER BY name
    |LIMIT ? OFFSET ?
    """.trimMargin(), mapper, 2) {
      bindLong(0, value)
      bindLong(1, value_)
    }

    override fun toString(): String = "CatDatabase.sq:selectPage"
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("breeds_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("breeds_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_545_287_995,
        """SELECT breeds_cache.id, breeds_cache.name, breeds_cache.description, breeds_cache.origin, breeds_cache.temperament, breeds_cache.life_span, breeds_cache.image_url, breeds_cache.updated_at FROM breeds_cache WHERE id = ?""",
        mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "CatDatabase.sq:selectById"
  }

  private inner class IsFavoriteQuery<out T : Any>(
    public val breed_id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("favorites", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("favorites", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-835_681_485,
        """SELECT EXISTS(SELECT 1 FROM favorites WHERE breed_id = ?) AS exist""", mapper, 1) {
      bindString(0, breed_id)
    }

    override fun toString(): String = "CatDatabase.sq:isFavorite"
  }
}
