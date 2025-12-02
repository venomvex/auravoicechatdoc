package com.aura.voicechat.di

import com.aura.voicechat.data.repository.AuthRepositoryImpl
import com.aura.voicechat.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for AuthRepository binding
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Separated from RepositoryModule to avoid duplicate binding conflicts.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
