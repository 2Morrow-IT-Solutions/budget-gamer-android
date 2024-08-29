package com.tomorrowit.budgetgamer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlatformModel(
    override var key: String = "",
    val colour_background: String = "",
    val colour_margin: String = "",
    val colour_text: String = "",
    val logo: String = "",
    val name: String = "",
    val priority: Int = 0
) : Parcelable, FirebaseModel