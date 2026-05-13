package com.aether.aodaod.core.di

import android.content.Context
import com.aether.aodaod.data.local.preferences.AODPreferences
import com.aether.aodaod.data.repository.AODSettingsRepositoryImpl
import com.aether.aodaod.domain.repository.AODSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAODPreferences(
        @ApplicationContext context: Context
    ): AODPreferences {
        return AODPreferences(context)
    }
}

/**
 * Hilt module for binding repository interfaces to implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAODSettingsRepository(
        impl: AODSettingsRepositoryImpl
    ): AODSettingsRepository
}
