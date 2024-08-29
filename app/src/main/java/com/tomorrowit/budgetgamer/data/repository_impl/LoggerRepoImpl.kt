package com.tomorrowit.budgetgamer.data.repository_impl

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import javax.inject.Inject

class LoggerRepoImpl @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : LoggerRepo {

    private var myUser: String? = null

    override fun setUid(userId: String) {
        if (myUser != userId) {
            myUser = userId
            firebaseCrashlytics.setUserId(userId)
        }
    }

    override fun verbose(tag: String, message: String) {
        Log.v(tag, message)
        sendLogToFirebase(tag, message)
    }

    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
        sendLogToFirebase(tag, message)
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, message)
        sendLogToFirebase(tag, message)
    }

    override fun warning(tag: String, message: String) {
        Log.w(tag, message)
        sendLogToFirebase(tag, message)
    }

    override fun warning(tag: String, message: String, exception: Exception) {
        Log.w(tag, message, exception)
        sendLogToFirebase(tag, message)
    }

    override fun error(tag: String, message: String) {
        Log.e(tag, message)
        sendLogToFirebase(tag, message)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
        sendLogToFirebase(tag, message)
    }

    private fun sendLogToFirebase(tag: String, message: String) {
        firebaseCrashlytics.log("$tag: $message")
    }
}