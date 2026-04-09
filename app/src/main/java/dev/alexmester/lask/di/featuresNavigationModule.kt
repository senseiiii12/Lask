package dev.alexmester.lask.di

import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.BookmarksApi
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.impl.navigation.ArticleDetailImpl
import dev.alexmester.impl.navigation.BookmarksImpl
import dev.alexmester.impl.navigation.NewsFeedImpl
import dev.alexmester.impl.navigation.ProfileImpl
import org.koin.dsl.module

val featuresNavigationModule = module {
    single<NewsFeedApi> { NewsFeedImpl(articleDetailApi = get()) }
    single<ArticleDetailApi> { ArticleDetailImpl() }
    single<BookmarksApi> { BookmarksImpl(articleDetailApi = get()) }
    single<ProfileApi> { ProfileImpl(articleDetailApi = get()) }
}