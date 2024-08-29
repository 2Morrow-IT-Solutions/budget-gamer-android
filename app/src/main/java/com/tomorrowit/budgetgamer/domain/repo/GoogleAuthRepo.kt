package com.tomorrowit.budgetgamer.domain.repo

import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import com.google.firebase.auth.AuthCredential


interface GoogleAuthRepo {
    suspend fun signInWithGoogle(credential: Credential): GoogleAuthResult

    fun googleSignInFallback(fragment: Fragment, rcSignIn: Int)

    suspend fun getCredential(fragment: Fragment, request: GetCredentialRequest): Credential?

    fun createCredentialRequest(): GetCredentialRequest
}

sealed class GoogleAuthResult {
    data class IsSuccess(val credential: AuthCredential) : GoogleAuthResult()
    data class IsFailure(val exception: Exception) : GoogleAuthResult()
}