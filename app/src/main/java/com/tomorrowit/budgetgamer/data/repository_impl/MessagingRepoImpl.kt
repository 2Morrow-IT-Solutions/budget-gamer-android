package com.tomorrowit.budgetgamer.data.repository_impl

import com.google.firebase.messaging.FirebaseMessaging
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import javax.inject.Inject

class MessagingRepoImpl @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : MessagingRepo {

    //Todo: Later on add these inside a Constants files
    override fun subscribeToFreeGamesTopic() {
        firebaseMessaging.subscribeToTopic("free_games")
    }

    override fun unsubscribeFromFreeGamesTopic() {
        firebaseMessaging.unsubscribeFromTopic("free_games")
    }
}