package com.tomorrowit.budgetgamer.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.budgetgamer.common.utils.diff_util.SubscriptionWithGamesDiffCallback
import com.tomorrowit.budgetgamer.data.entities.SubscriptionWithGames
import com.tomorrowit.budgetgamer.databinding.ItemSubscriptionSectionBinding
import com.tomorrowit.budgetgamer.domain.listeners.GameClicked
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase

class RecyclerviewAdapterSubscriptionSection(
    private val mContext: Context,
    private val loadImageUseCase: LoadImageUseCase,
    private val itemClicked: GameClicked
) : ListAdapter<SubscriptionWithGames, RecyclerviewAdapterSubscriptionSection.SubscriptionViewHolder>(
    SubscriptionWithGamesDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val binding = ItemSubscriptionSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubscriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.itemSubscriptionSectionTitle.text =
                    subscriptionEntity.name
                val gameAdapter =
                    RecyclerViewAdapterSubscriptionGame(mContext, loadImageUseCase, itemClicked)
                binding.itemSubscriptionSectionRecyclerView.adapter = gameAdapter
                gameAdapter.submitList(gamesWithPlatforms)
            }
        }
    }

    inner class SubscriptionViewHolder(
        val binding: ItemSubscriptionSectionBinding
    ) : RecyclerView.ViewHolder(binding.root)
}