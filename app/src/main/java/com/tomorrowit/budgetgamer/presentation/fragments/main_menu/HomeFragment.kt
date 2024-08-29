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
import com.tomorrowit.budgetgamer.databinding.FragmentHomeBinding
import com.tomorrowit.budgetgamer.domain.listeners.GameClicked
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase
import com.tomorrowit.budgetgamer.presentation.activities.AddUrlActivity
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerViewAdapterFreeGames
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerViewHelper
import com.tomorrowit.budgetgamer.presentation.activities.AuthActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.QuestionDialog
import com.tomorrowit.budgetgamer.presentation.activities.GameDetailsActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.MainViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.HomeState
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), GameClicked {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var loadImageUseCase: LoadImageUseCase

    private val recyclerViewAdapter: RecyclerViewAdapterFreeGames by lazy {
        RecyclerViewAdapterFreeGames(requireContext(), loadImageUseCase, this@HomeFragment)
    }

    private val questionDialog: QuestionDialog by lazy {
        QuestionDialog(
            this@HomeFragment.requireContext(),
            getString(R.string.error),
            getString(R.string.no_login_add_link),
            object : QuestionDialog.QuestionDialogListener {
                override fun YesButtonAction() {
                    requireActivity().openActivityPush(AuthActivity())
                }

                override fun NoButtonAction() {

                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.homeFragmentRecyclerview.layoutManager =
            RecyclerViewHelper.getGridLayoutManager(requireContext())
        binding.homeFragmentRecyclerview.addItemDecoration(
            RecyclerViewHelper.getGridSpacingItemDecoration(
                requireContext()
            )
        )
        binding.homeFragmentRecyclerview.adapter = recyclerViewAdapter

        binding.homeFragmentAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (activityViewModel.isUserAuthenticated()) {
                    requireActivity().openActivityPushAny(
                        AddUrlActivity(),
                        mapOf(AddUrlActivity.ACTION_ID to AddUrlActivity.ADD_GAME)
                    )
                } else {
                    questionDialog.show()
                }
            }
        })

        binding.homeFragmentBanner.root.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPush(AuthActivity())
            }
        })

        binding.homeFragmentBanner.bannerLayoutClose.setOnClickListener(object :
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
                    HomeState.Init -> {}

                    is HomeState.IsError -> {
                        withContext(Dispatchers.UI) {
                            showError(state.message)
                        }
                    }

                    HomeState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            showLoading()
                        }
                    }

                    is HomeState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            recyclerViewAdapter.submitList(state.data)
                            showRecycler()
                        }
                    }

                    HomeState.IsEmpty -> {
                        showEmpty()

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
                    ViewHelper.showView(binding.homeFragmentBanner.root)
                } else {
                    ViewHelper.hideView(binding.homeFragmentBanner.root)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        activityViewModel.userBanned.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
            .distinctUntilChanged()
            .onEach {
                if (!it) {
                    ViewHelper.showView(binding.homeFragmentAdd)
                } else {
                    ViewHelper.hideView(binding.homeFragmentAdd)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    //region Banner logic
    private fun showBanner() {
        binding.homeFragmentBar.subtitle = getText(R.string.network_error_title)
        binding.homeFragmentInternetBanner.bannerLayoutText.text =
            getText(R.string.network_error_description)
        ViewHelper.showView(binding.homeFragmentInternetBanner.root)
    }

    private fun hideBanner() {
        binding.homeFragmentBar.subtitle = ""
        binding.homeFragmentInternetBanner.bannerLayoutText.text = ""
        ViewHelper.hideView(binding.homeFragmentInternetBanner.root)

    }
    //endregion

    //region View Handlers
    private fun showRecycler() {
        ViewHelper.showView(binding.homeFragmentRecyclerview)
        ViewHelper.hideView(binding.homeFragmentLoading)
        ViewHelper.hideView(binding.homeFragmentError)
    }

    private fun showEmpty() {
        binding.homeFragmentError.setImage(R.drawable.error_empty)
        binding.homeFragmentError.setTitle(getString(R.string.no_games_title))
        binding.homeFragmentError.setDescription(getString(R.string.no_games_description))
        ViewHelper.showView(binding.homeFragmentError)
        ViewHelper.hideView(binding.homeFragmentRecyclerview)
        ViewHelper.hideView(binding.homeFragmentLoading)
    }

    private fun showLoading() {
        ViewHelper.showView(binding.homeFragmentLoading)
        ViewHelper.hideView(binding.homeFragmentError)
        ViewHelper.hideView(binding.homeFragmentRecyclerview)
    }

    private fun showError(errorMessage: String) {
        binding.homeFragmentError.setImage(R.drawable.error_empty)
        binding.homeFragmentError.setTitle(getString(R.string.error))
        binding.homeFragmentError.setDescription(errorMessage)
        ViewHelper.showView(binding.homeFragmentError)
        ViewHelper.hideView(binding.homeFragmentLoading)
        ViewHelper.hideView(binding.homeFragmentRecyclerview)
    }
    //endregion

    override fun onItemClicked(gameEntity: GameEntity) {
        requireActivity().openActivityPushAny(
            GameDetailsActivity(),
            mapOf(
                GameDetailsActivity.GAME_ID to gameEntity.gameId,
                GameDetailsActivity.GAME_FROM_SUBSCRIPTION to false
            )
        )
    }
}