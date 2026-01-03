package com.example.catbreeds.data.repo

import com.example.catbreeds.domain.model.Breed

internal fun com.example.catbreeds.db.Breeds_cache.toDomain(isFavorite: Boolean): Breed =
    Breed(
        id = id,
        name = name,
        description = description,
        origin = origin,
        temperament = temperament,
        lifeSpan = life_span,
        imageUrl = image_url,
        isFavorite = isFavorite
    )
