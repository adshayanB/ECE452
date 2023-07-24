package com.example.farmeraid.navigation.module

import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
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
    fun provideAppNavigator(snackbarDelegate : SnackbarDelegate) : AppNavigator {
        return AppNavigator(
            snackbarDelegate = snackbarDelegate
        )
    }
}