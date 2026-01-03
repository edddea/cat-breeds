package com.example.catbreeds.data.local

import com.example.catbreeds.data.platform.createSqlDriver
import com.example.catbreeds.db.CatDatabase

class DatabaseFactory {
    fun create(): CatDatabase = CatDatabase(createSqlDriver())
}
