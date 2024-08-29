package com.tomorrowit.budgetgamer.domain.listeners

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0
    private val minClickInterval: Long = 500

    abstract fun onSingleClick(v: View?)

    @Synchronized
    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime

        if (elapsedTime > minClickInterval) {
            lastClickTime = currentClickTime
            onSingleClick(v)
        }
    }
}