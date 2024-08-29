package com.tomorrowit.budgetgamer.data.mapper

import com.tomorrowit.budgetgamer.data.entities.ProviderEntity
import com.tomorrowit.budgetgamer.data.model.ProviderModel

class ProviderMapper : BaseMapper<ProviderModel, ProviderEntity> {

    override fun mapFromFirebaseModel(firebaseModel: ProviderModel): ProviderEntity {
        return ProviderEntity(
            colour = firebaseModel.colour,
            id = firebaseModel.key,
            logo = firebaseModel.logo,
            name = firebaseModel.name
        )
    }

    override fun mapToFirebaseModel(entity: ProviderEntity): ProviderModel {
        return ProviderModel(
            colour = entity.colour,
            key = entity.id,
            logo = entity.logo,
            name = entity.name
        )
    }
}