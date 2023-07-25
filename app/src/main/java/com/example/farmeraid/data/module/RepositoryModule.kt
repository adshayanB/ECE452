package com.example.farmeraid.data.module

import com.example.farmeraid.data.CharityRepository
import com.example.farmeraid.data.FarmRepository
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.UserRepository
import com.google.android.play.integrity.internal.f
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideQuotasRepository(transactionRepository: TransactionRepository) : QuotasRepository {
        return QuotasRepository(transactionRepository)
    }

    @Singleton
    @Provides
    fun provideInventoryRepository(userRepository: UserRepository) : InventoryRepository {
        return InventoryRepository(
            userRepository = userRepository
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Singleton
    @Provides
    fun provideTransactionRepository(userRepository: UserRepository): TransactionRepository {
        return TransactionRepository(
            userRepository = userRepository,
        )
    }

    @Singleton
    @Provides
    fun provideMarketRepository(quotasRepository: QuotasRepository, farmRepository: FarmRepository, userRepository: UserRepository): MarketRepository {
        return MarketRepository(
            quotasRepository = quotasRepository,
            farmRepository = farmRepository,
            userRepository = userRepository,
        )
    }

    @Singleton
    @Provides
    fun provideFarmRepository(userRepository: UserRepository): FarmRepository {
        return FarmRepository(
            userRepository = userRepository
        )
    }

    @Singleton
    @Provides
    fun provideCharityRepository(userRepository: UserRepository, farmRepository: FarmRepository): CharityRepository {
        return CharityRepository(
            userRepository = userRepository,
            farmRepository = farmRepository
        )
    }
}