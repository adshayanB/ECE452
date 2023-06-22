package com.example.farmeraid.navigation.module

import com.example.farmeraid.navigation.AppNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigatorModule {
    @Singleton
    @Provides
    fun provideAppNavigator() : AppNavigator {
        return AppNavigator()
    }
}