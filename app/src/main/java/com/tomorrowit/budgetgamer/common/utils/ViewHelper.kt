package com.tomorrowit.budgetgamer.common.utils

import android.graphics.Color
import android.view.View
import android.widget.TextView

object ViewHelper {

    @JvmStatic
    fun toggleVisibility(view: View) {
        if (view.visibility == View.VISIBLE) {
            hideView(view)
        } else {
            showView(view)
        }
    }

    @JvmStatic
    fun showView(view: View) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    fun hideView(view: View) {
        if (view.visibility != View.GONE) {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    fun disableView(view: View) {
        if (view.isEnabled) {
            view.isEnabled = false
        }
    }

    @JvmStatic
    fun disableViewAndSetColor(view: View) {
        if (view.isEnabled) {
            view.isEnabled = false

            if (view is TextView) {
                view.setTextColor(Color.parseColor("#525252"))
            }
        }
    }

    @JvmStatic
    fun enableView(view: View) {
        if (!view.isEnabled) {
            view.isEnabled = true
        }
    }
}