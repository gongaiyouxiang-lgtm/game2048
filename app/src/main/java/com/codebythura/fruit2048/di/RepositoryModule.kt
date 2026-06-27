package com.codebythura.fruit2048.di


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.codebythura.fruit2048.repository.DataStoreRepository
import com.codebythura.fruit2048.repository.DataStoreRepositoryImpl
import com.codebythura.fruit2048.repository.GameStateRepository
import com.codebythura.fruit2048.repository.GameStateRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameStateRepository(impl: GameStateRepositoryImpl): GameStateRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(impl: DataStoreRepositoryImpl): DataStoreRepository

}