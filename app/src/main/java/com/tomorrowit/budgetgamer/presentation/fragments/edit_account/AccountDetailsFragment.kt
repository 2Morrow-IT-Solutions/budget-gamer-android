package com.tomorrowit.budgetgamer.presentation.fragments.edit_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityFade
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentAccountDetailsBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.activities.SplashScreenActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.QuestionDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.EditAccountState
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.EditAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AccountDetailsFragment : Fragment() {
    private lateinit var binding: FragmentAccountDetailsBinding
    private val activityViewModel: EditAccountViewModel by activityViewModels()

    private val deleteAccountDialog: QuestionDialog by lazy {
        QuestionDialog(
            this@AccountDetailsFragment.requireContext(),
            getString(R.string.delete_account),
            getString(R.string.account_delete_question_provider),
            object : QuestionDialog.QuestionDialogListener {
                override fun YesButtonAction() {
                    activityViewModel.deleteProviderAccount()
                }

                override fun NoButtonAction() {
                }
            }
        )
    }

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@AccountDetailsFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.deleting_account)
        )
    }

    private val successDialog: InfoDialog by lazy {
        InfoDialog(
            this@AccountDetailsFragment.requireContext(),
            getString(R.string.success),
            getString(R.string.account_deleted)
        ).apply {
            setOnDismissListener {
                requireActivity().openActivityFade(SplashScreenActivity())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        .apply { binding = this }.root

    override fun onStart() {
        super.onStart()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        activityViewModel.refreshUser()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailsAccountBar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        binding.fragmentAccountDetails1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_accountDetailsFragment_to_changeNameFragment)
            }
        })

        binding.fragmentAccountDetails2.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_accountDetailsFragment_to_changeEmailFragment)
            }
        })

        binding.fragmentAccountDetails3.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_accountDetailsFragment_to_changePasswordFragment)
            }
        })

        binding.fragmentAccountDetails4.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (activityViewModel.provider == "apple.com" || activityViewModel.provider == "google.com") {
                    deleteAccountDialog.show()
                } else {
                    findNavController().navigate(R.id.action_accountDetailsFragment_to_deleteAccountFragment)
                }
            }
        })
    }

    private fun updateAuthenticatedProvider(provider: String, displayName: String, email: String) {
        binding.editAccountMessage.text =
            getString(R.string.you_are_logged_with) + "${provider}. " + getString(
                R.string.you_can_edit_if_password
            )
        ViewHelper.showView(binding.editAccountMessage)
        binding.fragmentAccountDetails1Description.text =
            displayName
        binding.fragmentAccountDetails2Description.text =
            email
        //Disable all 3 buttons
        ViewHelper.disableView(binding.fragmentAccountDetails1)
        ViewHelper.disableView(binding.fragmentAccountDetails2)
        ViewHelper.disableView(binding.fragmentAccountDetails3)
        //Disable the texts
        ViewHelper.disableViewAndSetColor(binding.fragmentAccountDetails1Title)
        ViewHelper.disableViewAndSetColor(binding.fragmentAccountDetails1Description)
        ViewHelper.disableViewAndSetColor(binding.fragmentAccountDetails2Title)
        ViewHelper.disableViewAndSetColor(binding.fragmentAccountDetails2Description)
        ViewHelper.disableViewAndSetColor(binding.fragmentAccountDetails3Title)
        ViewHelper.disableViewAndSetColor(binding.fragmentAccountDetails3Description)
        //Hide all the chevron arrows
        ViewHelper.hideView(binding.fragmentAccountDetails1Chevron)
        ViewHelper.hideView(binding.fragmentAccountDetails2Chevron)
        ViewHelper.hideView(binding.fragmentAccountDetails3Chevron)
        ViewHelper.hideView(binding.fragmentAccountDetails4Chevron)
    }

    private fun updateAuthenticatedEmail(displayName: String, email: String) {
        ViewHelper.hideView(binding.editAccountMessage)
        binding.fragmentAccountDetails1Description.text =
            displayName
        binding.fragmentAccountDetails2Description.text =
            email
    }

    private fun observeState() {
        activityViewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    EditAccountState.Init -> {}

                    is EditAccountState.IsAuthenticatedEmail -> {
                        hideLoading()
                        updateAuthenticatedEmail(state.displayName, state.email)
                    }

                    is EditAccountState.IsAuthenticatedProvider -> {
                        hideLoading()
                        updateAuthenticatedProvider(state.provider, state.displayName, state.email)
                    }

                    is EditAccountState.IsError -> {
                        hideLoading()
                        InfoDialog(
                            this@AccountDetailsFragment.requireContext(),
                            getString(R.string.error),
                            state.message
                        ).apply {
                            setOnDismissListener {
                                requireActivity().finish()
                            }
                        }.show()
                    }

                    EditAccountState.IsLoading -> {
                        showLoading()
                    }

                    EditAccountState.IsDeletingAccount -> {
                        loadingDialog.show()
                    }

                    is EditAccountState.IsErrorDeletingAccount -> {
                        loadingDialog.dismiss()
                        InfoDialog(
                            this@AccountDetailsFragment.requireContext(),
                            getString(R.string.error),
                            state.message
                        ).show()
                    }

                    EditAccountState.IsSuccessDeletingAccount -> {
                        loadingDialog.dismiss()
                        successDialog.show()
                    }
                }

            }.launchIn(lifecycleScope)
    }

    private fun showLoading() {
        ViewHelper.showView(binding.fragmentAccountDetailsLoading)
    }

    private fun hideLoading() {
        ViewHelper.hideView(binding.fragmentAccountDetailsLoading)
    }
}