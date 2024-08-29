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
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.databinding.FragmentArticlesBinding
import com.tomorrowit.budgetgamer.domain.listeners.ArticleClicked
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenUrlAddressUseCase
import com.tomorrowit.budgetgamer.presentation.activities.AddUrlActivity
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerViewAdapterArticles
import com.tomorrowit.budgetgamer.presentation.adapters.RecyclerViewHelper
import com.tomorrowit.budgetgamer.presentation.activities.AuthActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.QuestionDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.MainViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.ArticlesState
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.ArticlesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ArticlesFragment : Fragment(), ArticleClicked {

    private lateinit var binding: FragmentArticlesBinding

    private val viewModel: ArticlesViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var openUrlAddressUseCase: OpenUrlAddressUseCase

    @Inject
    lateinit var loadImageUseCase: LoadImageUseCase

    private val recyclerViewAdapter: RecyclerViewAdapterArticles by lazy {
        RecyclerViewAdapterArticles(loadImageUseCase, this@ArticlesFragment)
    }

    private val questionDialog: QuestionDialog by lazy {
        QuestionDialog(
            this@ArticlesFragment.requireContext(),
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
    ) = FragmentArticlesBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.articlesFragmentRecyclerview.layoutManager =
            RecyclerViewHelper.getGridLayoutManager(requireContext())
        binding.articlesFragmentRecyclerview.addItemDecoration(
            RecyclerViewHelper.getGridSpacingItemDecoration(
                requireContext()
            )
        )
        binding.articlesFragmentRecyclerview.adapter = recyclerViewAdapter

        binding.articlesFragmentAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (activityViewModel.isUserAuthenticated()) {
                    requireActivity().openActivityPushAny(
                        AddUrlActivity(),
                        mapOf(AddUrlActivity.ACTION_ID to AddUrlActivity.ADD_ARTICLE)
                    )
                } else {
                    questionDialog.show()
                }
            }
        })

        binding.articlesFragmentBanner.root.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPush(AuthActivity())
            }
        })

        binding.articlesFragmentBanner.bannerLayoutClose.setOnClickListener(object :
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
                    ArticlesState.Init -> {}
                    is ArticlesState.IsError -> {
                        withContext(Dispatchers.UI) {
                            showError(state.message)
                        }
                    }

                    ArticlesState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            showLoading()
                        }
                    }

                    is ArticlesState.IsSuccess -> {
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
                    ViewHelper.showView(binding.articlesFragmentBanner.root)
                } else {
                    ViewHelper.hideView(binding.articlesFragmentBanner.root)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        activityViewModel.userBanned.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
            .distinctUntilChanged()
            .onEach {
                if (!it) {
                    ViewHelper.showView(binding.articlesFragmentAdd)
                } else {
                    ViewHelper.hideView(binding.articlesFragmentAdd)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    //region Banner logic
    private fun showBanner() {
        binding.articlesFragmentBar.subtitle = getText(R.string.network_error_title)
        binding.articlesFragmentInternetBanner.bannerLayoutText.text =
            getText(R.string.network_error_description)
        ViewHelper.showView(binding.articlesFragmentInternetBanner.root)
    }

    private fun hideBanner() {
        binding.articlesFragmentBar.subtitle = ""
        binding.articlesFragmentInternetBanner.bannerLayoutText.text = ""
        ViewHelper.hideView(binding.articlesFragmentInternetBanner.root)

    }
    //endregion

    //region View Handlers
    private fun showRecycler() {
        ViewHelper.showView(binding.articlesFragmentRecyclerview)
        ViewHelper.hideView(binding.articlesFragmentLoading)
        ViewHelper.hideView(binding.articlesFragmentError)
    }

    private fun showEmpty() {
        binding.articlesFragmentError.setImage(R.drawable.error_empty)
        binding.articlesFragmentError.setTitle(getString(R.string.no_articles_title))
        binding.articlesFragmentError.setDescription(getString(R.string.no_articles_description))
        ViewHelper.showView(binding.articlesFragmentError)
        ViewHelper.hideView(binding.articlesFragmentRecyclerview)
        ViewHelper.hideView(binding.articlesFragmentLoading)
    }

    private fun showLoading() {
        ViewHelper.showView(binding.articlesFragmentLoading)
        ViewHelper.hideView(binding.articlesFragmentError)
        ViewHelper.hideView(binding.articlesFragmentRecyclerview)
    }

    private fun showError(errorMessage: String) {
        binding.articlesFragmentError.setImage(R.drawable.error_empty)
        binding.articlesFragmentError.setTitle(getString(R.string.error))
        binding.articlesFragmentError.setDescription(errorMessage)
        ViewHelper.showView(binding.articlesFragmentError)
        ViewHelper.hideView(binding.articlesFragmentLoading)
        ViewHelper.hideView(binding.articlesFragmentRecyclerview)
    }
    //endregion

    override fun onItemClicked(articleEntity: ArticleEntity) {
        openUrlAddressUseCase.invoke(articleEntity.link)
    }
}