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
import com.tomorrowit.budgetgamer.common.config.extensions.isValidName
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentChangeNameBinding
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.ChangeNameViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.EditNameState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChangeNameFragment : Fragment() {
    private lateinit var binding: FragmentChangeNameBinding
    private val viewModel: ChangeNameViewModel by viewModels()

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@ChangeNameFragment.requireContext(),
            getString(R.string.please_wait_dots),
            getString(R.string.updating_name)
        )
    }
    private val successDialog: InfoDialog by lazy {
        InfoDialog(
            this@ChangeNameFragment.requireContext(),
            getString(R.string.success),
            getString(R.string.account_name_changed)
        ).apply {
            setOnDismissListener {
                this@ChangeNameFragment.findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChangeNameBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeNameFragmentBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.changeNameFragmentField.setText(viewModel.getName())

        binding.changeNameFragmentField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == viewModel.getName()) {
                    ViewHelper.disableView(binding.changeNameFragmentSubmit)
                } else {
                    if (binding.changeNameFragmentField.text.toString().isValidName()) {
                        ViewHelper.enableView(binding.changeNameFragmentSubmit)
                    }
                }
            }
        })

        binding.changeNameFragmentField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.changeNameFragmentSubmit.performClick()
                true
            } else {
                false
            }
        }

        binding.changeNameFragmentSubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateField()) {
                    viewModel.changeName(binding.changeNameFragmentField.text.toString())
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
                    is EditNameState.Init -> Unit
                    is EditNameState.IsLoading -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.show()
                        }
                    }

                    is EditNameState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            successDialog.show()
                        }
                    }

                    is EditNameState.ShowError -> {
                        withContext(Dispatchers.UI) {
                            loadingDialog.dismiss()
                            InfoDialog(
                                this@ChangeNameFragment.requireContext(),
                                getString(R.string.error),
                                state.message
                            ).show()
                        }
                    }
                }

            }.launchIn(lifecycleScope)
    }

    private fun validateField(): Boolean {
        if (!binding.changeNameFragmentField.text.toString().isValidName()) {
            InfoDialog(
                this@ChangeNameFragment.requireContext(),
                getString(R.string.error),
                viewModel.getErrorForField(
                    binding.changeNameFragmentField.text.toString(),
                    FieldType.Name
                )
            ).show()
            return false
        }
        return true
    }
}