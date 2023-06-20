package com.example.farmeraid.home.module

import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.QuotasRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideQuotasRepository() = QuotasRepository()

    @Provides
    @Singleton
    fun provideInventoryRepository() = InventoryRepository()
}