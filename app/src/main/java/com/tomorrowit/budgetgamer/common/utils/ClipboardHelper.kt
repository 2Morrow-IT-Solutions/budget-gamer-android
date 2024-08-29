package com.tomorrowit.budgetgamer.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.domain.usecases.MyToasterUseCase

class ClipboardHelper(
    private val context: Context,
    private val clipboardManager: ClipboardManager,
    private val myToasterUseCase: MyToasterUseCase
) {
    fun copyToClipboard(value: String) {
        val clipData = ClipData.newPlainText("Source Text", value)
        clipboardManager.setPrimaryClip(clipData)
        myToasterUseCase.invoke(context.resources.getString(R.string.link_copied))
    }

    fun copyFromClipboard(): String {
        if (clipboardManager.hasPrimaryClip()) {
            val clip = clipboardManager.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val clipboardText = clip.getItemAt(0).coerceToText(context)
                if (clipboardText != null)
                    return clipboardText.toString()
            }
        }
        return ""
    }
}