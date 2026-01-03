package com.example.catbreeds.db

import kotlin.Long
import kotlin.String

public data class Breeds_cache(
  public val id: String,
  public val name: String,
  public val description: String,
  public val origin: String?,
  public val temperament: String?,
  public val life_span: String?,
  public val image_url: String?,
  public val updated_at: Long,
)
