package com.tomorrowit.budgetgamer.common.di

import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.credentials.CredentialManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.tomorrowit.budgetgamer.BuildConfig
import com.tomorrowit.budgetgamer.common.network.AppInitializer
import com.tomorrowit.budgetgamer.common.utils.MyActivityLifecycleCallbacks
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.usecases.notifications.CreateNotificationChannelUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideInitialiseFirebaseUseCase(
        @ApplicationContext context: Context,
        authRepo: AuthRepo,
        loggerRepo: LoggerRepo,
        createNotificationChannelUseCase: CreateNotificationChannelUseCase
    ): AppInitializer {
        return AppInitializer(context, authRepo, loggerRepo, createNotificationChannelUseCase)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideMyActivityLifecycleCallbacks(loggerRepo: LoggerRepo): MyActivityLifecycleCallbacks {
        return MyActivityLifecycleCallbacks(loggerRepo)
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_SERVER_ID)
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

}