package com.tomorrowit.budgetgamer.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.diff_util.PlatformModelDiffCallback
import com.tomorrowit.budgetgamer.data.model.PlatformModel
import com.tomorrowit.budgetgamer.databinding.ItemPlatformBinding
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerViewAdapterPlatforms.MyViewHolderPlatforms

class RecyclerViewAdapterPlatforms : ListAdapter<PlatformModel, MyViewHolderPlatforms>(
    PlatformModelDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderPlatforms {
        val binding =
            ItemPlatformBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderPlatforms(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolderPlatforms, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.itemPlatformName.text = name
                binding.itemPlatformLogo.setImageResource(Logic.getPlatformDrawableForString(logo))
            }
        }
    }

    inner class MyViewHolderPlatforms(
        val binding: ItemPlatformBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}