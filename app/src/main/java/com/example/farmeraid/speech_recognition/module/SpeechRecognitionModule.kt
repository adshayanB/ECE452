package com.example.farmeraid.speech_recognition.module

import com.example.farmeraid.speech_recognition.KontinuousSpeechRecognizer
import com.example.farmeraid.speech_recognition.SpeechRecognizerUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechRecognitionModule {

    @Singleton
    @Provides
    fun provideSpeechRecognizer(): SpeechRecognizerUtility {
        return SpeechRecognizerUtility()
    }

    @Singleton
    @Provides
    fun provideKontinuousSpeechRecognizer(): KontinuousSpeechRecognizer {
        return KontinuousSpeechRecognizer()
    }
}
