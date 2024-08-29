package com.tomorrowit.budgetgamer.presentation.activities

import android.os.Bundle
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.databinding.ActivityAuthBinding
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.presentation.base.BaseSlideActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseSlideActivity() {
    private lateinit var binding: ActivityAuthBinding

    @Inject
    lateinit var loggerRepo: LoggerRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }
}