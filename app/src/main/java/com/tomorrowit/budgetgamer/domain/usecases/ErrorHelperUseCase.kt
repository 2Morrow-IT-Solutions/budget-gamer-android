package com.tomorrowit.budgetgamer.domain.usecases

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.domain.listeners.FieldType

class ErrorHelperUseCase(private val context: Context) {

    fun getErrorForField(string: String, fieldType: FieldType): String {
        when (fieldType) {
            FieldType.Email -> {
                return getErrorForEmail(string)
            }

            FieldType.Password -> {
                return getErrorForPassword(string)
            }

            FieldType.Name -> {
                return getErrorForFullName(string)
            }

            FieldType.None -> {
                return getErrorForNone(string)
            }

            FieldType.Normal -> {
                return getErrorForNormal(string)
            }
        }
    }

    private fun getErrorForFullName(string: String): String {
        if (string.length < 3) {
            return context.resources.getString(R.string.error_full_name_min_char)
        }
        if (string.length > 100) {
            return context.resources.getString(R.string.error_full_name_max_char)
        }
        if (string.isEmpty()) {
            return context.resources.getString(R.string.error_no_full_name_added)
        }
        return context.resources.getString(R.string.error_invalid_full_name)
    }

    private fun getErrorForEmail(string: String): String {
        return context.resources.getString(R.string.error_valid_email)
    }

    private fun getErrorForPassword(string: String): String {
        if (string.isEmpty()) {
            return context.resources.getString(R.string.error_password_empty)
        }
        if (string.length < 8) {
            return context.resources.getString(R.string.error_password_small)
        }
        if (!string.matches("(.*[0-9].*)".toRegex())) {
            return context.resources.getString(R.string.error_password_number)
        }
        if (!string.matches("(.*[A-Z].*)".toRegex())) {
            return context.resources.getString(R.string.error_password_uppercase)
        }
        if (!string.matches("(.*[a-z].*)".toRegex())) {
            return context.resources.getString(R.string.error_password_lowercase)
        }
        if (string.length > 100) {
            return context.resources.getString(R.string.error_password_max_chars)
        }
        if (!string.matches("^(?=.*[_,.()$#!?&@]).*$".toRegex())) {
            return context.resources.getString(R.string.error_password_special)
        }
        return context.resources.getString(R.string.error_valid_password)
    }

    private fun getErrorForNone(string: String): String {
        return context.resources.getString(R.string.error_valid_text)
    }

    private fun getErrorForNormal(string: String): String {
        return context.resources.getString(R.string.error_valid_text)
    }

    fun firebaseLoginErrorToMessageSecret(e: Exception): String {
        return if (e is FirebaseTooManyRequestsException) {
            context.resources.getString(R.string.error_too_many_login_requests)
        } else {
            context.resources.getString(R.string.error_wrong_credentials)
        }
    }

    fun firebaseLoginErrorToMessage(e: Exception?): String {
        if (e is FirebaseAuthInvalidCredentialsException) {
            val errorCode = e.errorCode
            if (errorCode == "ERROR_WRONG_PASSWORD") {
                return context.resources.getString(R.string.wrong_password)
            }
        }
        if (e is FirebaseAuthInvalidUserException) {
            val errorCode = e.errorCode
            if (errorCode == "ERROR_USER_NOT_FOUND") {
                return context.getString(R.string.wrong_password)
            }
        }
        if (e is FirebaseNetworkException) {
            return context.resources.getString(R.string.network_error)
        }
        return if (e is FirebaseTooManyRequestsException) {
            context.getString(R.string.too_many_requests)
        } else context.getString(R.string.error_try_again)
    }

    fun firebaseRegisterErrorToMessage(e: Exception?): String {
        if (e is FirebaseAuthUserCollisionException) {
            val errorCode = e.errorCode
            if (errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                return context.getString(R.string.email_in_use)
            }
        }
        if (e is FirebaseNetworkException) {
            return context.resources.getString(R.string.network_error)
        }
        return if (e is FirebaseTooManyRequestsException) {
            context.getString(R.string.too_many_requests)
        } else context.getString(R.string.error_try_again)
    }
}