package com.tomorrowit.budgetgamer.presentation.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tomorrowit.budgetgamer.common.config.extensions.navigateToNewActivity
import com.tomorrowit.budgetgamer.common.config.extensions.navigateToNewActivityAny
import com.tomorrowit.budgetgamer.databinding.ActivitySplashScreenBinding
import com.tomorrowit.budgetgamer.presentation.viewmodels.AuthState
import com.tomorrowit.budgetgamer.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//Todo: Implement the SplashScreen the way Android wants it later on.
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    AuthState.Init -> {

                    }

                    is AuthState.IsError -> {
                        navigateToNewActivityAny(
                            BlockerActivity(),
                            mapOf(BlockerActivity.PAGE to BlockerActivity.APP_NO_INTERNET)
                        )
                    }

                    AuthState.IsLoading -> {

                    }

                    AuthState.IsMaintenance -> {
                        navigateToNewActivityAny(
                            BlockerActivity(),
                            mapOf(BlockerActivity.PAGE to BlockerActivity.APP_MAINTENANCE)
                        )
                    }

                    AuthState.IsSuccess -> {
                        viewModel.checkNotifications()
                        navigateToNewActivity(MainActivity())
                    }

                    AuthState.IsUpdateRequired -> {
                        navigateToNewActivityAny(
                            BlockerActivity(),
                            mapOf(BlockerActivity.PAGE to BlockerActivity.APP_UPDATE)
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }
}