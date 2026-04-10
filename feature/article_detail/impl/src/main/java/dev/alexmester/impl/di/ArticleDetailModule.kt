package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.interactor.ArticleDetailInteractor
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import dev.alexmester.models.di.DISPATCHER_IO
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val articleDetailModule = module {

    single {
        ArticleDetailLocalDataSource(
            articleDao = get(),
            userStateDao = get(),
            ioDispatcher = get(named(DISPATCHER_IO)),
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