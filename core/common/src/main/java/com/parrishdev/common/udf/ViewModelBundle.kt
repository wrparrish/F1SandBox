package com.parrishdev.common.udf

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Container for common dependencies needed by ViewModels.
 *
 * Provides:
 * - CoroutineExceptionHandler for centralized error handling
 * - DispatcherProvider for injectable dispatchers (enables testing)
 */
@Singleton
class ViewModelBundle @Inject constructor(
    val coroutineExceptionHandler: CoroutineExceptionHandler,
    val dispatcherProvider: DispatcherProvider
)

/**
 * Interface for providing coroutine dispatchers.
 * Allows swapping dispatchers in tests for deterministic behavior.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/**
 * Default implementation using standard dispatchers.
 */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}
