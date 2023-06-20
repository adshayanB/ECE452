package com.example.farmeraid.data.module

import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.QuotasRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideQuotasRepository() : QuotasRepository {
        return QuotasRepository()
    }

    @Singleton
    @Provides
    fun provideInventoryRepository() : InventoryRepository {
        return InventoryRepository()
    }
}