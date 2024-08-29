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
import com.tomorrowit.budgetgamer.common.config.extensions.isValidName
import com.tomorrowit.budgetgamer.common.config.extensions.isValidPassword
import com.tomorrowit.budgetgamer.common.config.extensions.navigateToNewActivity
import com.tomorrowit.budgetgamer.databinding.FragmentRegisterBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.activities.MainActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.RegisterState
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@RegisterFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.creating_account)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.activityRegisterBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.activityRegisterPasswordRepeat.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.activityRegisterSubmit.performClick()
                true
            } else {
                false
            }
        }

        binding.activityRegisterSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateField() && validatePasswords()) {
                    viewModel.register(
                        binding.activityRegisterName.text.toString(),
                        binding.activityRegisterMail.text.toString(),
                        binding.activityRegisterPassword.text.toString()
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
        viewModel.state
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { registerState ->
                when (registerState) {
                    RegisterState.Init -> Unit

                    RegisterState.IsLoading -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.show()
                        }
                    }

                    RegisterState.IsSuccess -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            viewModel.checkNotifications()
                            requireActivity().navigateToNewActivity(MainActivity())
                        }
                    }

                    is RegisterState.ShowError -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@RegisterFragment.requireContext(),
                                getString(R.string.error),
                                registerState.message
                            ).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun validateField(): Boolean {
        if (!binding.activityRegisterPassword.text.toString().isValidPassword()
            && !binding.activityRegisterMail.text.toString().isValidEmail()
            && !binding.activityRegisterPasswordRepeat.text.toString().isValidPassword()
            && !binding.activityRegisterName.text.toString().isValidName()
        ) {
            InfoDialog(
                this@RegisterFragment.requireContext(),
                getString(R.string.error),
                getString(R.string.please_fill_in)
            ).show()
            return false
        }
        if (!binding.activityRegisterName.text.toString().isValidName()) {
            InfoDialog(
                this@RegisterFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.activityRegisterName.text.toString(),
                    FieldType.Name
                )
            ).show()
            return false
        }
        if (!binding.activityRegisterMail.text.toString().isValidEmail()) {
            InfoDialog(
                this@RegisterFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.activityRegisterMail.text.toString(),
                    FieldType.Email
                )
            ).show()
            return false
        }
        if (!binding.activityRegisterPassword.text.toString().isValidPassword()) {
            InfoDialog(
                this@RegisterFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.activityRegisterPassword.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        if (!binding.activityRegisterPasswordRepeat.text.toString().isValidPassword()) {
            InfoDialog(
                this@RegisterFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.activityRegisterPasswordRepeat.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        return true
    }

    private fun validatePasswords(): Boolean {
        if (binding.activityRegisterPassword.text.toString() != binding.activityRegisterPasswordRepeat.text.toString()
            && binding.activityRegisterPasswordRepeat.text.toString() != binding.activityRegisterPassword.text.toString()
        ) {
            InfoDialog(
                this@RegisterFragment.requireContext(),
                getString(R.string.error),
                getString(R.string.passwords_no_match)
            ).show()
            return false
        }
        return true
    }
}