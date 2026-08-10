package com.codebythura.fruit2048.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.codebythura.fruit2048.data.AppDataSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideBestScoreDataStore(@ApplicationContext context: Context) =
        DataStoreFactory.create(
            serializer = AppDataSerializer,
            produceFile = { context.dataStoreFile("app_data.json") }
        )
}