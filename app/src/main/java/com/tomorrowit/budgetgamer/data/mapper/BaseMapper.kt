package com.tomorrowit.budgetgamer.data.mapper

interface BaseMapper<FirebaseModel, Entity> {
    fun mapFromFirebaseModel(firebaseModel: FirebaseModel): Entity
    fun mapToFirebaseModel(entity: Entity): FirebaseModel
}