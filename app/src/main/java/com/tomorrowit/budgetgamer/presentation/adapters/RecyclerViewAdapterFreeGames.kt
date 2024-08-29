package com.tomorrowit.budgetgamer.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.TimeUtility
import com.tomorrowit.budgetgamer.common.utils.diff_util.GameWithProviderDiffCallback
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import com.tomorrowit.budgetgamer.databinding.ItemGameBinding
import com.tomorrowit.budgetgamer.domain.listeners.GameClicked
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.listeners.ViewBouncer
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase

class RecyclerViewAdapterFreeGames(
    private val mContext: Context,
    private val loadImageUseCase: LoadImageUseCase,
    private val gameClicked: GameClicked
) : ListAdapter<GameWithProvider, RecyclerViewAdapterFreeGames.MyViewHolderFreeGames>(
    GameWithProviderDiffCallback()
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderFreeGames {
        val binding =
            ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderFreeGames(binding, gameClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolderFreeGames, position: Int) {
        with(holder) {
            with(getItem(position)) {
                val gameEntity = this.gameEntity
                val provider = this.provider
                binding.itemGameName.text = gameEntity.name
                binding.itemGameDate.text =
                    TimeUtility.fromMillisecondsToDateStringDots(gameEntity.endDate)
                loadImageUseCase.invoke(
                    binding.itemGamePicture,
                    gameEntity.cover,
                    LoadImageUseCase.GAME_IMAGE
                )
                if (provider != null) {
                    binding.itemGameProviderName.text = provider.name
                    binding.itemGameProviderLogo.setImageDrawable(
                        AppCompatResources.getDrawable(
                            mContext,
                            Logic.getProviderDrawableForString(provider.logo)
                        )
                    )
                }
            }
        }
    }

    inner class MyViewHolderFreeGames(
        val binding: ItemGameBinding,
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