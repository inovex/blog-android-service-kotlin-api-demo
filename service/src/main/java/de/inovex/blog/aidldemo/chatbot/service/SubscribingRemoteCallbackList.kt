package de.inovex.blog.aidldemo.chatbot.service

import android.os.IInterface
import android.os.RemoteCallbackList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

/**
 * Serves as an extension to androids [RemoteCallbackList] to solve the following tasks:
 * * Threadsafe manipulation of the [callbackList] when doing broadcasts to multiple subscriptions
 * * Automatically flow observation of a given [sourceFlow] when at least one subscription exists
 *
 * @param T type of callback defined in the .aidl file
 * @param U type of observed flow value
 * @param sourceFlow actual source flow of observed value [U]
 * @param coroutineScope to observe source flow on
 * @param actor which calls the actual callback function with a given value
 */
class SubscribingRemoteCallbackList<T : IInterface, U : Any>(
    sourceFlow: Flow<U>,
    coroutineScope: CoroutineScope,
    private val actor: (callback: T, value: U) -> Unit
) : RemoteCallbackList<T>() {

    private val mutex = Mutex()

    private val sharedSourceCommandFlow: MutableStateFlow<SharingCommand> =
        MutableStateFlow(SharingCommand.STOP)

    private val sharedSourceFlow: Flow<U> = sourceFlow
        .shareIn(coroutineScope, { sharedSourceCommandFlow }, 1)
        .onEach { broadcast(it) }

    suspend fun registerAndCollect(callback: T) {
        mutex.withLock {
            Timber.d("register() called")
            register(callback)
            if (registeredCallbackCount == 1) {
                sharedSourceCommandFlow.emit(SharingCommand.START)
            }
        }
        sharedSourceFlow.first()
    }

    suspend fun unregisterAndCancel(callback: T) = mutex.withLock {
        unregister(callback)
        if (registeredCallbackCount == 0) {
            sharedSourceCommandFlow.emit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
        }
    }

    private suspend fun broadcast(value: U) = mutex.withLock {
        Timber.d("broadcast() called with: value = [$value]")
        val receiverCount = beginBroadcast()
        for (i in 0 until receiverCount) {
            actor(getRegisteredCallbackItem(i), value)
        }
        finishBroadcast()
    }

    /**
     * Whenever a callback binding died unexpectedly we ensure that the [sharedSourceFlow] is
     * stopped when no callback is left in the [RemoteCallbackList].
     */
    override fun onCallbackDied(callback: T) {
        super.onCallbackDied(callback)
        if (registeredCallbackCount == 0) {
            sharedSourceCommandFlow.tryEmit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
        }
    }
}