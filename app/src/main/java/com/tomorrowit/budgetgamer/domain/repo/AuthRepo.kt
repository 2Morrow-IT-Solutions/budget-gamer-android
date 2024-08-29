package com.tomorrowit.budgetgamer.domain.repo

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    fun getCurrentUser(): FirebaseUser?

    fun getCurrentUserUid(): String?

    fun isUserAuthenticated(): Boolean

    fun getUserName(): String?

    fun getUserEmail(): String?

    fun getUserPhotoUrl(): Uri?

    fun setLanguage()

    suspend fun reloadUser(): AuthRepoResult

    fun getProvider(): String

    suspend fun changeEmail(email: String): AuthRepoResult

    suspend fun changePassword(password: String): AuthRepoResult

    suspend fun resetPassword(email: String): AuthRepoResult

    suspend fun deleteUser(): AuthRepoResult

    suspend fun setUserName(username: String): AuthRepoResult

    suspend fun createUser(email: String, password: String): AuthRepoResult

    suspend fun signIn(googleAuthCredential: AuthCredential): AuthRepoResult

    suspend fun signInWithCredential(idToken: String): AuthRepoResult

    suspend fun signIn(email: String, password: String): AuthRepoResult

    suspend fun reAuthenticateUser(password: String): AuthRepoResult

    suspend fun reAuthenticateUser(email: String, password: String): AuthRepoResult

    fun signOut()
}

sealed class AuthRepoResult {
    data object IsSuccess : AuthRepoResult()
    data class IsFailure(val exception: Exception) : AuthRepoResult()
}
