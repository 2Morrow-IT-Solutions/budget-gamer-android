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
import com.tomorrowit.budgetgamer.common.config.extensions.isValidPassword
import com.tomorrowit.budgetgamer.common.config.extensions.navigateToNewActivity
import com.tomorrowit.budgetgamer.databinding.FragmentLoginBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.activities.MainActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.LoginState
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@LoginFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.logging_in)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLoginBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fragmentLoginBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.fragmentLoginForgot.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        })

        binding.fragmentLoginPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.fragmentLoginSubmit.performClick()
                true
            } else {
                false
            }
        }

        binding.fragmentLoginSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateField()) {
                    viewModel.login(
                        binding.fragmentLoginMail.text.toString(),
                        binding.fragmentLoginPassword.text.toString()
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
            .onEach { loginState ->
                when (loginState) {
                    LoginState.Init -> Unit

                    LoginState.IsLoading -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.show()
                        }
                    }

                    LoginState.IsSuccess -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            viewModel.checkNotifications()
                            requireActivity().navigateToNewActivity(MainActivity())
                        }
                    }

                    is LoginState.ShowError -> {
                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@LoginFragment.requireContext(),
                                getString(R.string.error),
                                loginState.message
                            ).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun validateField(): Boolean {
        if (!binding.fragmentLoginPassword.text.toString().isValidPassword()
            && !binding.fragmentLoginMail.text.toString().isValidEmail()
        ) {
            InfoDialog(
                this@LoginFragment.requireContext(),
                getString(R.string.error),
                getString(R.string.please_fill_in)
            ).show()
            return false
        }
        if (!binding.fragmentLoginMail.text.toString().isValidEmail()) {
            InfoDialog(
                this@LoginFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.fragmentLoginMail.text.toString(),
                    FieldType.Email
                )
            ).show()
            return false
        }
        if (!binding.fragmentLoginPassword.text.toString().isValidPassword()) {
            InfoDialog(
                this@LoginFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.fragmentLoginPassword.text.toString(),
                    FieldType.Password
                )
            ).show()
            return false
        }
        return true
    }
}
