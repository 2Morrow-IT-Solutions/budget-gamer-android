package com.tomorrowit.budgetgamer.common.utils.diff_util

import androidx.recyclerview.widget.DiffUtil
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.data.entities.GameWithPlatform
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import com.tomorrowit.budgetgamer.data.entities.SubscriptionWithGames
import com.tomorrowit.budgetgamer.data.model.PlatformModel

class ArticleEntityDiffCallback : DiffUtil.ItemCallback<ArticleEntity>() {
    override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
        return oldItem == newItem
    }
}

class GameWithProviderDiffCallback : DiffUtil.ItemCallback<GameWithProvider>() {
    override fun areItemsTheSame(oldItem: GameWithProvider, newItem: GameWithProvider): Boolean {
        return oldItem.gameEntity.gameId == newItem.gameEntity.gameId
    }

    override fun areContentsTheSame(oldItem: GameWithProvider, newItem: GameWithProvider): Boolean {
        return oldItem == newItem
    }
}

class PlatformModelDiffCallback : DiffUtil.ItemCallback<PlatformModel>() {
    override fun areItemsTheSame(oldItem: PlatformModel, newItem: PlatformModel): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: PlatformModel, newItem: PlatformModel): Boolean {
        return oldItem == newItem
    }
}

class SubscriptionWithGamesDiffCallback :
    DiffUtil.ItemCallback<SubscriptionWithGames>() {
    override fun areItemsTheSame(
        oldItem: SubscriptionWithGames,
        newItem: SubscriptionWithGames
    ): Boolean {
        return oldItem.subscriptionEntity.id == newItem.subscriptionEntity.id
    }

    override fun areContentsTheSame(
        oldItem: SubscriptionWithGames, newItem: SubscriptionWithGames
    ): Boolean {
        return oldItem == newItem
    }
}

class GameWithPlatformsDiffCallback : DiffUtil.ItemCallback<GameWithPlatform>() {
    override fun areItemsTheSame(oldItem: GameWithPlatform, newItem: GameWithPlatform): Boolean {
        return oldItem.gameEntity.gameId == newItem.gameEntity.gameId
    }

    override fun areContentsTheSame(
        oldItem: GameWithPlatform,
        newItem: GameWithPlatform
    ): Boolean {
        return oldItem == newItem
    }
}