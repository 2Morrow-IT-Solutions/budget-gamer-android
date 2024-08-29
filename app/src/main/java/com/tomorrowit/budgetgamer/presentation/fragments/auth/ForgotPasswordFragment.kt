package com.tomorrowit.budgetgamer.presentation.fragments.auth

import android.os.Bundle
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
import com.tomorrowit.budgetgamer.common.config.extensions.isValidEmail
import com.tomorrowit.budgetgamer.databinding.FragmentForgotPasswordBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.ForgotPasswordViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.ForgotState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@ForgotPasswordFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.sending_link)
        )
    }
    private val successDialog: InfoDialog by lazy {
        InfoDialog(
            this@ForgotPasswordFragment.requireContext(),
            getString(R.string.success),
            getString(R.string.password_reset_send)
        ).apply {
            setOnDismissListener {
                this@ForgotPasswordFragment.findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentForgotPasswordSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateField()) {
                    viewModel.forgot(binding.fragmentForgotPasswordMail.text.toString())
                }
            }
        })
        binding.fragmentForgotPasswordBar.setNavigationOnClickListener {
            this@ForgotPasswordFragment.findNavController().popBackStack()
        }

        binding.fragmentForgotPasswordMail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.fragmentForgotPasswordSubmit.performClick()
                true
            } else {
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { forgotState ->
                when (forgotState) {
                    ForgotState.Init -> Unit

                    ForgotState.IsLoading -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.show()
                        }
                    }

                    ForgotState.IsSuccess -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            successDialog.show()
                        }
                    }

                    is ForgotState.ShowError -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@ForgotPasswordFragment.requireContext(),
                                getString(R.string.error),
                                forgotState.message
                            ).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun validateField(): Boolean {
        if (!binding.fragmentForgotPasswordMail.text.toString().isValidEmail()) {
            InfoDialog(
                this@ForgotPasswordFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.fragmentForgotPasswordMail.text.toString(),
                    FieldType.Email
                )
            ).show()
            return false
        }
        return true
    }
}