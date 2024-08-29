package com.tomorrowit.budgetgamer.common.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncArticlesUseCase
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncFreeGamesUseCase
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncSubscriptionGamesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var loggerRepo: LoggerRepo

    @Inject
    lateinit var syncFreeGamesUseCase: SyncFreeGamesUseCase

    @Inject
    lateinit var syncArticlesUseCase: SyncArticlesUseCase

    @Inject
    lateinit var syncSubscriptionGamesUseCase: SyncSubscriptionGamesUseCase

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? {
        loggerRepo.debug(tag(), "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loggerRepo.debug(tag(), "onStartCommand")
        startSyncing()
        return START_STICKY
    }

    private fun startSyncing() {
        scope.launch {
            syncFreeGamesUseCase.invoke()
        }

        scope.launch {
            syncSubscriptionGamesUseCase.invoke()
        }
        scope.launch {
            syncArticlesUseCase.invoke()
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}