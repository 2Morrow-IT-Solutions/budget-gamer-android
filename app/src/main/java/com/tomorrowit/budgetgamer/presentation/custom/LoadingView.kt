package com.tomorrowit.budgetgamer.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.utils.ViewHelper


class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var _titleTextView: TextView
    private var _descriptionTextView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_loading, this, true)

        _titleTextView = findViewById(R.id.loading_title)
        _descriptionTextView = findViewById(R.id.loading_description)

        // Retrieve custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)

        val titleText: String = typedArray.getString(R.styleable.LoadingView_view_title) ?: ""
        val descriptionText: String =
            typedArray.getString(R.styleable.LoadingView_view_description) ?: ""

        typedArray.recycle()

        updateUI(titleText, descriptionText)
    }

    private fun updateUI(titleText: String, descriptionText: String) {
        setTitle(titleText)
        setDescription(descriptionText)
    }

    fun setTitle(value: String) {
        if (value.isEmpty()) {
            ViewHelper.hideView(_titleTextView)
        } else {
            ViewHelper.showView(_titleTextView)
            _titleTextView.text = value
        }
    }

    fun setDescription(value: String) {
        if (value.isEmpty()) {
            ViewHelper.hideView(_descriptionTextView)
        } else {
            ViewHelper.showView(_descriptionTextView)
            _descriptionTextView.text = value
        }
    }
}