package com.example.catbreeds.domain.model

data class Breed(
    val id: String,
    val name: String,
    val description: String,
    val origin: String?,
    val temperament: String?,
    val lifeSpan: String?,
    val imageUrl: String?,
    val isFavorite: Boolean
) {
    val shortDescription: String
        get() = description.take(120).let { if (description.length > 120) "$it…" else it }
}
