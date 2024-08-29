package com.tomorrowit.budgetgamer.data.mapper

import com.tomorrowit.budgetgamer.data.entities.GameEntity
import com.tomorrowit.budgetgamer.data.model.GameModel

class GameMapper : BaseMapper<GameModel, GameEntity> {
    override fun mapFromFirebaseModel(firebaseModel: GameModel): GameEntity {
        return GameEntity(
            gameId = firebaseModel.key,
            cover = firebaseModel.cover,
            coverPortrait = firebaseModel.cover_portrait,
            description = firebaseModel.description,
            developer = firebaseModel.developer,
            endDate = firebaseModel.end_date,
            free = firebaseModel.free,
            giveaway = firebaseModel.giveaway,
            name = firebaseModel.name,
            platformId = firebaseModel.platform_ids,
            providerId = firebaseModel.provider_id,
            providerUrl = firebaseModel.provider_url,
            publisher = firebaseModel.publisher,
            releaseDate = firebaseModel.release_date,
            startDate = firebaseModel.start_date,
            subscriptionId = firebaseModel.subscription_id
        )
    }

    override fun mapToFirebaseModel(entity: GameEntity): GameModel {
        return GameModel(
            key = entity.gameId,
            cover = entity.cover,
            cover_portrait = entity.coverPortrait,
            description = entity.description,
            developer = entity.developer,
            end_date = entity.endDate,
            free = entity.free,
            giveaway = entity.giveaway,
            name = entity.name,
            platform_ids = entity.platformId,
            provider_id = entity.providerId ?: "",
            provider_url = entity.providerUrl,
            publisher = entity.publisher,
            release_date = entity.releaseDate,
            start_date = entity.startDate,
            subscription_id = entity.subscriptionId
        )
    }
}