package dev.alexmester.impl

import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.usecase.RefreshFeedUseCase
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefreshFeedUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private val prefsSource = FakeUserPreferencesDataSource()
    private lateinit var useCase: RefreshFeedUseCase

    @Before
    fun setUp() {
        useCase = RefreshFeedUseCase(
            repository = repository,
            preferencesDataSource = prefsSource,
        )
    }

    @Test
    fun `given successful repo response, returns Success with item count`() = runTest {
        repository.refreshResult = AppResult.Success(5)

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(5, (result as AppResult.Success).data)
    }

    @Test
    fun `given default prefs, passes fallback country and language to repository`() = runTest {
        useCase()

        assertEquals("us", repository.lastRefreshCountry)
        assertEquals("en", repository.lastRefreshLanguage)
    }

    @Test
    fun `given custom prefs, passes configured country and language to repository`() = runTest {
        prefsSource.emit(UserPreferences(defaultCountry = "de", defaultLanguage = "de"))

        useCase()

        assertEquals("de", repository.lastRefreshCountry)
        assertEquals("de", repository.lastRefreshLanguage)
    }

    @Test
    fun `given repo returns NoInternet, propagates Failure with NoInternet error`() = runTest {
        repository.refreshResult = AppResult.Failure(NetworkError.NoInternet())

        val result = useCase()

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is NetworkError.NoInternet)
    }

    @Test
    fun `given repo returns RateLimit, propagates Failure with RateLimit error`() = runTest {
        repository.refreshResult = AppResult.Failure(NetworkError.RateLimit(retryAfterSeconds = 60))

        val result = useCase()

        val failure = result as AppResult.Failure
        assertTrue(failure.error is NetworkError.RateLimit)
        assertEquals(60L, (failure.error as NetworkError.RateLimit).retryAfterSeconds)
    }

    @Test
    fun `when called sequentially, executes both calls`() = runTest {
        repository.refreshResult = AppResult.Success(10)

        useCase()
        useCase()

        assertEquals(2, repository.refreshCallCount)
    }
}