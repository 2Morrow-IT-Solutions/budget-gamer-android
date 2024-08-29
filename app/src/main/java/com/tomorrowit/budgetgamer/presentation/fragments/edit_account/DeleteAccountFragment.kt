package com.tomorrowit.budgetgamer.presentation.fragments.edit_account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.UI
import com.tomorrowit.budgetgamer.common.config.extensions.isValidPassword
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityFade
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentDeleteAccountBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.activities.SplashScreenActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.DeleteAccountViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.DeleteState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DeleteAccountFragment : Fragment() {
    private lateinit var binding: FragmentDeleteAccountBinding
    private val viewModel: DeleteAccountViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@DeleteAccountFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.deleting_account)
        )
    }

    private val successDialog: InfoDialog by lazy {
        InfoDialog(
            this@DeleteAccountFragment.requireContext(),
            getString(R.string.success),
            getString(R.string.account_deleted)
        ).apply {
            setOnDismissListener { requireActivity().openActivityFade(SplashScreenActivity()) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteAccountFragmentBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.deleteAccountFragmentPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isValidPassword()) {
                    ViewHelper.enableView(binding.deleteAccountFragmentSubmit)
                } else {
                    ViewHelper.disableView(binding.deleteAccountFragmentSubmit)
                }
            }
        })

        binding.deleteAccountFragmentPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.deleteAccountFragmentSubmit.performClick()
                true
            } else {
                false
            }
        }

        binding.deleteAccountFragmentSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateField()) {
                    viewModel.authenticateAndDelete(
                        binding.deleteAccountFragmentPassword.text.toString()
                    )
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).distinctUntilChanged()
            .onEach { deleteState ->
                when (deleteState) {
                    is DeleteState.Init -> Unit

                    is DeleteState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            successDialog.show()
                        }
                    }

                    is DeleteState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.show()
                        }
                    }

                    is DeleteState.ShowError -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@DeleteAccountFragment.requireContext(),
                                getString(R.string.error),
                                deleteState.message
                            ).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun validateField(): Boolean {
        if (!binding.deleteAccountFragmentPassword.text.toString().isValidPassword()) {
            InfoDialog(
                this@DeleteAccountFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.deleteAccountFragmentPassword.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        return true
    }
}