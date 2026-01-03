package com.example.catbreeds.db

import kotlin.Long
import kotlin.String

public data class Session(
  public val token: String,
  public val username: String,
  public val expires_at: Long,
)
