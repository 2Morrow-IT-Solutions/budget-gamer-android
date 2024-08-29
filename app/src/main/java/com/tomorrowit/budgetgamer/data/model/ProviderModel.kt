package com.tomorrowit.budgetgamer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProviderModel(
    override var key: String = "",
    val colour: String = "",
    val logo: String = "",
    val name: String = ""
) : Parcelable, FirebaseModel
