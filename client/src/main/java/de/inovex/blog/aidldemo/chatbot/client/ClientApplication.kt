package de.inovex.blog.aidldemo.chatbot.client

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import timber.log.Timber

/**
 * Basic Client Application with Timber and Koin setup
 */
class ClientApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@ClientApplication)
            modules(modules)
        }
    }
}

val modules = listOf(module {
    single { RemoteBotRepository(get(), CoroutineScope(Dispatchers.IO)) }
    viewModel { ClientMainViewModel(get()) }
})