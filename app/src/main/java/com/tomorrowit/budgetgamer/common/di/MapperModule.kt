package com.tomorrowit.budgetgamer.common.di

import com.tomorrowit.budgetgamer.data.mapper.ArticleMapper
import com.tomorrowit.budgetgamer.data.mapper.ClientPlatformMapper
import com.tomorrowit.budgetgamer.data.mapper.GameMapper
import com.tomorrowit.budgetgamer.data.mapper.PlatformMapper
import com.tomorrowit.budgetgamer.data.mapper.ProviderMapper
import com.tomorrowit.budgetgamer.data.mapper.SubscriptionMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideArticleMapper(): ArticleMapper {
        return ArticleMapper()
    }

    @Provides
    @Singleton
    fun provideGameMapper(): GameMapper {
        return GameMapper()
    }

    @Provides
    @Singleton
    fun providePlatformMapper(): PlatformMapper {
        return PlatformMapper()
    }

    @Provides
    @Singleton
    fun provideProviderMapper(): ProviderMapper {
        return ProviderMapper()
    }

    @Provides
    @Singleton
    fun provideSubscriptionMapper(): SubscriptionMapper {
        return SubscriptionMapper()
    }

    @Provides
    @Singleton
    fun provideClientPlatformMapper(): ClientPlatformMapper {
        return ClientPlatformMapper()
    }
}