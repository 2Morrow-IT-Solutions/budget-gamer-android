package com.tomorrowit.budgetgamer.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.data.entities.ClientPlatformEntity
import com.tomorrowit.budgetgamer.data.entities.GameEntity
import com.tomorrowit.budgetgamer.data.entities.GamePlatformCrossRef
import com.tomorrowit.budgetgamer.data.entities.PlatformEntity
import com.tomorrowit.budgetgamer.data.entities.ProviderEntity
import com.tomorrowit.budgetgamer.data.entities.SubscriptionEntity
import com.tomorrowit.budgetgamer.data.local.room.dao.ArticleDao
import com.tomorrowit.budgetgamer.data.local.room.dao.ClientPlatformDao
import com.tomorrowit.budgetgamer.data.local.room.dao.GameDao
import com.tomorrowit.budgetgamer.data.local.room.dao.PlatformDao
import com.tomorrowit.budgetgamer.data.local.room.dao.ProviderDao
import com.tomorrowit.budgetgamer.data.local.room.dao.SubscriptionDao

@Database(
    entities = [
        ArticleEntity::class,
        ClientPlatformEntity::class,
        GameEntity::class,
        GamePlatformCrossRef::class,
        PlatformEntity::class,
        ProviderEntity::class,
        SubscriptionEntity::class
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun clientPlatformDao(): ClientPlatformDao
    abstract fun gameDao(): GameDao
    abstract fun platformDao(): PlatformDao
    abstract fun providerDao(): ProviderDao
    abstract fun subscriptionDao(): SubscriptionDao
}