package com.tomorrowit.budgetgamer.data.repository_impl

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.AuthRepoResult
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val loggerRepo: LoggerRepo,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : AuthRepo {

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun getCurrentUserUid(): String? {
        return getCurrentUser()?.uid
    }

    override fun isUserAuthenticated(): Boolean {
        return getCurrentUser() != null
    }

    override fun getUserName(): String? {
        return getCurrentUser()?.displayName
    }

    override fun getUserEmail(): String? {
        return getCurrentUser()?.email
    }

    override fun getUserPhotoUrl(): Uri? {
        return getCurrentUser()?.photoUrl
    }

    override fun setLanguage() {
        firebaseAuth.useAppLanguage()
    }

    override suspend fun reloadUser(): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    getCurrentUser()!!.reload().addOnSuccessListener {
                        loggerRepo.info(tag(), "Successfully changed email")
                        continuation.resume(AuthRepoResult.IsSuccess)
                    }.addOnFailureListener {
                        loggerRepo.error(tag(), it.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(it))
                    }
                } else {
                    loggerRepo.error(tag(), "User is not authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated")))
                }
            }
        }

    override fun getProvider(): String {
        return if (isUserAuthenticated()) {
            getCurrentUser()?.providerData?.get(1)?.providerId ?: ""
        } else {
            ""
        }
    }

    override suspend fun changeEmail(email: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    getCurrentUser()!!.updateEmail(email).addOnSuccessListener {
                        loggerRepo.info(tag(), "Successfully changed email")
                        continuation.resume(AuthRepoResult.IsSuccess)
                    }.addOnFailureListener {
                        loggerRepo.error(tag(), it.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(it))
                    }
                } else {
                    loggerRepo.error(tag(), "User is not authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated")))
                }
            }
        }

    override suspend fun changePassword(password: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    getCurrentUser()!!.updatePassword(password).addOnSuccessListener {
                        loggerRepo.info(tag(), "Successfully changed password")
                        continuation.resume(AuthRepoResult.IsSuccess)
                    }.addOnFailureListener {
                        loggerRepo.error(tag(), it.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(it))
                    }
                } else {
                    loggerRepo.error(tag(), "User is not authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated")))
                }
            }
        }

    override suspend fun resetPassword(email: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (!isUserAuthenticated()) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                        loggerRepo.info(tag(), "Successfully reset password")
                        continuation.resume(AuthRepoResult.IsSuccess)
                    }.addOnFailureListener {
                        loggerRepo.error(tag(), it.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(it))
                    }
                } else {
                    loggerRepo.error(tag(), "A user is already authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("A user is already authenticated")))
                }
            }
        }

    override suspend fun deleteUser(): AuthRepoResult = withContext(coroutineDispatcher) {
        suspendCoroutine { continuation ->
            if (isUserAuthenticated()) {
                getCurrentUser()!!.delete().addOnSuccessListener {
                    loggerRepo.info(tag(), "Successfully deleted user")
                    continuation.resume(AuthRepoResult.IsSuccess)
                }.addOnFailureListener {
                    loggerRepo.error(tag(), it.message.toString())
                    continuation.resume(AuthRepoResult.IsFailure(it))
                }
            } else {
                loggerRepo.error(tag(), "User is not authenticated")
                continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated")))
            }
        }
    }

    override suspend fun setUserName(username: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    val userProfileChangeRequest: UserProfileChangeRequest =
                        UserProfileChangeRequest.Builder().setDisplayName(username).build()
                    getCurrentUser()!!.updateProfile(userProfileChangeRequest)
                        .addOnSuccessListener {
                            loggerRepo.info(tag(), "Successfully set username")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        }.addOnFailureListener {
                            loggerRepo.error(tag(), it.message.toString())
                            continuation.resume(AuthRepoResult.IsFailure(it))
                        }
                } else {
                    loggerRepo.error(tag(), "User is not authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated")))
                }
            }
        }

    override suspend fun createUser(email: String, password: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            loggerRepo.info(tag(), "Successfully sign with phone")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        } else {
                            loggerRepo.error(tag(), "User is null")
                            continuation.resume(AuthRepoResult.IsFailure(Exception("User is null")))
                        }
                    }.addOnFailureListener { exception ->
                        loggerRepo.error(tag(), exception.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(exception))
                    }
            }
        }

    override suspend fun signIn(googleAuthCredential: AuthCredential): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                firebaseAuth.signInWithCredential(googleAuthCredential)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            loggerRepo.info(tag(), "Successfully sign with phone")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        } else {
                            loggerRepo.error(tag(), "User is null")
                            continuation.resume(AuthRepoResult.IsFailure(Exception("User is null")))
                        }
                    }.addOnFailureListener { exception ->
                        loggerRepo.error(tag(), exception.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(exception))
                    }
            }
        }

    override suspend fun signInWithCredential(idToken: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            loggerRepo.info(tag(), "Successfully sign with phone")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        } else {
                            loggerRepo.error(tag(), "User is null")
                            continuation.resume(AuthRepoResult.IsFailure(Exception("User is null")))
                        }
                    }.addOnFailureListener { exception ->
                        loggerRepo.error(tag(), exception.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(exception))
                    }
            }
        }


    override suspend fun signIn(email: String, password: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is already authenticated")))
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        if (it != null) {
                            loggerRepo.info(tag(), "Successfully signed in with email")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        } else {
                            loggerRepo.error(tag(), "Failed to sign in with email")
                            continuation.resume(AuthRepoResult.IsFailure(Exception()))
                        }
                    }.addOnFailureListener {
                        loggerRepo.error(tag(), it.message.toString())
                        continuation.resume(AuthRepoResult.IsFailure(it))
                    }
                }
            }
        }


    override suspend fun reAuthenticateUser(password: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    val currentUser = getCurrentUser()!!

                    val credential =
                        EmailAuthProvider.getCredential(
                            currentUser.email.toString(),
                            password
                        )
                    currentUser.reauthenticate(credential)
                        .addOnSuccessListener {
                            loggerRepo.info(tag(), "Successfully re-authenticated user")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        }.addOnFailureListener {
                            loggerRepo.error(tag(), it.message.toString())
                            continuation.resume(AuthRepoResult.IsFailure(it))
                        }
                } else {
                    loggerRepo.error(tag(), "User is not authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated.")))
                }
            }
        }

    override suspend fun reAuthenticateUser(email: String, password: String): AuthRepoResult =
        withContext(coroutineDispatcher) {
            suspendCoroutine { continuation ->
                if (isUserAuthenticated()) {
                    val currentUser = getCurrentUser()!!

                    val credential = EmailAuthProvider.getCredential(email, password)
                    currentUser.reauthenticate(credential)
                        .addOnSuccessListener {
                            loggerRepo.info(tag(), "Successfully re-authenticated user")
                            continuation.resume(AuthRepoResult.IsSuccess)
                        }.addOnFailureListener {
                            loggerRepo.error(tag(), it.message.toString())
                            continuation.resume(AuthRepoResult.IsFailure(it))
                        }
                } else {
                    loggerRepo.error(tag(), "User is not authenticated")
                    continuation.resume(AuthRepoResult.IsFailure(Exception("User is not authenticated.")))
                }
            }
        }

    override fun signOut() {
        firebaseAuth.signOut()
        loggerRepo.info(tag(), "Successfully sign out")
    }
}