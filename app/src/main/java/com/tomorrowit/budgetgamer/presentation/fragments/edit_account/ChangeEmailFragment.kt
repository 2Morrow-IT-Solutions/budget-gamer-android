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
import com.tomorrowit.budgetgamer.common.config.extensions.isValidEmail
import com.tomorrowit.budgetgamer.common.config.extensions.isValidPassword
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentChangeEmailBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.ChangeEmailViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.EditEmailState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChangeEmailFragment : Fragment() {
    private lateinit var binding: FragmentChangeEmailBinding
    private val viewModel: ChangeEmailViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@ChangeEmailFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.updating_email)
        )
    }

    private val successDialog: InfoDialog by lazy {
        InfoDialog(
            this@ChangeEmailFragment.requireContext(),
            getString(R.string.success),
            getString(R.string.email_changed)
        ).apply {
            setOnDismissListener {
                this@ChangeEmailFragment.findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChangeEmailBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeEmailFragmentBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.changeEmailFragmentField.setText(viewModel.getEmail())

        binding.changeEmailFragmentField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == viewModel.getEmail()) {
                    ViewHelper.disableView(binding.changeEmailFragmentSubmit)
                } else {
                    if (binding.changeEmailFragmentField.text.toString().isValidEmail()) {
                        ViewHelper.enableView(binding.changeEmailFragmentSubmit)
                    }
                }
            }
        })

        binding.changeEmailFragmentPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (binding.changeEmailFragmentField.text.toString().isValidEmail()
                    && binding.changeEmailFragmentPassword.text.toString().isValidPassword()
                    && binding.changeEmailFragmentField.text.toString() != viewModel.getEmail()
                ) {
                    ViewHelper.enableView(binding.changeEmailFragmentSubmit)
                }
            }
        })

        binding.changeEmailFragmentPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.changeEmailFragmentSubmit.performClick()
                true
            } else {
                false
            }
        }

        binding.changeEmailFragmentSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateField()) {
                    viewModel.authenticateAndChangeEmail(
                        binding.changeEmailFragmentField.text.toString(),
                        binding.changeEmailFragmentPassword.text.toString()
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
            .onEach { state ->
                when (state) {
                    is EditEmailState.Init -> Unit
                    is EditEmailState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.show()
                        }
                    }

                    is EditEmailState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            successDialog.show()
                        }
                    }

                    is EditEmailState.ShowError -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@ChangeEmailFragment.requireContext(),
                                getString(R.string.error),
                                state.message
                            ).show()
                        }
                    }
                }

            }.launchIn(lifecycleScope)
    }

    private fun validateField(): Boolean {
        if (!binding.changeEmailFragmentField.text.toString().isValidEmail()) {
            InfoDialog(
                this@ChangeEmailFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.changeEmailFragmentField.text.toString(),
                    FieldType.Email
                )
            ).show()
            return false
        }
        if (!binding.changeEmailFragmentPassword.text.toString().isValidPassword()) {
            InfoDialog(
                this@ChangeEmailFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.changeEmailFragmentPassword.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        return true
    }
}