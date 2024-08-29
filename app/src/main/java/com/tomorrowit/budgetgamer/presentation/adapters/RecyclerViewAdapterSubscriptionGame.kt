package com.tomorrowit.budgetgamer.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.diff_util.GameWithPlatformsDiffCallback
import com.tomorrowit.budgetgamer.data.entities.GameWithPlatform
import com.tomorrowit.budgetgamer.databinding.ItemSubscriptionGameBinding
import com.tomorrowit.budgetgamer.domain.listeners.GameClicked
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.listeners.ViewBouncer
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase

class RecyclerViewAdapterSubscriptionGame(
    private val mContext: Context,
    private val loadImageUseCase: LoadImageUseCase,
    private val gameClicked: GameClicked
) : ListAdapter<GameWithPlatform, RecyclerViewAdapterSubscriptionGame.MyViewHolderSubscriptionGame>(
    GameWithPlatformsDiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolderSubscriptionGame {
        val binding =
            ItemSubscriptionGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderSubscriptionGame(binding, gameClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolderSubscriptionGame, position: Int) {
        with(holder) {
            with(getItem(position)) {
                val platformIds = gameEntity.platformId.split(",").map { it.trim() }

                val matchingPlatforms = platforms.filter { platform ->
                    platformIds.contains(platform.platformId)
                }
                matchingPlatforms.forEach {
                    with(it) {
                        binding.itemSubscriptionGameTitle.text = gameEntity.name
                        loadImageUseCase.invoke(
                            binding.itemSubscriptionGameImage,
                            gameEntity.coverPortrait
                        )
                        binding.itemSubscriptionGameProviderLogo.setImageDrawable(
                            AppCompatResources.getDrawable(
                                mContext,
                                Logic.getPlatformDrawableForString(this.logo)
                            )
                        )

                        binding.itemSubscriptionGameMargin.strokeColor =
                            Logic.getColourForString(this.colourMargin)

                        binding.itemSubscriptionGameProviderLogo.setColorFilter(
                            Logic.getColourForString(this.colourText)
                        )
                        binding.itemSubscriptionGameProviderBackground.setBackgroundColor(
                            Logic.getColourForString(this.colourBackground)
                        )
                        binding.itemSubscriptionGameProviderName.text = this.name
                        binding.itemSubscriptionGameProviderName.setTextColor(
                            Logic.getColourForString(this.colourText)
                        )
                    }
                }
            }
        }
    }

    inner class MyViewHolderSubscriptionGame(
        val binding: ItemSubscriptionGameBinding,
        private var gameClicked: GameClicked
    ) : RecyclerView.ViewHolder(binding.root) {
        val viewBouncer = ViewBouncer()
        private val singleClickListener = object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    gameClicked.onItemClicked(getItem(position).gameEntity)
                    viewBouncer.animateScale(binding.root)
                }
            }
        }

        init {
            binding.root.setOnClickListener(singleClickListener)
        }
    }
}