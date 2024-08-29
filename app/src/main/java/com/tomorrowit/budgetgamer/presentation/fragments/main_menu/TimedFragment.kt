package com.tomorrowit.budgetgamer.presentation.fragments.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.UI
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityPush
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityPushAny
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.data.entities.GameEntity
import com.tomorrowit.budgetgamer.databinding.FragmentTimedBinding
import com.tomorrowit.budgetgamer.domain.listeners.GameClicked
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerviewAdapterSubscriptionSection
import com.tomorrowit.budgetgamer.presentation.activities.AuthActivity
import com.tomorrowit.budgetgamer.presentation.activities.GameDetailsActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.MainViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.TimedState
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.TimedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class TimedFragment : Fragment(), GameClicked {

    private lateinit var binding: FragmentTimedBinding

    private val viewModel: TimedViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var loadImageUseCase: LoadImageUseCase

    private val recyclerViewAdapter: RecyclerviewAdapterSubscriptionSection by lazy {
        RecyclerviewAdapterSubscriptionSection(
            requireContext(),
            loadImageUseCase,
            this@TimedFragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTimedBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.timedFragmentRecyclerview.adapter = recyclerViewAdapter
        binding.timedFragmentBanner.root.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPush(AuthActivity())
            }
        })

        binding.timedFragmentBanner.bannerLayoutClose.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                activityViewModel.dismissBanner()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    TimedState.Init -> {}

                    is TimedState.IsError -> {
                        withContext(Dispatchers.UI) {
                            showError(state.message)
                        }
                    }

                    TimedState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            showLoading()
                        }
                    }

                    is TimedState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            if (state.data.isEmpty()) {
                                showEmpty()
                            } else {
                                recyclerViewAdapter.submitList(state.data)
                            }
                            showRecycler()
                        }
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        activityViewModel.hasInternet.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    withContext(Dispatchers.Main) {
                        hideBanner()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showBanner()
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        activityViewModel.showBanner.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    ViewHelper.showView(binding.timedFragmentBanner.root)
                } else {
                    ViewHelper.hideView(binding.timedFragmentBanner.root)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    //region Banner logic
    private fun showBanner() {
        binding.timedFragmentBar.subtitle = getText(R.string.network_error_title)
        binding.timedFragmentInternetBanner.bannerLayoutText.text =
            getText(R.string.network_error_description)
        ViewHelper.showView(binding.timedFragmentInternetBanner.root)
    }

    private fun hideBanner() {
        binding.timedFragmentBar.subtitle = ""
        binding.timedFragmentInternetBanner.bannerLayoutText.text = ""
        ViewHelper.hideView(binding.timedFragmentInternetBanner.root)

    }
    //endregion

    private fun showRecycler() {
        ViewHelper.showView(binding.timedFragmentRecyclerview)
        ViewHelper.hideView(binding.timedFragmentLoading)
        ViewHelper.hideView(binding.timedFragmentError)
    }

    private fun showEmpty() {
        binding.timedFragmentError.setImage(R.drawable.error_empty)
        binding.timedFragmentError.setTitle(getString(R.string.no_timed_title))
        binding.timedFragmentError.setDescription(getString(R.string.no_timed_description))
        ViewHelper.showView(binding.timedFragmentError)
        ViewHelper.hideView(binding.timedFragmentLoading)
        ViewHelper.hideView(binding.timedFragmentRecyclerview)
    }

    private fun showLoading() {
        ViewHelper.showView(binding.timedFragmentLoading)
        ViewHelper.hideView(binding.timedFragmentError)
        ViewHelper.hideView(binding.timedFragmentRecyclerview)
    }

    private fun showError(errorMessage: String) {
        binding.timedFragmentError.setImage(R.drawable.error_empty)
        binding.timedFragmentError.setTitle(getString(R.string.error))
        binding.timedFragmentError.setDescription(errorMessage)
        ViewHelper.showView(binding.timedFragmentError)
        ViewHelper.hideView(binding.timedFragmentLoading)
        ViewHelper.hideView(binding.timedFragmentRecyclerview)
    }

    override fun onItemClicked(gameEntity: GameEntity) {
        requireActivity().openActivityPushAny(
            GameDetailsActivity(), mapOf(
                GameDetailsActivity.GAME_ID to gameEntity.gameId,
                GameDetailsActivity.GAME_FROM_SUBSCRIPTION to true
            )
        )
    }
}