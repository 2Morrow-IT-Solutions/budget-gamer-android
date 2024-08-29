package com.tomorrowit.budgetgamer.presentation.activities

import android.os.Bundle
import androidx.activity.viewModels
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.databinding.ActivityEditAccountBinding
import com.tomorrowit.budgetgamer.presentation.base.BaseSlideActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account.EditAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountActivity : BaseSlideActivity() {

    private lateinit var binding: ActivityEditAccountBinding

    private val viewModel: EditAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }
}