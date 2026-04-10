package dev.alexmester.impl.presentation.profile.mvi

import dev.alexmester.api.navigation.ArticleListType

sealed class ProfileSideEffect {
    data class NavigateToArticleList(val type: ArticleListType) : ProfileSideEffect()
}