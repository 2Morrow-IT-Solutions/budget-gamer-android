package com.tomorrowit.budgetgamer.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.DialogQuestionBinding

class QuestionDialog : Dialog {
    constructor(
        context: Context,
        title: String,
        description: String,
        questionDialogListener: QuestionDialogListener
    ) : super(context) {
        returnDialog(
            context,
            title,
            description,
            context.getString(R.string.yes),
            context.getString(R.string.no),
            questionDialogListener
        )
    }

    constructor(
        context: Context,
        title: String,
        description: String,
        yesText: String,
        noText: String,
        questionDialogListener: QuestionDialogListener
    ) : super(context) {
        returnDialog(context, title, description, yesText, noText, questionDialogListener)
    }


    private fun returnDialog(
        context: Context,
        title: String,
        description: String,
        yesText: String,
        noText: String,
        questionDialogListener: QuestionDialogListener
    ) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding = DialogQuestionBinding.inflate(layoutInflater)
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

        if (title.isEmpty()) {
            ViewHelper.hideView(binding.dialogQuestionTitle)
            val params =
                binding.dialogQuestionDescription.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = 0
            binding.dialogQuestionDescription.layoutParams = params
        }

        binding.apply {
            dialogQuestionTitle.text = title
            dialogQuestionDescription.text = description
            dialogQuestionYes.text = yesText
            dialogQuestionNo.text = noText

            dialogQuestionNo.setOnClickListener {
                questionDialogListener.NoButtonAction()
                dismiss()
            }

            dialogQuestionYes.setOnClickListener {
                questionDialogListener.YesButtonAction()
                dismiss()
            }

        }
        create()
    }

    interface QuestionDialogListener {
        fun YesButtonAction()
        fun NoButtonAction()
    }
}
