package com.tomorrowit.budgetgamer.data.mapper

import com.tomorrowit.budgetgamer.data.entities.SubscriptionEntity
import com.tomorrowit.budgetgamer.data.model.SubscriptionModel

class SubscriptionMapper : BaseMapper<SubscriptionModel, SubscriptionEntity> {
    override fun mapFromFirebaseModel(firebaseModel: SubscriptionModel): SubscriptionEntity {
        return SubscriptionEntity(
            colour = firebaseModel.colour,
            id = firebaseModel.key,
            logo = firebaseModel.logo,
            name = firebaseModel.name
        )
    }

    override fun mapToFirebaseModel(entity: SubscriptionEntity): SubscriptionModel {
        return SubscriptionModel(
            colour = entity.colour,
            key = entity.id,
            logo = entity.logo,
            name = entity.name
        )
    }
}