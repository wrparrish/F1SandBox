package com.parrishdev.driver.drivers

import androidx.lifecycle.SavedStateHandle
import com.parrishdev.common.udf.test.DataStateHolder
import com.parrishdev.common.udf.test.MainDispatcherRule
import com.parrishdev.common.udf.test.TestLifecycleOwner
import com.parrishdev.common.udf.test.createTestViewModelBundle
import com.parrishdev.driver.fixtures.createDriver
import com.parrishdev.driver.model.Driver
import com.parrishdev.driver.store.DriverStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.parrishdev.driver.contracts.DriverDetailsRoute
import androidx.navigation.toRoute

/**
 * Unit tests for [DriverDetailViewModel].
 *
 * Tests verify:
 * - Initial state and SavedStateHandle argument extraction
 * - Driver detail data streaming from database
 * - Pull-to-refresh database cache updates
 * - Error handling and retry states
 * - DataState mutations through StateProvider
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DriverDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stateHolder = DataStateHolder<DriverDetailDataState>()
    private val driverStore = mockk<DriverStore>(relaxed = true)
    private val driverFlow = MutableStateFlow<Driver?>(null)
    private val testLifecycleOwner = TestLifecycleOwner()

    private val stateProvider = mockk<DriverDetailStateProvider> {
        every { reduce(any()) } answers {
            val dataState = firstArg<DriverDetailDataState>()
            stateHolder.capture(dataState)
            DriverDetailStateProvider().reduce(dataState)
        }
    }

    private lateinit var viewModel: DriverDetailViewModel

    @Before
    fun setup() {
        stateHolder.clear()
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every {
            any<SavedStateHandle>().toRoute<DriverDetailsRoute>(
                eq(DriverDetailsRoute::class),
                any()
            )
        } answers {
            val handle = firstArg<SavedStateHandle>()
            val driverNumber = handle.get<Int>("driverNumber") ?: 4
            val season = handle.get<Int>("season") ?: 2025
            DriverDetailsRoute(driverNumber = driverNumber, season = season)
        }
        coEvery { driverStore.streamDriver(any(), any()) } returns driverFlow
    }

    private fun createSavedStateHandle(
        driverNumber: Int = 4
    ): SavedStateHandle = SavedStateHandle(
        mapOf(
            "driverNumber" to driverNumber
        )
    )

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = createSavedStateHandle()
    ): DriverDetailViewModel {
        return DriverDetailViewModel(
            driverStore = driverStore,
            stateProvider = stateProvider,
            viewModelBundle = createTestViewModelBundle(),
            savedStateHandle = savedStateHandle
        ).also {
            it.onStartObserving(testLifecycleOwner)
        }
    }

    // region Initialization Tests

    @Test
    fun `init sets driver number from SavedStateHandle`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(driverNumber = 44))
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertEquals(44, initialState.driverNumber)
    }

    @Test
    fun `init starts with loading state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertNull(initialState.driver)
        assertTrue(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `init triggers refresh latest drivers call`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coVerify { driverStore.refreshLatestDrivers(any(), force = false) }
    }

    @Test
    fun `init observes streamDriver for correct driver number`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(driverNumber = 63))
        advanceUntilIdle()

        coVerify { driverStore.streamDriver(63, any()) }
    }

    @Test
    fun `driver flow emission updates data state`() = runTest {
        val mockDriver = createDriver(driverNumber = 4)
        viewModel = createViewModel()
        advanceUntilIdle()

        driverFlow.emit(mockDriver)
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(mockDriver, latestState.driver)
        assertFalse(latestState.isLoading)
    }

    // endregion

    // region Refresh Tests

    @Test
    fun `onRefresh sets isRefreshing then clears it`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        val finalState = stateHolder.latest()
        assertFalse("Final state should not be refreshing", finalState.isRefreshing)
        coVerify { driverStore.refreshLatestDrivers(any(), force = true) }
    }

    @Test
    fun `onRefresh calls driverStore refreshLatestDrivers with force true`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(driverNumber = 4))
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify { driverStore.refreshLatestDrivers(any(), force = true) }
    }

    // endregion

    // region Error Handling Tests

    @Test
    fun `refresh error sets error state`() = runTest {
        val errorMessage = "Network error"
        coEvery { driverStore.refreshLatestDrivers(any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(errorMessage, latestState.error)
        assertFalse(latestState.isRefreshing)
    }

    @Test
    fun `error with null message uses default message`() = runTest {
        coEvery { driverStore.refreshLatestDrivers(any(), any()) } throws RuntimeException(null as String?)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals("Failed to load driver details", latestState.error)
    }

    @Test
    fun `onRetry clears error and triggers refresh`() = runTest {
        val errorMessage = "Network error"
        coEvery { driverStore.refreshLatestDrivers(any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { driverStore.refreshLatestDrivers(any(), any()) } returns Unit
        stateHolder.clear()

        viewModel.onRetry()
        advanceUntilIdle()

        val states = stateHolder.all()
        assertTrue(states.any { it.error == null })
        coVerify(atLeast = 2) { driverStore.refreshLatestDrivers(any(), any()) }
    }

    // endregion

    // region ViewState Transformation Tests

    @Test
    fun `stateProvider transforms loading state correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertTrue(viewState.isLoading)
        assertNull(viewState.driver)
    }

    @Test
    fun `stateProvider transforms success state correctly`() = runTest {
        val mockDriver = createDriver(driverNumber = 4)
        viewModel = createViewModel()
        advanceUntilIdle()

        driverFlow.emit(mockDriver)
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertEquals(mockDriver, viewState.driver)
    }

    // endregion
}
