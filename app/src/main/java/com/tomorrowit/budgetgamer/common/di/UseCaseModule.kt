package com.tomorrowit.budgetgamer.common.di

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import com.tomorrowit.budgetgamer.data.mapper.ClientPlatformMapper
import com.tomorrowit.budgetgamer.data.mapper.PlatformMapper
import com.tomorrowit.budgetgamer.data.mapper.ProviderMapper
import com.tomorrowit.budgetgamer.data.mapper.SubscriptionMapper
import com.tomorrowit.budgetgamer.domain.repo.ExistsRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ClientPlatformRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.PlatformRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ProviderRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.SubscriptionRepo
import com.tomorrowit.budgetgamer.domain.usecases.ClearOldDataUseCase
import com.tomorrowit.budgetgamer.domain.usecases.ErrorHelperUseCase
import com.tomorrowit.budgetgamer.domain.usecases.GetHasInternetUseCase
import com.tomorrowit.budgetgamer.domain.usecases.GetStringResourceForIdUseCase
import com.tomorrowit.budgetgamer.domain.usecases.IsGoogleServicesAvailable
import com.tomorrowit.budgetgamer.domain.usecases.IsNewUrlUseCase
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncPlatformSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetStringResourceForIdUseCase(@ApplicationContext context: Context): GetStringResourceForIdUseCase {
        return GetStringResourceForIdUseCase(context)
    }

    @Provides
    fun provideIsNewUrlUseCase(
        existsRepo: ExistsRepo,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): IsNewUrlUseCase {
        return IsNewUrlUseCase(existsRepo, coroutineDispatcher)
    }

    @Provides
    fun provideErrorHelperUseCase(@ApplicationContext context: Context): ErrorHelperUseCase {
        return ErrorHelperUseCase(context)
    }

    @Provides
    fun provideGetConnectivityStatusUseCase(
        connectivityManager: ConnectivityManager,
        @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
    ): GetHasInternetUseCase {
        return GetHasInternetUseCase(connectivityManager, coroutineDispatcher)
    }

    @Provides
    fun provideSyncPlatformSettings(
        readRepo: ReadRepo,
        platformMapper: PlatformMapper,
        platformRepo: PlatformRepo,
        providerMapper: ProviderMapper,
        providerRepo: ProviderRepo,
        subscriptionMapper: SubscriptionMapper,
        subscriptionRepo: SubscriptionRepo,
        clientPlatformMapper: ClientPlatformMapper,
        clientPlatformRepo: ClientPlatformRepo
    ): SyncPlatformSettings {
        return SyncPlatformSettings(
            readRepo,
            platformMapper,
            platformRepo,
            providerMapper,
            providerRepo,
            subscriptionMapper,
            subscriptionRepo,
            clientPlatformMapper,
            clientPlatformRepo
        )
    }

    @Provides
    fun provideIsGoogleServicesAvailable(
        packageManager: PackageManager
    ): IsGoogleServicesAvailable {
        return IsGoogleServicesAvailable(packageManager)
    }

    @Provides
    fun provideClearOldDataUseCase(
        gameRepo: GameRepo,
        articleRepo: ArticleRepo
    ): ClearOldDataUseCase {
        return ClearOldDataUseCase(gameRepo, articleRepo)
    }
}