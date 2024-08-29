package com.tomorrowit.budgetgamer.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.UI
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.common.utils.ClipboardHelper
import com.tomorrowit.budgetgamer.common.utils.Logic.getProviderDrawableForString
import com.tomorrowit.budgetgamer.common.utils.TimeUtility
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import com.tomorrowit.budgetgamer.databinding.ActivityGameDetailsBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.ShareGameUseCase
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerViewAdapterPlatforms
import com.tomorrowit.budgetgamer.presentation.base.BaseSlideActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.GameDetailsState
import com.tomorrowit.budgetgamer.presentation.viewmodels.GameDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class GameDetailsActivity : BaseSlideActivity() {
    private lateinit var binding: ActivityGameDetailsBinding

    private val viewModel: GameDetailsViewModel by viewModels()

    private val recyclerViewAdapter: RecyclerViewAdapterPlatforms by lazy {
        RecyclerViewAdapterPlatforms()
    }

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    @Inject
    lateinit var shareGameUseCase: ShareGameUseCase

    @Inject
    lateinit var loadImageUseCase: LoadImageUseCase

    companion object {
        const val GAME_ID: String = "GAME_ID"
        const val GAME_FROM_SUBSCRIPTION = "GAME_FROM_SUBSCRIPTION"
    }

    private var gameId: String? = null
    private var gameFromSubscription: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityGameDetailsPlatformsRv.adapter = recyclerViewAdapter

        gameId = intent.getStringExtra(GAME_ID)
        gameFromSubscription = intent.extras?.getBoolean(GAME_FROM_SUBSCRIPTION, false)

        viewModel.startLoadingGame(gameId, gameFromSubscription)
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    GameDetailsState.Init -> {}

                    is GameDetailsState.IsError -> {
                        withContext(Dispatchers.UI) {
                            InfoDialog(
                                this@GameDetailsActivity,
                                getString(R.string.error),
                                state.message
                            ).show()
                        }
                    }

                    GameDetailsState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            showLoading()
                        }
                    }

                    is GameDetailsState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            setViews(state.game)
                            recyclerViewAdapter.submitList(state.platformList)
                            hideLoading()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }

    private fun setViews(data: GameWithProvider) {
        loadImageUseCase.invoke(
            binding.activityGameDetailsCover,
            data.gameEntity.cover,
            LoadImageUseCase.GAME_DETAILS
        )
        binding.activityGameDetailsTitle.text = data.gameEntity.name

        data.provider?.let {
            binding.activityGameDetailsProviderName.text = it.name
            binding.activityGameDetailsProviderLogo.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@GameDetailsActivity,
                    getProviderDrawableForString(it.logo)
                )
            )
        }

        binding.activityGameDetailsFreeUntil.text =
            TimeUtility.fromMillisecondsToDateAndTimeStringDots(
                getString(R.string.at),
                data.gameEntity.endDate
            )

        //region If we don't have a description, hide the Label text field and top separator
        if (data.gameEntity.description == "") {
            ViewHelper.hideView(binding.activityGameDetailsSeparator1)
            ViewHelper.hideView(binding.activityGameDetailsDescriptionLabel)
            ViewHelper.hideView(binding.activityGameDetailsDescription)
        } else {
            binding.activityGameDetailsDescription.text = data.gameEntity.description
        }
        //endregion

        //region If we don't have a developer, hide the Label text field and top separator
        if (data.gameEntity.developer == "") {
            ViewHelper.hideView(binding.activityGameDetailsSeparator2)
            ViewHelper.hideView(binding.activityGameDetailsDeveloperLabel)
            ViewHelper.hideView(binding.activityGameDetailsDeveloper)
        } else {
            binding.activityGameDetailsDeveloper.text = data.gameEntity.developer
        }
        //endregion

        //region If we don't have a release date, hide the Label text field and top separator
        if (data.gameEntity.releaseDate == "") {
            ViewHelper.hideView(binding.activityGameDetailsSeparator3)
            ViewHelper.hideView(binding.activityGameDetailsReleaseDateLabel)
            ViewHelper.hideView(binding.activityGameDetailsReleaseDate)
        } else {
            binding.activityGameDetailsReleaseDate.text = data.gameEntity.releaseDate
        }
        //endregion

        binding.activityGameDetailsClose.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                finish()
            }
        })

        binding.activityGameDetailsShare.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                shareGameUseCase.openGameLink(
                    data.gameEntity.name,
                    data.gameEntity.providerUrl
                )
            }
        })

        binding.activityGameDetailsCopy.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                clipboardHelper.copyToClipboard(
                    data.gameEntity.providerUrl
                )
            }
        })

        binding.activityGameDetailsOpen.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(data.gameEntity.providerUrl))
                startActivity(browserIntent)
            }
        })
    }

    private fun showLoading() {
        ViewHelper.showView(binding.activityGameDetailsLoading)
    }

    private fun hideLoading() {
        ViewHelper.hideView(binding.activityGameDetailsLoading)
    }
}