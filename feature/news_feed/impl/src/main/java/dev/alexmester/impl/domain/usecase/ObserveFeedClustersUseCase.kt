package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveFeedClustersUseCase(
    private val repository: NewsFeedRepository,
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    operator fun invoke(): Flow<Pair<List<NewsCluster>, UserPreferences>> =
        repository.observeFeedClusters()
            .combine(preferencesDataSource.userPreferences) { clusters, prefs ->
                clusters to prefs
            }
}
