package com.tomorrowit.budgetgamer.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.ActivityNotificationsBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenApplicationSettingsUseCase
import com.tomorrowit.budgetgamer.presentation.base.BaseFadeActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsActivity : BaseFadeActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()

    @Inject
    lateinit var openApplicationSettingsUseCase: OpenApplicationSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.notificationsActivityBar.setNavigationOnClickListener {
            this@NotificationsActivity.finish()
        }

        lifecycleScope.launch {
            if (viewModel.getNotificationValue() == (viewModel.getUid()) + "free_games") {
                binding.notificationsActivityAll.isChecked = true
            }

            binding.notificationsActivityAll.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    if (isChecked) {
                        viewModel.setNotificationValue(viewModel.getUid() + "free_games")
                    } else {
                        viewModel.setNotificationValue("")
                    }
                    if (viewModel.getNotificationValue() == (viewModel.getUid()) + "free_games") {
                        viewModel.subscribeToTopic()
                    } else {
                        viewModel.unsubscribeToTopic()
                    }
                }
            }
            binding.notificationsActivityButton.setOnClickListener(object :
                OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    openApplicationSettingsUseCase.invoke(OpenApplicationSettingsUseCase.NOTIFICATION_SETTINGS)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (NotificationManagerCompat.from(this@NotificationsActivity)
                .areNotificationsEnabled()
        ) {
            ViewHelper.showView(binding.notificationsActivityAll)
            ViewHelper.hideView(binding.notificationsActivityMessage)
            ViewHelper.hideView(binding.notificationsActivityButton)
        } else {
            ViewHelper.hideView(binding.notificationsActivityAll)
            ViewHelper.showView(binding.notificationsActivityMessage)
            ViewHelper.showView(binding.notificationsActivityButton)
        }
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }
}