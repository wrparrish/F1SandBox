package com.parrishdev.common.udf.test

import com.parrishdev.common.udf.DispatcherProvider
import com.parrishdev.common.udf.ViewModelBundle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Creates a [ViewModelBundle] configured for testing.
 *
 * By default:
 * - Uses [UnconfinedTestDispatcher] for deterministic, eager execution
 * - Provides a [CoroutineExceptionHandler] that prints exceptions (configurable)
 *
 * @param testDispatcher The test dispatcher to use (defaults to UnconfinedTestDispatcher)
 * @param dispatcherProvider Custom dispatcher provider (defaults to TestDispatcherProvider)
 * @param onException Callback invoked when a coroutine exception is caught
 * @return ViewModelBundle configured for testing
 *
 * Usage:
 * ```
 * @Test
 * fun `test viewmodel behavior`() = runTest {
 *     val viewModelBundle = createTestViewModelBundle()
 *     val viewModel = MyViewModel(
 *         stateProvider = stateProvider,
 *         viewModelBundle = viewModelBundle,
 *         myStore = mockStore
 *     )
 *     // Test assertions...
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createTestViewModelBundle(
    testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
    dispatcherProvider: DispatcherProvider = TestDispatcherProvider(testDispatcher),
    onException: (Throwable) -> Unit = { e ->
        println("Test CoroutineExceptionHandler caught: ${e.message}")
        e.printStackTrace()
    }
): ViewModelBundle = ViewModelBundle(
    coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onException(throwable)
    },
    dispatcherProvider = dispatcherProvider
)

/**
 * Creates a [ViewModelBundle] that throws exceptions immediately.
 * Useful when you want tests to fail fast on uncaught coroutine exceptions.
 *
 * Usage:
 * ```
 * val viewModelBundle = createFailFastViewModelBundle()
 * // Any uncaught exception in coroutines will fail the test immediately
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createFailFastViewModelBundle(
    testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
): ViewModelBundle = ViewModelBundle(
    coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throw throwable
    },
    dispatcherProvider = TestDispatcherProvider(testDispatcher)
)

/**
 * Creates a [ViewModelBundle] that collects exceptions for later assertion.
 *
 * Usage:
 * ```
 * val exceptions = mutableListOf<Throwable>()
 * val viewModelBundle = createExceptionCollectingViewModelBundle(exceptions)
 *
 * // ... run your test ...
 *
 * assertEquals(1, exceptions.size)
 * assertTrue(exceptions[0] is NetworkException)
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createExceptionCollectingViewModelBundle(
    exceptions: MutableList<Throwable>,
    testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
): ViewModelBundle = ViewModelBundle(
    coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        exceptions.add(throwable)
    },
    dispatcherProvider = TestDispatcherProvider(testDispatcher)
)
