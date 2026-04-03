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
            .fallbackToDestructiveMigrationFrom(1,2,3)
            .build()
    }

    single { get<AppDatabase>().newsArticleDao() }
    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().clapDao() }
    single { get<AppDatabase>().readingHistoryDao() }
}