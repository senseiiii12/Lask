package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

/**
 * Публичный контракт feature:news-feed.
 *
 * Другие фичи зависят только от этого интерфейса через :api модуль.
 * Реализация (NewsFeedImpl) живёт в :impl и регистрируется в Koin в :app.
 */
interface NewsFeedApi : FeatureApi {

    /** Маршрут к главной ленте топ-новостей */
    fun feedRoute(): FeedRoute
}