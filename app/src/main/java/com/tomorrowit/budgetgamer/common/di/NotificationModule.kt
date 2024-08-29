package com.tomorrowit.budgetgamer.common.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.tomorrowit.budgetgamer.domain.usecases.notifications.CreateNotificationChannelUseCase
import com.tomorrowit.budgetgamer.domain.usecases.notifications.CreateNotificationUseCase
import com.tomorrowit.budgetgamer.domain.usecases.notifications.GetNotificationChannelsUseCase
import com.tomorrowit.budgetgamer.domain.usecases.notifications.ShowNotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideGetNotificationChannelsUseCase(@ApplicationContext context: Context): GetNotificationChannelsUseCase {
        return GetNotificationChannelsUseCase(context)
    }

    @Provides
    @Singleton
    fun provideCreateNotificationChannelUseCase(
        notificationManager: NotificationManager,
        getNotificationChannelsUseCase: GetNotificationChannelsUseCase
    ): CreateNotificationChannelUseCase {
        return CreateNotificationChannelUseCase(
            notificationManager,
            getNotificationChannelsUseCase
        )
    }

    @Provides
    fun provideCreateNotificationUseCase(@ApplicationContext context: Context): CreateNotificationUseCase {
        return CreateNotificationUseCase(context)
    }

    @Provides
    fun provideShowNotificationUseCase(@ApplicationContext context: Context): ShowNotificationUseCase {
        return ShowNotificationUseCase(NotificationManagerCompat.from(context))
    }
}