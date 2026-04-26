package dev.alexmester.impl

import app.cash.turbine.test
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveReadArticleIdsUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private val useCase = ObserveReadArticleIdsUseCase(repository)

    @Test
    fun `given no read articles, emits empty list`() = runTest {
        useCase().test {
            assertEquals(emptyList<Long>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given read articles in repo, emits their ids`() = runTest {
        val inputIds = listOf(1L, 2L, 3L)
        repository.emitReadIds(inputIds)

        useCase().test {
            assertEquals(inputIds, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when read ids updated once, emits updated list`() = runTest {
        useCase().test {
            awaitItem()

            repository.emitReadIds(listOf(10L, 20L))

            assertEquals(listOf(10L, 20L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when read ids updated multiple times, emits each update in order`() = runTest {
        useCase().test {
            awaitItem() // initial empty

            repository.emitReadIds(listOf(1L))
            assertEquals(listOf(1L), awaitItem())

            repository.emitReadIds(listOf(1L, 2L))
            assertEquals(listOf(1L, 2L), awaitItem())

            repository.emitReadIds(listOf(1L, 2L, 3L))
            assertEquals(listOf(1L, 2L, 3L), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}