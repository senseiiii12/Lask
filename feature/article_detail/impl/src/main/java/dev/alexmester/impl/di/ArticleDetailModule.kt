package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.domain.usecase.AddClapUseCase
import dev.alexmester.impl.domain.usecase.GetArticleUseCase
import dev.alexmester.impl.domain.usecase.GetAutoTranslateLanguageUseCase
import dev.alexmester.impl.domain.usecase.MarkAsReadUseCase
import dev.alexmester.impl.domain.usecase.ObserveClapCountUseCase
import dev.alexmester.impl.domain.usecase.ObserveIsBookmarkedUseCase
import dev.alexmester.impl.domain.usecase.ToggleBookmarkUseCase
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
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
        ArticleDetailRepositoryImpl(
            local = get(),
            translateApiService = get(),
            preferencesDataSource = get()
        )
    }

    factory { GetArticleUseCase(repository = get()) }
    factory { ObserveIsBookmarkedUseCase(repository = get()) }
    factory { ObserveClapCountUseCase(repository = get()) }
    factory { ToggleBookmarkUseCase(repository = get()) }
    factory { AddClapUseCase(repository = get()) }
    factory { MarkAsReadUseCase(repository = get()) }
    factory { TranslateTextsUseCase(repository = get()) }
    factory { GetAutoTranslateLanguageUseCase(repository = get()) }

    viewModel { (articleId: Long, articleUrl: String) ->
        ArticleDetailViewModel(
            getArticleUseCase = get(),
            observeIsBookmarkedUseCase = get(),
            observeClapCountUseCase = get(),
            toggleBookmarkUseCase = get(),
            addClapUseCase = get(),
            markAsReadUseCase = get(),
            translateTextsUseCase = get(),
            getAutoTranslateLanguageUseCase = get(),
            articleId = articleId,
            articleUrl = articleUrl,
        )
    }
}