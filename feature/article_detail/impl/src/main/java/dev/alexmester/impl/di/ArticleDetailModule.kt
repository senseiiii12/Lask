package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.interactor.ArticleDetailInteractor
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val articleDetailModule = module {

    single {
        ArticleDetailLocalDataSource(
            newsArticleDao = get(),
            bookmarkDao = get(),
            clapDao = get(),
            readingHistoryDao = get(),
        )
    }

    single<ArticleDetailRepository> {
        ArticleDetailRepositoryImpl(local = get())
    }

    factory {
        ArticleDetailInteractor(repository = get())
    }

    viewModel { (articleId: Long, articleUrl: String) ->
        ArticleDetailViewModel(
            interactor = get(),
            articleId = articleId,
            articleUrl = articleUrl,
        )
    }
}