package com.parrishdev.common.udf.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit Rule that sets the Main dispatcher to a test dispatcher.
 *
 * This is essential for testing ViewModels that use [viewModelScope] because
 * it defaults to [Dispatchers.Main]. Without this rule, coroutines launched
 * in [viewModelScope] won't execute correctly in tests.
 *
 * Usage:
 * ```
 * class MyViewModelTest {
 *     @get:Rule
 *     val mainDispatcherRule = MainDispatcherRule()
 *
 *     private val viewModel = createViewModel()
 *
 *     @Test
 *     fun `test viewmodel coroutines`() = runTest {
 *         // viewModelScope coroutines will now use the test dispatcher
 *         viewModel.loadData()
 *         advanceUntilIdle()
 *         // assertions...
 *     }
 * }
 * ```
 *
 * Note: If you use [StandardTestDispatcher] instead of [UnconfinedTestDispatcher],
 * you'll need to manually call [advanceUntilIdle()] to execute coroutines.
 *
 * @param testDispatcher The test dispatcher to use as Main (defaults to UnconfinedTestDispatcher)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
