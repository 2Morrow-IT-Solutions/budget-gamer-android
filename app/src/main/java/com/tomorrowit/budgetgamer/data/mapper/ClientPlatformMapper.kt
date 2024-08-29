package com.tomorrowit.budgetgamer.data.mapper

import com.tomorrowit.budgetgamer.data.entities.ClientPlatformEntity
import com.tomorrowit.budgetgamer.data.model.ClientPlatformModel

class ClientPlatformMapper : BaseMapper<ClientPlatformModel, ClientPlatformEntity> {

    override fun mapFromFirebaseModel(firebaseModel: ClientPlatformModel): ClientPlatformEntity {
        return ClientPlatformEntity(
            id = firebaseModel.key,
            maintenance = firebaseModel.maintenance,
            minVersion = firebaseModel.min_version
        )
    }

    override fun mapToFirebaseModel(entity: ClientPlatformEntity): ClientPlatformModel {
        return ClientPlatformModel(
            key = entity.id,
            maintenance = entity.maintenance,
            min_version = entity.minVersion
        )
    }
}