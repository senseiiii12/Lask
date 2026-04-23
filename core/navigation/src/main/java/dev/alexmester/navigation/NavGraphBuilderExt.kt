package dev.alexmester.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.register(
    featureApi: FeatureApi,
    navController: NavHostController,
) {
    featureApi.registerGraph(
        navGraphBuilder = this,
        navController = navController,
    )
}