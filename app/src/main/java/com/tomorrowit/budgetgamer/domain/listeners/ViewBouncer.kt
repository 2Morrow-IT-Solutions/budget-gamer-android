package com.tomorrowit.budgetgamer.domain.listeners

import android.view.View
import android.view.animation.AccelerateInterpolator

class ViewBouncer {
    private var scaleFactor = 0.95f
    private var defaultScale = 1f
    private var duration = 150L

    fun animateScale(view: View) {
        view.animate()
            .scaleX(scaleFactor)
            .scaleY(scaleFactor)
            .setDuration(duration)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(defaultScale)
                    .scaleY(defaultScale)
                    .setDuration(duration)
                    .setInterpolator(AccelerateInterpolator())
                    .start()
            }
            .start()
    }
}