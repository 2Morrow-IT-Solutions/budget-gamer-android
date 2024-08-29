package com.tomorrowit.budgetgamer.domain.repo

interface MessagingRepo {
    fun subscribeToFreeGamesTopic()
    fun unsubscribeFromFreeGamesTopic()
}