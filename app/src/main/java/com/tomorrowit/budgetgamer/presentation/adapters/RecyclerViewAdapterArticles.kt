package com.tomorrowit.budgetgamer.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.budgetgamer.common.utils.diff_util.ArticleEntityDiffCallback
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.databinding.ItemArticleBinding
import com.tomorrowit.budgetgamer.domain.listeners.ArticleClicked
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.listeners.ViewBouncer
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase

class RecyclerViewAdapterArticles(
    private val loadImageUseCase: LoadImageUseCase,
    private val articleClicked: ArticleClicked
) : ListAdapter<ArticleEntity, RecyclerViewAdapterArticles.MyViewHolderArticles>(
    ArticleEntityDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderArticles {
        val binding =
            ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderArticles(binding, articleClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolderArticles, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.itemArticleName.text = title
                binding.itemArticleDescription.text = description
                binding.itemArticleSource.text = domain
                loadImageUseCase.invoke(
                    binding.itemArticlePicture,
                    cover,
                    LoadImageUseCase.GAME_IMAGE
                )
            }
        }
    }

    inner class MyViewHolderArticles(
        val binding: ItemArticleBinding,
        private var articleClicked: ArticleClicked
    ) : RecyclerView.ViewHolder(binding.root) {
        val viewBouncer = ViewBouncer()
        private val singleClickListener = object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    articleClicked.onItemClicked(getItem(position))
                    viewBouncer.animateScale(binding.root)
                }
            }
        }

        init {
            binding.root.setOnClickListener(singleClickListener)
        }
    }
}