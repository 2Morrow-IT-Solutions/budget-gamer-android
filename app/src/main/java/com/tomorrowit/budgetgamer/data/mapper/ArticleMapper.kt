package com.tomorrowit.budgetgamer.data.mapper

import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.data.model.ArticleModel

class ArticleMapper : BaseMapper<ArticleModel, ArticleEntity> {
    override fun mapFromFirebaseModel(firebaseModel: ArticleModel): ArticleEntity {
        return ArticleEntity(
            cover = firebaseModel.cover,
            description = firebaseModel.description,
            domain = firebaseModel.domain,
            endDate = firebaseModel.end_date,
            id = firebaseModel.key,
            link = firebaseModel.link,
            startDate = firebaseModel.start_date,
            title = firebaseModel.title
        )
    }

    override fun mapToFirebaseModel(entity: ArticleEntity): ArticleModel {
        return ArticleModel(
            cover = entity.cover,
            description = entity.description,
            domain = entity.domain,
            end_date = entity.endDate,
            key = entity.id,
            link = entity.link,
            start_date = entity.startDate,
            title = entity.title
        )
    }
}