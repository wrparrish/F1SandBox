package com.parrishdev.common.udf.test

import com.parrishdev.common.udf.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Test implementation of [DispatcherProvider] using test dispatchers.
 *
 * By default uses [UnconfinedTestDispatcher] which executes coroutines eagerly,
 * making tests deterministic without needing explicit advancement.
 *
 * Usage:
 * ```
 * val testDispatcherProvider = TestDispatcherProvider()
 * val viewModelBundle = createTestViewModelBundle(
 *     dispatcherProvider = testDispatcherProvider
 * )
 * ```
 *
 * For more control over execution timing, use [StandardTestDispatcher]:
 * ```
 * val scheduler = TestCoroutineScheduler()
 * val testDispatcher = StandardTestDispatcher(scheduler)
 * val testDispatcherProvider = TestDispatcherProvider(testDispatcher)
 * // Then use scheduler.advanceUntilIdle() to control execution
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
