package dev.alexmester.lask

import android.app.Application
import android.util.Log
import coil3.EventListener
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.database.di.databaseModule
import dev.alexmester.datastore.di.dataStoreModule
import dev.alexmester.impl.di.newsFeedModule
import dev.alexmester.impl.navigation.NewsFeedImpl
import dev.alexmester.lask.welcome_screen.SplashViewModel
import dev.alexmester.network.di.networkModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val featuresModule = module {
    single<NewsFeedApi> { NewsFeedImpl() }
    // single<ExploreApi> { ExploreImpl() }
    // single<ArticleDetailApi> { ArticleDetailImpl() }
    // single<BookmarksApi> { BookmarksImpl() }
    // single<SettingsApi> { SettingsImpl() }
    // single<SearchApi> { SearchImpl() }
}
val splashModule = module {
    viewModel { SplashViewModel(preferencesDataSource = get()) }
}

class App : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)

            modules(
                splashModule,
                // Core
                networkModule,
                databaseModule,
                dataStoreModule,

                // Feature DI
                newsFeedModule,

                // Feature Navigation
                featuresModule,
            )
        }
    }
}