package com.tomorrowit.budgetgamer.presentation.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.UI
import com.tomorrowit.budgetgamer.common.config.extensions.navigateToNewActivity
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.databinding.FragmentSocialBinding
import com.tomorrowit.budgetgamer.domain.repo.GoogleAuthRepo
import com.tomorrowit.budgetgamer.domain.repo.GoogleAuthResult
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.activities.MainActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.SocialState
import com.tomorrowit.budgetgamer.presentation.viewmodels.auth.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SocialFragment : Fragment() {

    private val RC_SIGN_IN = 9001
    private lateinit var binding: FragmentSocialBinding
    private val viewModel: SocialViewModel by viewModels()

    @Inject
    lateinit var googleAuthRepo: GoogleAuthRepo

    @Inject
    lateinit var loggerRepo: LoggerRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSocialBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signIn()
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    val idToken = it.idToken
                    if (idToken != null) {
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        lifecycleScope.launch {
                            viewModel.authForGoogle(credential)
                        }
                    }
                }
            } catch (e: ApiException) {
                loggerRepo.error(tag(), "Google sign-in failed: ${e.localizedMessage}")
            }
        }
    }

    private fun signIn() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = googleAuthRepo.createCredentialRequest()
            val credential = googleAuthRepo.getCredential(this@SocialFragment, request)
            if (credential != null) {
                val newCredential = googleAuthRepo.signInWithGoogle(credential)
                if (newCredential is GoogleAuthResult.IsSuccess) {
                    val authCredential = newCredential.credential
                    viewModel.authForGoogle(authCredential)
                }
            } else {
                googleAuthRepo.googleSignInFallback(this@SocialFragment, RC_SIGN_IN)
            }
        }
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).distinctUntilChanged()
            .onEach { socialState ->
                when (socialState) {
                    is SocialState.Init -> Unit

                    is SocialState.IsSuccess -> {
                        withContext(Dispatchers.UI) {
                            requireActivity().navigateToNewActivity(MainActivity())
                        }
                    }

                    is SocialState.ShowError -> {
                        withContext(Dispatchers.UI) {
                            InfoDialog(
                                this@SocialFragment.requireContext(),
                                getString(R.string.error),
                                socialState.message
                            ).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }
}