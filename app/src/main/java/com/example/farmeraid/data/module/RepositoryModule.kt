package com.example.farmeraid.data.module

import android.content.Context
import com.example.farmeraid.data.CharityRepository
import com.example.farmeraid.data.FarmRepository
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.source.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideQuotasRepository(
        transactionRepository: TransactionRepository,
        networkMonitor: NetworkMonitor,
    ) : QuotasRepository {
        return QuotasRepository(
            transactionRepository = transactionRepository,
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideInventoryRepository(
        userRepository: UserRepository,
        networkMonitor: NetworkMonitor,
    ) : InventoryRepository {
        return InventoryRepository(
            userRepository = userRepository,
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        networkMonitor: NetworkMonitor,
    ): UserRepository {
        return UserRepository(
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideTransactionRepository(
        userRepository: UserRepository,
        networkMonitor: NetworkMonitor,
    ): TransactionRepository {
        return TransactionRepository(
            userRepository = userRepository,
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideMarketRepository(
        quotasRepository: QuotasRepository,
        farmRepository: FarmRepository,
        userRepository: UserRepository,
        networkMonitor: NetworkMonitor,
    ): MarketRepository {
        return MarketRepository(
            farmRepository = farmRepository,
            userRepository = userRepository,
            quotasRepository = quotasRepository,
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideFarmRepository(
        userRepository: UserRepository,
        networkMonitor: NetworkMonitor,
    ): FarmRepository {
        return FarmRepository(
            userRepository = userRepository,
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideCharityRepository(
        userRepository: UserRepository,
        farmRepository: FarmRepository,
        networkMonitor: NetworkMonitor,
    ): CharityRepository {
        return CharityRepository(
            farmRepository = farmRepository,
            userRepository = userRepository,
            networkMonitor = networkMonitor
        )
    }

    @Singleton
    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
    ): NetworkMonitor {
        return NetworkMonitor(
            context = context
        )
    }
}