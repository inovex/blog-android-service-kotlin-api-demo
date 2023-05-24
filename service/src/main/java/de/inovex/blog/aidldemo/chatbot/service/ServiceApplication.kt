package de.inovex.blog.aidldemo.chatbot.service

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import timber.log.Timber

/**
 * Basic Service Application with Timber, Koin and DataStore setup
 */
class ServiceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@ServiceApplication)
            modules(modules)
        }
    }
}

val modules = listOf(module {
    single { get<Context>().dataStore }
    single { LocalBotRepository(get()) }
    single { BotServiceDelegate(get(), CoroutineScope(Dispatchers.IO)) }
    viewModel { ServiceMainViewModel(get()) }
})

val Context.dataStore by preferencesDataStore(name = "bot-datastore")
