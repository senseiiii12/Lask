package dev.alexmester.impl.domain.interactor

import android.net.Uri
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class ProfileInteractor(
    private val preferencesDataSource: UserPreferencesDataSource,
    private val readingHistoryDao: ReadingHistoryDao,
) {

    suspend fun applyEditChanges(imageUri: Uri?, name: String){
        preferencesDataSource.updateAvatarUri(imageUri)
        preferencesDataSource.updateProfileName(name)
    }

    suspend fun updateStreak(){
        val today = LocalDate.now().toString()
        preferencesDataSource.updateStreak(today)
    }

    fun observeProfile(): Flow<Pair<UserPreferences, Int>> {
        return preferencesDataSource.userPreferences
            .combine(readingHistoryDao.getReadCount()){ prefs, readCount ->
                prefs to readCount
            }
    }
}