package com.tomorrowit.budgetgamer.domain.listeners

import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.data.entities.GameEntity

interface ArticleClicked {
    fun onItemClicked(articleEntity: ArticleEntity)
}

interface GameClicked {
    fun onItemClicked(gameEntity: GameEntity)
}