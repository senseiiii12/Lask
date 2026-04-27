package dev.alexmester.utils.extension.mutex

import kotlinx.coroutines.sync.Mutex

suspend inline fun <T> Mutex.withTryLock(
    block: suspend () -> T
): T? {
    if (!tryLock()) return null
    return try {
        block()
    } finally {
        unlock()
    }
}