package com.tomorrowit.budgetgamer.data.mapper

import com.tomorrowit.budgetgamer.data.entities.PlatformEntity
import com.tomorrowit.budgetgamer.data.model.PlatformModel

class PlatformMapper : BaseMapper<PlatformModel, PlatformEntity> {

    override fun mapFromFirebaseModel(firebaseModel: PlatformModel): PlatformEntity {
        return PlatformEntity(
            colourBackground = firebaseModel.colour_background,
            colourMargin = firebaseModel.colour_margin,
            colourText = firebaseModel.colour_text,
            platformId = firebaseModel.key,
            logo = firebaseModel.logo,
            name = firebaseModel.name,
            priority = firebaseModel.priority
        )
    }

    override fun mapToFirebaseModel(entity: PlatformEntity): PlatformModel {
        return PlatformModel(
            colour_background = entity.colourBackground,
            colour_margin = entity.colourMargin,
            colour_text = entity.colourText,
            key = entity.platformId,
            logo = entity.logo,
            name = entity.name,
            priority = entity.priority
        )
    }
}