package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["platformId", "gameId"],
    foreignKeys = [
        ForeignKey(
            entity = PlatformEntity::class,
            parentColumns = ["platformId"],
            childColumns = ["platformId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["platformId"]), Index(value = ["gameId"])]
)
data class GamePlatformCrossRef(
    val platformId: String,
    val gameId: String
)
