package com.tomorrowit.budgetgamer.common.di

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import coil.ImageLoader
import com.tomorrowit.budgetgamer.common.utils.ClipboardHelper
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase
import com.tomorrowit.budgetgamer.domain.usecases.MyToasterUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenApplicationSettingsUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenUrlAddressUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.ShareGameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @ActivityInstance
    @Provides
    fun provideActivity(@ActivityContext context: Context): Activity {
        return context as Activity
    }

    @ActivityScoped
    @Provides
    fun provideClipboardHelper(
        @ActivityContext context: Context,
        clipboardManager: ClipboardManager,
        myToasterUseCase: MyToasterUseCase
    ): ClipboardHelper {
        return ClipboardHelper(context, clipboardManager, myToasterUseCase)
    }

    @Provides
    fun provideShareRepository(@ActivityInstance activity: Activity): ShareGameUseCase {
        return ShareGameUseCase(activity)
    }

    @Provides
    fun provideOpenApplicationSettingsUseCase(
        @ActivityInstance activity: Activity,
        myToasterUseCase: MyToasterUseCase
    ): OpenApplicationSettingsUseCase {
        return OpenApplicationSettingsUseCase(activity, myToasterUseCase)
    }

    @Provides
    fun provideOpenUrlAddressUseCase(@ActivityInstance activity: Activity): OpenUrlAddressUseCase {
        return OpenUrlAddressUseCase(activity)
    }

    @ActivityScoped
    @Provides
    fun provideMyToasterUseCase(
        @ActivityContext context: Context
    ): MyToasterUseCase {
        return MyToasterUseCase(context)
    }

    @ActivityScoped
    @Provides
    fun provideLoadImageUseCase(
        @ActivityContext context: Context,
        imageLoader: ImageLoader
    ): LoadImageUseCase {
        return LoadImageUseCase(context, imageLoader)
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ActivityInstance