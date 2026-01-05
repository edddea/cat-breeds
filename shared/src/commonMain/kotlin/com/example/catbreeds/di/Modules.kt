package com.example.catbreeds.di

import com.example.catbreeds.data.local.DatabaseFactory
import com.example.catbreeds.data.platform.createHttpClient
import com.example.catbreeds.data.remote.ApiKeyProvider
import com.example.catbreeds.data.remote.TheCatApi
import com.example.catbreeds.data.repo.AuthRepositoryImpl
import com.example.catbreeds.data.repo.BreedRepositoryImpl
import com.example.catbreeds.data.repo.FavoritesRepositoryImpl
import com.example.catbreeds.domain.definitions.Constants.CAT_API_KEY
import com.example.catbreeds.domain.repo.AuthRepository
import com.example.catbreeds.domain.repo.BreedRepository
import com.example.catbreeds.domain.repo.FavoritesRepository
import com.example.catbreeds.domain.usecase.GetBreedDetailUseCase
import com.example.catbreeds.domain.usecase.GetBreedsPageUseCase
import com.example.catbreeds.domain.usecase.GetFavoritesUseCase
import com.example.catbreeds.domain.usecase.LoginUseCase
import com.example.catbreeds.domain.usecase.LogoutUseCase
import com.example.catbreeds.domain.usecase.RefreshBreedsPageUseCase
import com.example.catbreeds.domain.usecase.ToggleFavoriteUseCase
import com.example.catbreeds.presentation.BreedDetailViewModel
import com.example.catbreeds.presentation.BreedListViewModel
import com.example.catbreeds.presentation.FavoritesViewModel
import com.example.catbreeds.presentation.LoginViewModel
import com.example.catbreeds.presentation.SessionViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun sharedModules(): List<Module> = listOf(dataModule, domainModule, presentationModule)

private val dataModule = module {
    single { createHttpClient() }
    single<ApiKeyProvider> { EnvApiKeyProvider() }
    single { TheCatApi(get(), get()) }

    single { DatabaseFactory().create() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<BreedRepository> { BreedRepositoryImpl(get(), get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
}

private val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetBreedsPageUseCase(get()) }
    factory { RefreshBreedsPageUseCase(get()) }
    factory { GetBreedDetailUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { GetFavoritesUseCase(get()) }
}

private val presentationModule = module {
    factory { LoginViewModel(get()) }
    factory { BreedListViewModel(get(), get()) }
    factory { BreedDetailViewModel(get(), get()) }
    factory { FavoritesViewModel(get(), get()) }
    factory { SessionViewModel(get(), get()) }
}

private class EnvApiKeyProvider : ApiKeyProvider {
    override fun apiKeyOrNull(): String {
        return CAT_API_KEY
    }
}
