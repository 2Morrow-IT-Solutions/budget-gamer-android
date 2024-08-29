package com.tomorrowit.budgetgamer.presentation.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tomorrowit.budgetgamer.BuildConfig
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityFade
import com.tomorrowit.budgetgamer.databinding.ActivityBlockerBinding
import com.tomorrowit.budgetgamer.presentation.base.BaseFadeActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.BlockerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class BlockerActivity : BaseFadeActivity() {
    private var page: Int = APP_UPDATE

    private lateinit var binding: ActivityBlockerBinding
    private val viewModel: BlockerViewModel by viewModels()

    companion object {
        const val PAGE: String = "PAGE"
        const val APP_UPDATE = 0
        const val APP_MAINTENANCE = 1
        const val APP_NO_INTERNET = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBlockerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        page = intent.getIntExtra(PAGE, APP_UPDATE)

        when (page) {
            APP_UPDATE -> {
                showError(
                    getString(R.string.update_title),
                    getString(R.string.update_description),
                    R.drawable.error_update
                )
                binding.blockerActivityErrorLayout.setButton(getString(R.string.update)) {
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                            )
                        )
                    }
                }
            }

            APP_MAINTENANCE -> {
                showError(
                    getString(R.string.maintenance_title),
                    getString(R.string.maintenance_description),
                    R.drawable.error_maintenance
                )
            }

            else -> {
                showError(
                    getString(R.string.network_error_title),
                    getString(R.string.network_error_description)
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.hasInternet.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .distinctUntilChanged()
            .onEach {
                if (!it) {
                    openActivityFade(SplashScreenActivity())
                }
            }.launchIn(lifecycleScope)
    }

    private fun showError(
        title: String,
        description: String,
        drawable: Int = R.drawable.error_loading
    ) {
        binding.blockerActivityErrorLayout.setTitle(title)
        binding.blockerActivityErrorLayout.setDescription(description)
        binding.blockerActivityErrorLayout.setImage(drawable)
    }
}