package dev.alexmester.impl

import dev.alexmester.impl.domain.usecase.GetCurrentLocaleUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCurrentLocaleUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private val useCase = GetCurrentLocaleUseCase(repository)

    @Test
    fun `given default locale in repo, returns us and en`() = runTest {
        val (country, language) = useCase()

        assertEquals("us", country)
        assertEquals("en", language)
    }

    @Test
    fun `given custom country and language, returns configured values`() = runTest {
        repository.currentLocale = "fr" to "fr"

        val (country, language) = useCase()

        assertEquals("fr", country)
        assertEquals("fr", language)
    }

    @Test
    fun `given country and language differ, returns both correctly`() = runTest {
        repository.currentLocale = "gb" to "en"

        val (country, language) = useCase()

        assertEquals("gb", country)
        assertEquals("en", language)
    }
}