package com.tomorrowit.budgetgamer.domain.usecases.sync

import com.tomorrowit.budgetgamer.data.mapper.ClientPlatformMapper
import com.tomorrowit.budgetgamer.data.mapper.PlatformMapper
import com.tomorrowit.budgetgamer.data.mapper.ProviderMapper
import com.tomorrowit.budgetgamer.data.mapper.SubscriptionMapper
import com.tomorrowit.budgetgamer.domain.repo.ReadRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadResponse
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ClientPlatformRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.PlatformRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ProviderRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.SubscriptionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SyncPlatformSettings(
    private val readRepo: ReadRepo,
    private val platformMapper: PlatformMapper,
    private val platformRepo: PlatformRepo,
    private val providerMapper: ProviderMapper,
    private val providerRepo: ProviderRepo,
    private val subscriptionMapper: SubscriptionMapper,
    private val subscriptionRepo: SubscriptionRepo,
    private val clientPlatformMapper: ClientPlatformMapper,
    private val clientPlatformRepo: ClientPlatformRepo
) {
    private var _firstSyncDone = MutableStateFlow<Boolean>(false)
    val firstSyncDone: StateFlow<Boolean> get() = _firstSyncDone

    suspend operator fun invoke() {
        readRepo.getPlatformSettings().collect { response ->
            if (response is ReadResponse.IsSuccess) {
                response.data.platforms.entries.forEach {
                    it.value.key = it.key
                    platformRepo.upsert(platformMapper.mapFromFirebaseModel(it.value))
                }

                response.data.providers.entries.forEach {
                    it.value.key = it.key
                    providerRepo.upsert(providerMapper.mapFromFirebaseModel(it.value))
                }

                response.data.subscriptions.entries.forEach {
                    it.value.key = it.key
                    subscriptionRepo.upsert(subscriptionMapper.mapFromFirebaseModel(it.value))
                }

                response.data.android.key = "android"
                response.data.ios.key = "ios"
                response.data.web.key = "web"

                clientPlatformRepo.upsert(clientPlatformMapper.mapFromFirebaseModel(response.data.android))
                clientPlatformRepo.upsert(clientPlatformMapper.mapFromFirebaseModel(response.data.ios))
                clientPlatformRepo.upsert(clientPlatformMapper.mapFromFirebaseModel(response.data.web))
            }

            if (!_firstSyncDone.value) {
                _firstSyncDone.value = true
            }
        }
    }
}