@file:Suppress("Unused")

package com.tomorrowit.budgetgamer.common.di

import com.tomorrowit.budgetgamer.data.repository_impl.AuthRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.CollectionRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.ExistsRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.GoogleAuthRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.LoggerRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.MessagingRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.PreferencesRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.ReadRepoImpl
import com.tomorrowit.budgetgamer.data.remote_repo_impl.RemoteRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl.ArticleRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl.ClientPlatformRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl.GameRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl.PlatformRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl.ProviderRepoImpl
import com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl.SubscriptionRepoImpl
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.CollectionRepo
import com.tomorrowit.budgetgamer.domain.repo.ExistsRepo
import com.tomorrowit.budgetgamer.domain.repo.GoogleAuthRepo
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadRepo
import com.tomorrowit.budgetgamer.domain.remote_repo.RemoteRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ClientPlatformRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.PlatformRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ProviderRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.SubscriptionRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepo(authRepoImpl: AuthRepoImpl): AuthRepo

    @Binds
    abstract fun bindCollectionRepo(collectionRepoImpl: CollectionRepoImpl): CollectionRepo

    @Binds
    abstract fun bindMessagingRepo(messagingRepoImpl: MessagingRepoImpl): MessagingRepo

    @Binds
    abstract fun bindExistsRepo(existsRepoImpl: ExistsRepoImpl): ExistsRepo

    @Binds
    abstract fun bindLoggerRepo(loggerRepoImpl: LoggerRepoImpl): LoggerRepo

    @Binds
    abstract fun bindPreferencesModule(preferencesRepoImpl: PreferencesRepoImpl): PreferencesRepo

    @Binds
    abstract fun bindReadRepo(readRepoImpl: ReadRepoImpl): ReadRepo

    @Binds
    abstract fun bindRetrofitRepo(retrofitRepoImpl: RemoteRepoImpl): RemoteRepo

    @Binds
    abstract fun bindArticleRepo(articleRepoImpl: ArticleRepoImpl): ArticleRepo

    @Binds
    abstract fun bindClientPlatformRepo(clientPlatformRepoImpl: ClientPlatformRepoImpl): ClientPlatformRepo

    @Binds
    abstract fun bindGameRepo(gameRepoImpl: GameRepoImpl): GameRepo

    @Binds
    abstract fun bindPlatformRepo(platformRepoImpl: PlatformRepoImpl): PlatformRepo

    @Binds
    abstract fun bindProviderRepo(providerRepoImpl: ProviderRepoImpl): ProviderRepo

    @Binds
    abstract fun bindSubscriptionRepo(subscriptionRepoImpl: SubscriptionRepoImpl): SubscriptionRepo

    @Binds
    abstract fun bindGoogleAuthRepo(subscriptionRepoImpl: GoogleAuthRepoImpl): GoogleAuthRepo
}