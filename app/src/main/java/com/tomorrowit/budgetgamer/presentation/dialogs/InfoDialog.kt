package com.tomorrowit.budgetgamer.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.databinding.DialogInfoBinding

class InfoDialog(
    context: Context,
    title: String,
    description: String,
    okText: String = context.getString(R.string.ok)
) : Dialog(context) {

    init {
        setupDialog(title, description, okText)
    }

    private fun setupDialog(title: String, description: String, okText: String) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding = DialogInfoBinding.inflate(layoutInflater)
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
            dialogInfoTitle.text = title
            dialogInfoDescription.text = description
            dialogInfoOk.text = okText
            dialogInfoOk.setOnClickListener {
                dismiss()
            }
        }
        create()
    }
}