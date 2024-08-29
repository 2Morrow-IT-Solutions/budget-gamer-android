package com.tomorrowit.budgetgamer.common.di

import android.content.Context
import com.tomorrowit.budgetgamer.domain.usecases.permissions.CanAskForPermissionUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.IsPermissionGivenUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.RequirePermissionsUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.notifications.IsNotificationsPermissionGivenUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.notifications.RequireNotificationsPermissionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object PermissionModule {

    @Provides
    @ActivityScoped
    fun provideCanAskForPermissionUseCase(): CanAskForPermissionUseCase {
        return CanAskForPermissionUseCase()
    }

    @Provides
    @ActivityScoped
    fun provideIsPermissionGivenUseCase(@ActivityContext context: Context): IsPermissionGivenUseCase {
        return IsPermissionGivenUseCase(context)
    }

    @Provides
    @ActivityScoped
    fun provideRequirePermissionsUseCase(): RequirePermissionsUseCase {
        return RequirePermissionsUseCase()
    }

    @Provides
    @ActivityScoped
    fun provideIsNotificationsPermissionGivenUseCase(
        isPermissionGivenUseCase: IsPermissionGivenUseCase
    ): IsNotificationsPermissionGivenUseCase {
        return IsNotificationsPermissionGivenUseCase(
            isPermissionGivenUseCase
        )
    }

    @Provides
    @ActivityScoped
    fun provideRequireNotificationsPermissionUseCase(
        requirePermissionsUseCase: RequirePermissionsUseCase,
    ): RequireNotificationsPermissionUseCase {
        return RequireNotificationsPermissionUseCase(requirePermissionsUseCase)
    }
}