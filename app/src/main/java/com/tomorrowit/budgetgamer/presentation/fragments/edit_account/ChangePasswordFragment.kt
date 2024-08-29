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
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentChangePasswordBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.ChangePasswordState
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding

    private val viewModel: ChangePasswordViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@ChangePasswordFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.updating_password)
        )
    }

    private val successDialog: InfoDialog by lazy {
        InfoDialog(
            this@ChangePasswordFragment.requireContext(),
            getString(R.string.success),
            getString(R.string.password_changed)
        ).also {
            it.setOnDismissListener {
                this@ChangePasswordFragment.findNavController().popBackStack()
            }
        }
    }

    private var isOldPasswordValid = false
    private var isNewPasswordValid = false
    private var isRepeatPasswordValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChangePasswordBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePasswordFragmentBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.changePasswordFragmentPasswordRepeat.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.changePasswordFragmentSubmit.performClick()
                true
            } else {
                false
            }
        }

        binding.changePasswordFragmentSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateFields() && validatePassword()) {
                    viewModel.authenticateAndEditPassword(
                        binding.changePasswordFragmentPassword.text.toString(),
                        binding.changePasswordFragmentPasswordNew.text.toString()
                    )
                }
            }
        })

        val passwordWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                isOldPasswordValid =
                    binding.changePasswordFragmentPassword.text.toString().isValidPassword()
                isNewPasswordValid =
                    binding.changePasswordFragmentPasswordNew.text.toString().isValidPassword()
                isRepeatPasswordValid =
                    binding.changePasswordFragmentPasswordRepeat.text.toString().isValidPassword()

                if (isOldPasswordValid && isNewPasswordValid && isRepeatPasswordValid) {
                    ViewHelper.enableView(binding.changePasswordFragmentSubmit)
                } else {
                    ViewHelper.disableView(binding.changePasswordFragmentSubmit)
                }
            }
        }

        binding.changePasswordFragmentPassword.addTextChangedListener(passwordWatcher)
        binding.changePasswordFragmentPasswordNew.addTextChangedListener(passwordWatcher)
        binding.changePasswordFragmentPasswordRepeat.addTextChangedListener(passwordWatcher)

    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).distinctUntilChanged()
            .onEach { editPasswordState ->
                when (editPasswordState) {
                    is ChangePasswordState.Init -> Unit

                    is ChangePasswordState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            successDialog.show()
                        }
                    }

                    is ChangePasswordState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.show()
                        }
                    }

                    is ChangePasswordState.ShowError -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@ChangePasswordFragment.requireContext(),
                                getString(R.string.error),
                                editPasswordState.message
                            ).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun validateFields(): Boolean {
        if (!binding.changePasswordFragmentPassword.text.toString().isValidPassword()) {
            InfoDialog(
                this@ChangePasswordFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.changePasswordFragmentPassword.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        if (!binding.changePasswordFragmentPasswordNew.text.toString().isValidPassword()) {
            InfoDialog(
                this@ChangePasswordFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.changePasswordFragmentPasswordNew.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        if (!binding.changePasswordFragmentPasswordRepeat.text.toString().isValidPassword()) {
            InfoDialog(
                this@ChangePasswordFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.changePasswordFragmentPasswordRepeat.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        return true
    }

    private fun validatePassword(): Boolean {
        if (binding.changePasswordFragmentPassword.text.toString() == binding.changePasswordFragmentPasswordNew.text.toString()
            && binding.changePasswordFragmentPassword.text.toString() == binding.changePasswordFragmentPasswordRepeat.text.toString()
        ) {
            InfoDialog(
                this@ChangePasswordFragment.requireContext(),
                getString(R.string.error),
                getString(R.string.error_new_password_not_equal_old_password)
            ).show()
            return false
        }
        if (binding.changePasswordFragmentPasswordRepeat.text.toString() != binding.changePasswordFragmentPasswordNew.text.toString()
            && binding.changePasswordFragmentPasswordNew.text.toString() != binding.changePasswordFragmentPasswordRepeat.text.toString()
        ) {
            InfoDialog(
                this@ChangePasswordFragment.requireContext(),
                getString(R.string.error),
                getString(R.string.error_new_password_not_equal_confirm_new_password)
            ).show()
            return false
        }
        return true
    }
}