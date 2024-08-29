package com.tomorrowit.budgetgamer.data.repository_impl

import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.tomorrowit.budgetgamer.BuildConfig
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.domain.repo.GoogleAuthRepo
import com.tomorrowit.budgetgamer.domain.repo.GoogleAuthResult
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleAuthRepoImpl @Inject constructor(
    private val credentialManager: CredentialManager,
    private val loggerRepo: LoggerRepo,
    private val googleSignInOptions: GoogleSignInOptions
) : GoogleAuthRepo {

    override suspend fun signInWithGoogle(credential: Credential): GoogleAuthResult {
        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        GoogleAuthResult.IsSuccess(firebaseCredential)
                    } catch (e: GoogleIdTokenParsingException) {
                        loggerRepo.error(tag(), "Received an invalid Google ID token response", e)
                        GoogleAuthResult.IsFailure(e)
                    }
                } else {
                    loggerRepo.error(tag(), "Unexpected type of credential")
                    GoogleAuthResult.IsFailure(Exception("Unexpected type of credential"))
                }
            }

            else -> {
                loggerRepo.error(tag(), "Unexpected type of credential")
                GoogleAuthResult.IsFailure(Exception("Unexpected type of credential"))
            }
        }
    }

    override fun googleSignInFallback(fragment: Fragment, rcSignIn: Int) {
        val googleSignInClient =
            GoogleSignIn.getClient(fragment.requireActivity(), googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, rcSignIn)
    }

    override suspend fun getCredential(
        fragment: Fragment,
        request: GetCredentialRequest
    ): Credential? {
        return try {
            val result = credentialManager.getCredential(fragment.requireContext(), request)
            result.credential
        } catch (e: GetCredentialException) {
            loggerRepo.error(tag(), "Credential retrieval failed: ${e.localizedMessage}")
            null
        }
    }

    override fun createCredentialRequest(): GetCredentialRequest {
        val rowNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rowNonce.toByteArray())
        val nonce = digest.joinToString("") { "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_SERVER_ID)
            .setAutoSelectEnabled(false)
            .setNonce(nonce)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
}