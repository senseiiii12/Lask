package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.GetCurrentLocaleUseCase
import dev.alexmester.impl.domain.usecase.ObserveFeedClustersUseCase
import dev.alexmester.impl.domain.usecase.GetLastCachedAtUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsUseCase
import dev.alexmester.impl.domain.usecase.RefreshFeedUseCase
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import dev.alexmester.models.di.DISPATCHER_IO
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newsFeedModule = module {

    single { NewsFeedApiService(client = get()) }

    single {
        NewsFeedLocalDataSource(
            db = get(),
            articleDao = get(),
            feedCacheDao = get(),
            userStateDao = get(),
            ioDispatcher = get(named(DISPATCHER_IO)),
        )
    }

    single<NewsFeedRepository> {
        NewsFeedRepositoryImpl(
            remote = get(),
            local = get(),
        )
    }

    factory { ObserveFeedClustersUseCase(repository = get(), preferencesDataSource = get()) }
    factory { ObserveReadArticleIdsUseCase(repository = get()) }
    factory { GetCurrentLocaleUseCase(preferencesDataSource = get()) }
    factory { GetLastCachedAtUseCase(repository = get()) }
    single { RefreshFeedUseCase(repository = get(), preferencesDataSource = get()) }


    viewModel {
        NewsFeedViewModel(
            observeFeedClusters = get(),
            refreshFeed = get(),
            observeReadArticleIds = get(),
            getCurrentLocale = get(),
            getLastCachedAt = get(),
        )
    }
}