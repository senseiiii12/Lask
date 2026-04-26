package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_LAST_STREAK_DATE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_STREAK_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class StreakRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferencesDataSource: UserPreferencesDataSource
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = {
                File.createTempFile("test_datastore", ".preferences_pb").apply {
                    deleteOnExit()
                }
            }
        )
        preferencesDataSource = UserPreferencesDataSourceImpl(dataStore)
    }

    /**
     * Следует установить значение серии равным 1, если предыдущая дата отсутствует.
     */
    @Test
    fun `should set streak to 1 when no previous date`() = runTest {
        preferencesDataSource.updateStreak("2026-04-25")

        val prefs = dataStore.data.first()

        assertEquals(1, prefs[KEY_STREAK_COUNT])
        assertEquals("2026-04-25", prefs[KEY_LAST_STREAK_DATE])
    }

    /**
     * Не следует менять серию в течение текущего дня.
     */
    @Test
    fun `should not change streak when same day`() = runTest {
        dataStore.edit {
            it[KEY_LAST_STREAK_DATE] = "2026-04-25"
            it[KEY_STREAK_COUNT] = 5
        }

        preferencesDataSource.updateStreak("2026-04-25")

        val prefs = dataStore.data.first()

        assertEquals(5, prefs[KEY_STREAK_COUNT])
        assertEquals("2026-04-25", prefs[KEY_LAST_STREAK_DATE])
    }

    /**
     * Следует увеличить серию, когда вчера заходили
     */
    @Test
    fun `should increment streak when yesterday`() = runTest {
        dataStore.edit {
            it[KEY_LAST_STREAK_DATE] = "2026-04-24"
            it[KEY_STREAK_COUNT] = 3
        }

        preferencesDataSource.updateStreak("2026-04-25")

        val prefs = dataStore.data.first()

        assertEquals(4, prefs[KEY_STREAK_COUNT])
        assertEquals("2026-04-25", prefs[KEY_LAST_STREAK_DATE])
    }

    /**
     * Серию следует обнулить при пропуске дня.
     */
    @Test
    fun `should reset streak when day skipped`() = runTest {
        dataStore.edit {
            it[KEY_LAST_STREAK_DATE] = "2026-04-20"
            it[KEY_STREAK_COUNT] = 10
        }

        preferencesDataSource.updateStreak("2026-04-25")

        val prefs = dataStore.data.first()

        assertEquals(1, prefs[KEY_STREAK_COUNT])
        assertEquals("2026-04-25", prefs[KEY_LAST_STREAK_DATE])
    }

    /**
     * Следует сбрасывать серию, если сохраненная дата недействительна.
     */
    @Test
    fun `should reset streak when stored date is invalid`() = runTest {
        dataStore.edit {
            it[KEY_LAST_STREAK_DATE] = "invalid-date"
            it[KEY_STREAK_COUNT] = 7
        }

        preferencesDataSource.updateStreak("2026-04-25")

        val prefs = dataStore.data.first()

        assertEquals(1, prefs[KEY_STREAK_COUNT])
    }
}