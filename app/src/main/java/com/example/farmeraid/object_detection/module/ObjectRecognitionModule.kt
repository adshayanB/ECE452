package com.example.farmeraid.object_detection.module


import android.content.Context
import com.example.farmeraid.object_detection.ObjectDetectionUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObjectRecognitionModule {

    @Singleton
    @Provides
    fun provideObjectRecognizer(@ApplicationContext context: Context): ObjectDetectionUtility{
        return ObjectDetectionUtility(context);
    }

}
