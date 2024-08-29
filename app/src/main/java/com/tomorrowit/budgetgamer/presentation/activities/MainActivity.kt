package com.tomorrowit.budgetgamer.presentation.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.common.services.SyncService
import com.tomorrowit.budgetgamer.databinding.ActivityMainBinding
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenApplicationSettingsUseCase
import com.tomorrowit.budgetgamer.presentation.base.BaseFadeActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseFadeActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var preferencesRepo: PreferencesRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        lifecycleScope.launch {
            Log.e(
                tag(),
                "This is the key for notifications: ${preferencesRepo.getMessagingToken().first()}"
            )
        }
        Intent(this, SyncService::class.java).also { intent ->
            startService(intent)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.railNavigation?.setupWithNavController(navHostFragment.navController)
        } else {
            binding.bottomNavigation?.setupWithNavController(navHostFragment.navController)
        }
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (binding.railNavigation?.selectedItemId != R.id.navigation_home) {
                binding.railNavigation?.selectedItemId = R.id.navigation_home
            } else {
                super.onBackPressed()
            }
        } else {
            if (binding.bottomNavigation?.selectedItemId != R.id.navigation_home) {
                binding.bottomNavigation?.selectedItemId = R.id.navigation_home
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the service
        Intent(this, SyncService::class.java).also { intent ->
            stopService(intent)
        }
    }
}