package com.tomorrowit.budgetgamer.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.utils.ViewHelper

class ErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var _imageView: ImageView
    private var _titleTextView: TextView
    private var _descriptionTextView: TextView
    private var _button: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_error, this, true)
        _imageView = findViewById(R.id.error_image)
        _titleTextView = findViewById(R.id.error_title)
        _descriptionTextView = findViewById(R.id.error_description)
        _button = findViewById(R.id.error_layout_button)
        // Retrieve custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ErrorView)

        val imageResId = typedArray.getResourceId(R.styleable.ErrorView_state_image, 0)
        val titleText: String = typedArray.getString(R.styleable.ErrorView_state_title) ?: ""
        val descriptionText: String =
            typedArray.getString(R.styleable.ErrorView_state_description) ?: ""
        val buttonText: String = typedArray.getString(R.styleable.ErrorView_state_button) ?: ""

        typedArray.recycle()

        updateUI(imageResId, titleText, descriptionText, buttonText)
    }

    private fun updateUI(
        imageResId: Int,
        titleText: String,
        descriptionText: String,
        buttonText: String
    ) {
        setImage(imageResId)
        setTitle(titleText)
        setDescription(descriptionText)
        setButton(buttonText)
    }

    fun setImage(resId: Int) {
        if (resId == 0) {
            ViewHelper.hideView(_imageView)
        } else {
            ViewHelper.showView(_imageView)
            _imageView.setImageResource(resId)
        }
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

    fun setButton(text: String, onClickListener: OnClickListener? = null) {
        if (text.isEmpty()) {
            ViewHelper.hideView(_button)
        } else {
            ViewHelper.showView(_button)
            _button.text = text
            _button.setOnClickListener(onClickListener)
        }
    }
}