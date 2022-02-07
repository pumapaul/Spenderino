package de.paulweber.spenderino

import de.paulweber.spenderino.model.networking.TokenStoring
import de.paulweber.spenderino.model.networking.client
import de.paulweber.spenderino.model.networking.tokenStore
import de.paulweber.spenderino.model.repositories.balance.BalanceRemoteSource
import de.paulweber.spenderino.model.repositories.balance.BalanceRemoteSourceImpl
import de.paulweber.spenderino.model.repositories.balance.BalanceRepository
import de.paulweber.spenderino.model.repositories.balance.BalanceRepositoryImpl
import de.paulweber.spenderino.model.repositories.donation.DonationRemoteSource
import de.paulweber.spenderino.model.repositories.donation.DonationRemoteSourceImpl
import de.paulweber.spenderino.model.repositories.donation.DonationRepository
import de.paulweber.spenderino.model.repositories.donation.DonationRepositoryImpl
import de.paulweber.spenderino.model.repositories.user.ProfileRemoteSource
import de.paulweber.spenderino.model.repositories.user.ProfileRemoteSourceImpl
import de.paulweber.spenderino.model.repositories.transaction.TransactionRemoteSource
import de.paulweber.spenderino.model.repositories.transaction.TransactionRemoteSourceImpl
import de.paulweber.spenderino.model.repositories.transaction.TransactionRepository
import de.paulweber.spenderino.model.repositories.transaction.TransactionRepositoryImpl
import de.paulweber.spenderino.model.repositories.user.UserRemoteSource
import de.paulweber.spenderino.model.repositories.user.UserRemoteSourceImpl
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserRepositoryImpl
import de.paulweber.spenderino.utility.Log
import org.koin.core.context.startKoin
import org.koin.core.logger.Logger
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(platformModule, commonModule)
}

fun initKoin() = initKoin {
    logger(Log)
}

val commonModule = module {
    single<Logger> { Log }
    single<TokenStoring> { tokenStore }
    single { client }
    factory<UserRemoteSource> { UserRemoteSourceImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    factory<BalanceRemoteSource> { BalanceRemoteSourceImpl() }
    single<BalanceRepository> { BalanceRepositoryImpl() }
    factory<DonationRemoteSource> { DonationRemoteSourceImpl() }
    single<DonationRepository> { DonationRepositoryImpl() }
    factory<TransactionRemoteSource> { TransactionRemoteSourceImpl() }
    single<TransactionRepository> { TransactionRepositoryImpl() }
    factory<ProfileRemoteSource> { ProfileRemoteSourceImpl() }
}

expect val platformModule: Module
