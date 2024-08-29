package com.tomorrowit.budgetgamer.domain.usecases

import android.content.Context

class GetStringResourceForIdUseCase(private val context: Context) {

    operator fun invoke(id: Int): String {
        return context.resources.getString(id)
    }
}