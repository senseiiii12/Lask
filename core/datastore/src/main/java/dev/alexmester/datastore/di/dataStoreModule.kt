package dev.alexmester.datastore.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dev.alexmester.datastore.UserPreferencesDataSourceImpl
import dev.alexmester.datastore.UserPreferencesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

val dataStoreModule = module {

    single<UserPreferencesDataSource>{
        UserPreferencesDataSourceImpl(
            dataStore = androidContext().dataStore,
        )
    }
}