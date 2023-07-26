package com.example.farmeraid.location_provider.module

import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.speech_recognition.SpeechRecognizerUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationProviderModule {
    @Singleton
    @Provides
    fun provideLiveLocation(): LocationProvider {
        return LocationProvider()
    }
}