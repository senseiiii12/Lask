package dev.alexmester.database.di

import androidx.room.Room
import dev.alexmester.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = "lask_database",
        )
            .fallbackToDestructiveMigrationFrom(1,2,3,4)
            .build()
    }

    single { get<AppDatabase>().articleDao() }
    single { get<AppDatabase>().articleUserStateDao()}
    single { get<AppDatabase>().feedCacheDao() }
}