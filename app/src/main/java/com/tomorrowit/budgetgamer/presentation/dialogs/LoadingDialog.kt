package com.tomorrowit.budgetgamer.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.databinding.DialogLoadingBinding

class LoadingDialog(context: Context, title: String, description: String) : Dialog(context) {

    init {
        setupDialog(title, description)
    }

    private fun setupDialog(title: String, description: String) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.apply {
            setGravity(Gravity.CENTER)
            attributes.windowAnimations = R.style.DialogTheme
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }
        binding.apply {
            dialogLoadingTitle.text = title
            dialogLoadingDescription.text = description
        }

        create()
    }
}
