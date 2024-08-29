package com.tomorrowit.budgetgamer.common.di

import android.content.Context
import com.tomorrowit.budgetgamer.data.mapper.ArticleMapper
import com.tomorrowit.budgetgamer.data.mapper.GameMapper
import com.tomorrowit.budgetgamer.domain.repo.CollectionRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncArticlesUseCase
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncFreeGamesUseCase
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncSubscriptionGamesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Qualifier

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceContext
    fun provideServiceContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @ServiceScoped
    fun provideSyncFreeGamesUseCase(
        collectionRepo: CollectionRepo,
        gameMapper: GameMapper,
        gameRepo: GameRepo
    ): SyncFreeGamesUseCase {
        return SyncFreeGamesUseCase(collectionRepo, gameMapper, gameRepo)
    }

    @Provides
    @ServiceScoped
    fun provideSyncArticlesUseCase(
        collectionRepo: CollectionRepo,
        articleMapper: ArticleMapper,
        articleRepo: ArticleRepo
    ): SyncArticlesUseCase {
        return SyncArticlesUseCase(collectionRepo, articleMapper, articleRepo)
    }

    @Provides
    @ServiceScoped
    fun provideSyncSubscriptionGamesUseCase(
        collectionRepo: CollectionRepo,
        gameMapper: GameMapper,
        gameRepo: GameRepo
    ): SyncSubscriptionGamesUseCase {
        return SyncSubscriptionGamesUseCase(collectionRepo, gameMapper, gameRepo)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServiceContext
