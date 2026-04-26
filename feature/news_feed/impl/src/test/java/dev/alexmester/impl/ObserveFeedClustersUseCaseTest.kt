package dev.alexmester.impl

import app.cash.turbine.test
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.usecase.ObserveFeedClustersUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ObserveFeedClustersUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private val prefsSource = FakeUserPreferencesDataSource()
    private lateinit var useCase: ObserveFeedClustersUseCase

    @Before
    fun setUp() {
        useCase = ObserveFeedClustersUseCase(
            repository = repository,
            preferencesDataSource = prefsSource,
        )
    }

    @Test
    fun `given empty cache, emits empty clusters paired with current prefs`() = runTest {
        useCase().test {
            val (clusters, prefs) = awaitItem()

            assertTrue(clusters.isEmpty())
            assertEquals("us", prefs.defaultCountry)
            assertEquals("en", prefs.defaultLanguage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given clusters in repo, emits clusters paired with current prefs`() = runTest {
        val inputClusters = listOf(buildCluster(1), buildCluster(2))
        repository.emitClusters(inputClusters)

        useCase().test {
            val (clusters, prefs) = awaitItem()

            assertEquals(inputClusters, clusters)
            assertEquals("us", prefs.defaultCountry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when prefs change, re-emits with updated prefs keeping same clusters`() = runTest {
        val inputClusters = listOf(buildCluster(1))
        repository.emitClusters(inputClusters)

        useCase().test {
            awaitItem()

            prefsSource.emit(UserPreferences(defaultCountry = "gb", defaultLanguage = "en"))
            val (clusters, updatedPrefs) = awaitItem()

            assertEquals(inputClusters, clusters)
            assertEquals("gb", updatedPrefs.defaultCountry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when clusters change, re-emits with updated clusters keeping same prefs`() = runTest {
        useCase().test {
            awaitItem()

            val newClusters = listOf(buildCluster(5))
            repository.emitClusters(newClusters)

            val (clusters, prefs) = awaitItem()

            assertEquals(newClusters, clusters)
            assertEquals("us", prefs.defaultCountry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when both clusters and prefs change, emits two separate updates`() = runTest {
        useCase().test {
            awaitItem()

            repository.emitClusters(listOf(buildCluster(1)))
            awaitItem()

            prefsSource.emit(UserPreferences(defaultCountry = "de", defaultLanguage = "de"))
            val (_, prefs) = awaitItem()

            assertEquals("de", prefs.defaultCountry)
            cancelAndIgnoreRemainingEvents()
        }
    }
}


