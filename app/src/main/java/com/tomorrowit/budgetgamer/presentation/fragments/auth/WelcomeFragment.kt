package com.tomorrowit.budgetgamer.presentation.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentWelcomeBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    private val viewModel: WelcomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentWelcomeBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.welcomeFragmentLogin.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
        })

        binding.changeEmailFragmentBar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        binding.welcomeFragmentRegister.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_welcomeFragment_to_termsFragment)
            }
        })

        binding.welcomeFragmentGoogle.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                findNavController().navigate(R.id.action_welcomeFragment_to_termsFragment_social)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.googleServicesAvailable.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                withContext(Dispatchers.Main) {
                    if (it) {
                        ViewHelper.showView(binding.thirdPartyAuth)
                        ViewHelper.showView(binding.welcomeFragmentOrLabel)
                    } else {
                        ViewHelper.hideView(binding.thirdPartyAuth)
                        ViewHelper.hideView(binding.welcomeFragmentOrLabel)
                    }
                }
            }.launchIn(lifecycleScope)
    }
}