package dev.alexmester.impl.di

import dev.alexmester.impl.domain.interactor.ProfileInteractor
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {

//    single {
//        ArticleListLocalDataSource(
//            newsArticleDao = get(),
//            bookmarkDao = get(),
//            clapDao = get(),
//            readingHistoryDao = get(),
//            ioDispatcher = get(named(DISPATCHER_IO)),
//        )
//    }
//
//    single<ArticleListRepository> {
//        ArticleListRepositoryImpl(local = get())
//    }

    factory {
        ProfileInteractor(
            preferencesDataSource = get(),
            readingHistoryDao = get()
        )
    }

    viewModel {
        ProfileViewModel(
            profileInteractor = get(),
        )
    }

//    viewModel { (type: ArticleListType) ->
//        ArticleListViewModel(
//            type = type,
//            interactor = get(),
//        )
//    }
}