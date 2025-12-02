package com.aura.voicechat.di

import com.aura.voicechat.data.repository.*
import com.aura.voicechat.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Note: AuthRepository is bound in AuthBindModule to avoid duplicate bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository
    
    @Binds
    @Singleton
    abstract fun bindRewardsRepository(impl: RewardsRepositoryImpl): RewardsRepository
    
    @Binds
    @Singleton
    abstract fun bindKycRepository(impl: KycRepositoryImpl): KycRepository
}
