package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SubscriptionWithGames(
    @Embedded val subscriptionEntity: SubscriptionEntity,
    @Relation(
        entity = GameEntity::class,
        parentColumn = "id",
        entityColumn = "subscriptionId"
    )
    val gamesWithPlatforms: List<GameWithPlatform>
)