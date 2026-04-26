package dev.alexmester.impl

import dev.alexmester.impl.domain.usecase.GetLastCachedAtUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetLastCachedAtUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private val useCase = GetLastCachedAtUseCase(repository)

    @Test
    fun `given no cached data, returns null`() = runTest {
        repository.lastCachedAt = null

        val result = useCase()

        assertNull(result)
    }

    @Test
    fun `given cached data, returns exact timestamp`() = runTest {
        val inputTimestamp = 1_714_128_000_000L
        repository.lastCachedAt = inputTimestamp

        val result = useCase()

        assertEquals(inputTimestamp, result)
    }

    @Test
    fun `given zero timestamp, returns zero`() = runTest {
        repository.lastCachedAt = 0L

        val result = useCase()

        assertEquals(0L, result)
    }
}