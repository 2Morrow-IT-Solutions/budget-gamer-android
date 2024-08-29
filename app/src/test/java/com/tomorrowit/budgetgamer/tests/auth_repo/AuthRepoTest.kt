package com.tomorrowit.budgetgamer.tests.auth_repo

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tomorrowit.budgetgamer.data.repository_impl.AuthRepoImpl
import com.tomorrowit.budgetgamer.domain.repo.AuthRepoResult
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.utils.FieldType
import com.tomorrowit.budgetgamer.utils.Logic
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class AuthRepoTest {

    private lateinit var authRepoImpl: AuthRepoImpl
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var loggerRepo: LoggerRepo
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        firebaseAuth = mock(FirebaseAuth::class.java)
        firebaseUser = mock(FirebaseUser::class.java)
        loggerRepo = mock(LoggerRepo::class.java)
        authRepoImpl = AuthRepoImpl(firebaseAuth, loggerRepo, testDispatcher)
    }

    @Test
    fun `changeEmail - success with valid email`() = runTest(testDispatcher) {
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        val email = "test@example.com"

        Assert.assertTrue(Logic.isFieldCorrect(FieldType.Email, email))

        val task = mock<Task<Void>>()
        `when`(firebaseUser.updateEmail(email)).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<Void>
            onSuccessListener.onSuccess(null)
            task
        }

        val result = authRepoImpl.changeEmail(email)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully changed email"))
    }

    @Test
    fun `changeEmail - failure with invalid email`() = runTest(testDispatcher) {
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        val email = "invalid-email"

        // Validate email before proceeding
        Assert.assertFalse(Logic.isFieldCorrect(FieldType.Email, email))

        val task = mock<Task<Void>>()
        `when`(firebaseUser.updateEmail(email)).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error changing email"))
            task
        }

        val result = authRepoImpl.changeEmail(email)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error changing email"))
    }

    @Test
    fun `resetPassword - success with valid email`() = runTest(testDispatcher) {
        val email = "test@example.com"
        `when`(firebaseAuth.currentUser).thenReturn(null)

        // Validate email before proceeding
        Assert.assertTrue(Logic.isFieldCorrect(FieldType.Email, email))

        val task = mock<Task<Void>>()
        `when`(firebaseAuth.sendPasswordResetEmail(email)).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<Void>
            onSuccessListener.onSuccess(null)
            task
        }

        val result = authRepoImpl.resetPassword(email)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully reset password"))
    }

    @Test
    fun `resetPassword - failure with invalid email`() = runTest(testDispatcher) {
        val email = "invalid-email"
        `when`(firebaseAuth.currentUser).thenReturn(null)

        // Validate email before proceeding
        Assert.assertFalse(Logic.isFieldCorrect(FieldType.Email, email))

        val task = mock<Task<Void>>()
        `when`(firebaseAuth.sendPasswordResetEmail(email)).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error resetting password"))
            task
        }

        val result = authRepoImpl.resetPassword(email)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error resetting password"))
    }

    @Test
    fun `deleteUser - success`() = runTest(testDispatcher) {
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)

        val task = mock<Task<Void>>()
        `when`(firebaseUser.delete()).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<Void>
            onSuccessListener.onSuccess(null)
            task
        }

        val result = authRepoImpl.deleteUser()
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully deleted user"))
    }

    @Test
    fun `deleteUser - failure`() = runTest(testDispatcher) {
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)

        val task = mock<Task<Void>>()
        `when`(firebaseUser.delete()).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error deleting user"))
            task
        }

        val result = authRepoImpl.deleteUser()
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error deleting user"))
    }

    @Test
    fun `setUserName - success`() = runTest(testDispatcher) {
        val username = "TestUser"
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)

        val task = mock<Task<Void>>()
        `when`(firebaseUser.updateProfile(any())).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<Void>
            onSuccessListener.onSuccess(null)
            task
        }

        val result = authRepoImpl.setUserName(username)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully set username"))
    }

    @Test
    fun `setUserName - failure`() = runTest(testDispatcher) {
        val username = "TestUser"
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)

        val task = mock<Task<Void>>()
        `when`(firebaseUser.updateProfile(any())).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error setting username"))
            task
        }

        val result = authRepoImpl.setUserName(username)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error setting username"))
    }

    @Test
    fun `setUserName - user not authenticated`() = runTest(testDispatcher) {
        `when`(firebaseAuth.currentUser).thenReturn(null)

        val result = authRepoImpl.setUserName("TestUser")
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("User is not authenticated"))
    }

    @Test
    fun `createUser - success`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "Password123!"

        val authResult = mock<AuthResult>()
        `when`(firebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mock())
        `when`(authResult.user).thenReturn(firebaseUser)

        val task = mock<Task<AuthResult>>()
        `when`(firebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<AuthResult>
            onSuccessListener.onSuccess(authResult)
            task
        }

        val result = authRepoImpl.createUser(email, password)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully sign with phone"))
    }

    @Test
    fun `createUser - failure`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "Password123!"

        val task = mock<Task<AuthResult>>()
        `when`(firebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error creating user"))
            task
        }

        val result = authRepoImpl.createUser(email, password)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error creating user"))
    }

    @Test
    fun `signIn with AuthCredential - success`() = runTest(testDispatcher) {
        val authCredential = mock<AuthCredential>()

        val authResult = mock<AuthResult>()
        `when`(firebaseAuth.signInWithCredential(authCredential)).thenReturn(mock())
        `when`(authResult.user).thenReturn(firebaseUser)

        val task = mock<Task<AuthResult>>()
        `when`(firebaseAuth.signInWithCredential(authCredential)).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<AuthResult>
            onSuccessListener.onSuccess(authResult)
            task
        }

        val result = authRepoImpl.signIn(authCredential)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully sign with phone"))
    }

    @Test
    fun `signIn with AuthCredential - failure`() = runTest(testDispatcher) {
        val authCredential = mock<AuthCredential>()

        val task = mock<Task<AuthResult>>()
        `when`(firebaseAuth.signInWithCredential(authCredential)).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error signing in"))
            task
        }

        val result = authRepoImpl.signIn(authCredential)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error signing in"))
    }

    @Test
    fun `signIn with idToken - success`() = runTest(testDispatcher) {
        val idToken = "fake_id_token"

        val authResult = mock<AuthResult>()
        `when`(firebaseAuth.signInWithCredential(any())).thenReturn(mock())
        `when`(authResult.user).thenReturn(firebaseUser)

        val task = mock<Task<AuthResult>>()
        `when`(firebaseAuth.signInWithCredential(any())).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<AuthResult>
            onSuccessListener.onSuccess(authResult)
            task
        }

        val result = authRepoImpl.signInWithCredential(idToken)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully sign with phone"))
    }

    @Test
    fun `signIn with idToken - failure`() = runTest(testDispatcher) {
        val idToken = "fake_id_token"

        val task = mock<Task<AuthResult>>()
        `when`(firebaseAuth.signInWithCredential(any())).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error signing in"))
            task
        }

        val result = authRepoImpl.signInWithCredential(idToken)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error signing in"))
    }

    @Test
    fun `reAuthenticateUser with password - success`() = runTest(testDispatcher) {
        val password = "CurrentPassword123!"
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.email).thenReturn("test@example.com")

        val credential = EmailAuthProvider.getCredential("test@example.com", password)
        val task = mock<Task<Void>>()
        `when`(firebaseUser.reauthenticate(credential)).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<Void>
            onSuccessListener.onSuccess(null)
            task
        }

        val result = authRepoImpl.reAuthenticateUser(password)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully re-authenticated user"))
    }

    @Test
    fun `reAuthenticateUser with password - failure`() = runTest(testDispatcher) {
        val password = "CurrentPassword123!"
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.email).thenReturn("test@example.com")

        val credential = EmailAuthProvider.getCredential("test@example.com", password)
        val task = mock<Task<Void>>()
        `when`(firebaseUser.reauthenticate(credential)).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error re-authenticating user"))
            task
        }

        val result = authRepoImpl.reAuthenticateUser(password)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error re-authenticating user"))
    }

    @Test
    fun `reAuthenticateUser with password - user not authenticated`() = runTest(testDispatcher) {
        `when`(firebaseAuth.currentUser).thenReturn(null)

        val result = authRepoImpl.reAuthenticateUser("CurrentPassword123!")
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("User is not authenticated."))
    }

    @Test
    fun `reAuthenticateUser with email and password - success`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "CurrentPassword123!"
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.email).thenReturn(email)

        val credential = EmailAuthProvider.getCredential(email, password)
        val task = mock<Task<Void>>()
        `when`(firebaseUser.reauthenticate(credential)).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val onSuccessListener = invocation.arguments[0] as OnSuccessListener<Void>
            onSuccessListener.onSuccess(null)
            task
        }

        val result = authRepoImpl.reAuthenticateUser(email, password)
        assert(result is AuthRepoResult.IsSuccess)
        verify(loggerRepo).info(any(), eq("Successfully re-authenticated user"))
    }

    @Test
    fun `reAuthenticateUser with email and password - failure`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "CurrentPassword123!"
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.email).thenReturn(email)

        val credential = EmailAuthProvider.getCredential(email, password)
        val task = mock<Task<Void>>()
        `when`(firebaseUser.reauthenticate(credential)).thenReturn(task)
        `when`(task.addOnFailureListener(any())).thenAnswer { invocation ->
            val onFailureListener = invocation.arguments[0] as OnFailureListener
            onFailureListener.onFailure(Exception("Error re-authenticating user"))
            task
        }

        val result = authRepoImpl.reAuthenticateUser(email, password)
        assert(result is AuthRepoResult.IsFailure)
        verify(loggerRepo).error(any(), eq("Error re-authenticating user"))
    }

    @Test
    fun `reAuthenticateUser with email and password - user not authenticated`() =
        runTest(testDispatcher) {
            `when`(firebaseAuth.currentUser).thenReturn(null)

            val result = authRepoImpl.reAuthenticateUser("test@example.com", "CurrentPassword123!")
            assert(result is AuthRepoResult.IsFailure)
            verify(loggerRepo).error(any(), eq("User is not authenticated."))
        }

    @Test
    fun `signOut - success`() = runTest(testDispatcher) {
        authRepoImpl.signOut()
        verify(firebaseAuth).signOut()
        verify(loggerRepo).info(any(), eq("Successfully sign out"))
    }
}