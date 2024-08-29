package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GameWithPlatform(
    @Embedded val gameEntity: GameEntity,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "platformId",
        associateBy = Junction(GamePlatformCrossRef::class)
    )
    val platforms: List<PlatformEntity>
)