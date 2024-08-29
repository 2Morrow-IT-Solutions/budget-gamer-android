package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithProvider(
    @Embedded val gameEntity: GameEntity,
    @Relation(
        parentColumn = "providerId",
        entityColumn = "id"
    )
    val provider: ProviderEntity?
)