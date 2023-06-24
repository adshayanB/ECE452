package com.example.farmeraid.snackbar.module

import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SnackbarModule {
    @Singleton
    @Provides
    fun provideSnackbarDelegate() : SnackbarDelegate {
        return SnackbarDelegate()
    }
}