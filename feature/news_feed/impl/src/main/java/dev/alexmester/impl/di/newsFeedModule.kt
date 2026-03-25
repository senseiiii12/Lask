package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.interactor.NewsFeedInteractor
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.presentation.NewsFeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val newsFeedModule = module {

    // Data
    single { NewsFeedApiService(client = get()) }
    single { NewsFeedLocalDataSource(dao = get()) }
    single<NewsFeedRepository> {
        NewsFeedRepositoryImpl(
            remote = get(),
            local = get(),
        )
    }

    // Domain
    factory {
        NewsFeedInteractor(
            repository = get(),
            preferencesDataSource = get(),
        )
    }

    // Presentation
    viewModel { NewsFeedViewModel(interactor = get()) }
}