package com.tomorrowit.budgetgamer.common.di

import android.content.Context
import androidx.room.Room
import com.tomorrowit.budgetgamer.common.config.Constants
import com.tomorrowit.budgetgamer.data.local.room.AppDatabase
import com.tomorrowit.budgetgamer.data.local.room.dao.ArticleDao
import com.tomorrowit.budgetgamer.data.local.room.dao.ClientPlatformDao
import com.tomorrowit.budgetgamer.data.local.room.dao.GameDao
import com.tomorrowit.budgetgamer.data.local.room.dao.PlatformDao
import com.tomorrowit.budgetgamer.data.local.room.dao.ProviderDao
import com.tomorrowit.budgetgamer.data.local.room.dao.SubscriptionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: AppDatabase): ArticleDao {
        return database.articleDao()
    }

    @Provides
    @Singleton
    fun provideClientPlatformDao(database: AppDatabase): ClientPlatformDao {
        return database.clientPlatformDao()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }

    @Provides
    @Singleton
    fun providePlatformDao(database: AppDatabase): PlatformDao {
        return database.platformDao()
    }

    @Provides
    @Singleton
    fun provideProviderDao(database: AppDatabase): ProviderDao {
        return database.providerDao()
    }

    @Provides
    @Singleton
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }
}