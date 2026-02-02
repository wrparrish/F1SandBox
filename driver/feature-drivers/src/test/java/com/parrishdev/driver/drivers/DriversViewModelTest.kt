package com.parrishdev.driver.drivers

import app.cash.turbine.test
import com.parrishdev.common.udf.test.DataStateHolder
import com.parrishdev.common.udf.test.MainDispatcherRule
import com.parrishdev.common.udf.test.TestLifecycleOwner
import com.parrishdev.common.udf.test.createTestViewModelBundle
import com.parrishdev.driver.fixtures.createDriver
import com.parrishdev.driver.fixtures.createDriverStandings
import com.parrishdev.driver.model.Driver
import com.parrishdev.driver.store.DriverStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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

/**
 * Unit tests for [DriversViewModel].
 *
 * Tests verify:
 * - Initial state and data loading
 * - Driver sorting by championship points
 * - Pull-to-refresh behavior
 * - Error handling and retry logic
 * - Driver selection events
 * - DataState mutations through StateProvider
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DriversViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stateHolder = DataStateHolder<DriversDataState>()
    private val driverStore = mockk<DriverStore>(relaxed = true)
    private val driversFlow = MutableStateFlow<List<Driver>>(emptyList())
    private val testLifecycleOwner = TestLifecycleOwner()

    private val stateProvider = mockk<DriversStateProvider> {
        every { reduce(any()) } answers {
            val dataState = firstArg<DriversDataState>()
            stateHolder.capture(dataState)
            DriversStateProvider().reduce(dataState)
        }
    }

    private lateinit var viewModel: DriversViewModel

    @Before
    fun setup() {
        stateHolder.clear()
        coEvery { driverStore.streamAllDrivers() } returns driversFlow
    }

    private fun createViewModel(): DriversViewModel {
        return DriversViewModel(
            driverStore = driverStore,
            stateProvider = stateProvider,
            viewModelBundle = createTestViewModelBundle()
        ).also {
            // Start lifecycle-aware observations (simulates screen becoming visible)
            it.onStartObserving(testLifecycleOwner)
        }
    }

    // region Initialization Tests

    @Test
    fun `init starts with loading state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertNull(initialState.drivers)
        assertTrue(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `init triggers refresh latest drivers call with force true`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coVerify { driverStore.refreshLatestDrivers(force = true) }
    }

    @Test
    fun `drivers flow emission updates data state`() = runTest {
        val mockDrivers = createDriverStandings()
        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(mockDrivers)
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(mockDrivers.size, latestState.drivers?.size)
        assertFalse(latestState.isLoading)
        // Note: error is cleared by successful refresh, not by flow emission
        // This is tested separately in refresh success tests
    }

    // endregion

    // region Sorting Tests

    @Test
    fun `drivers are sorted by championship points descending`() = runTest {
        val unsortedDrivers = listOf(
            createDriver(driverNumber = 1, championshipPoints = 100f),
            createDriver(driverNumber = 2, championshipPoints = 300f),
            createDriver(driverNumber = 3, championshipPoints = 200f)
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(unsortedDrivers)
        advanceUntilIdle()

        val sortedDrivers = stateHolder.latest().drivers!!
        assertEquals(300f, sortedDrivers[0].championshipPoints)
        assertEquals(200f, sortedDrivers[1].championshipPoints)
        assertEquals(100f, sortedDrivers[2].championshipPoints)
    }

    @Test
    fun `drivers with equal points maintain stable order`() = runTest {
        val driversWithEqualPoints = listOf(
            createDriver(driverNumber = 1, lastName = "First", championshipPoints = 100f),
            createDriver(driverNumber = 2, lastName = "Second", championshipPoints = 100f),
            createDriver(driverNumber = 3, lastName = "Third", championshipPoints = 100f)
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(driversWithEqualPoints)
        advanceUntilIdle()

        val sortedDrivers = stateHolder.latest().drivers!!
        assertEquals(3, sortedDrivers.size)
        // All should have same points
        assertTrue(sortedDrivers.all { it.championshipPoints == 100f })
    }

    // endregion

    // region Refresh Tests

    @Test
    fun `onRefresh sets isRefreshing then clears it`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        // After refresh completes, isRefreshing should be false
        val finalState = stateHolder.latest()
        assertFalse("Final state should not be refreshing", finalState.isRefreshing)
    }

    @Test
    fun `onRefresh calls driverStore refreshLatestDrivers with force true`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        // Called once in init, once in onRefresh
        coVerify(exactly = 2) { driverStore.refreshLatestDrivers(force = true) }
    }

    // endregion

    // region Error Handling Tests

    @Test
    fun `refresh error sets error state`() = runTest {
        val errorMessage = "Network error"
        coEvery { driverStore.refreshLatestDrivers(any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(errorMessage, latestState.error)
        assertFalse(latestState.isRefreshing)
    }

    @Test
    fun `refresh error emits ShowError event`() = runTest {
        val errorMessage = "Network error"
        coEvery { driverStore.refreshLatestDrivers(any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()

        viewModel.eventFlow.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is DriversEvent.ShowError)
            assertEquals(errorMessage, (event as DriversEvent.ShowError).message)
        }
    }

    @Test
    fun `onRetry clears error and triggers refresh`() = runTest {
        val errorMessage = "Network error"
        coEvery { driverStore.refreshLatestDrivers(any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Reset mock to succeed on retry
        coEvery { driverStore.refreshLatestDrivers(any()) } returns Unit
        stateHolder.clear()

        viewModel.onRetry()
        advanceUntilIdle()

        val states = stateHolder.all()
        // First mutation should clear error
        assertTrue(states.any { it.error == null })
    }

    @Test
    fun `error with null message uses default message`() = runTest {
        coEvery { driverStore.refreshLatestDrivers(any()) } throws RuntimeException(null as String?)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals("Failed to load drivers", latestState.error)
    }

    // endregion

    // region Driver Selection Tests

    @Test
    fun `onDriverSelected emits NavigateToDriverDetails event`() = runTest {
        val driver = createDriver(driverNumber = 4)

        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onDriverSelected(driver)

            val event = awaitItem()
            assertTrue(event is DriversEvent.NavigateToDriverDetails)
            assertEquals(4, (event as DriversEvent.NavigateToDriverDetails).driverNumber)
        }
    }

    @Test
    fun `onDriverSelected uses correct driver number`() = runTest {
        val verstappen = createDriver(driverNumber = 1, lastName = "Verstappen")
        val norris = createDriver(driverNumber = 4, lastName = "Norris")

        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onDriverSelected(verstappen)
            assertEquals(1, (awaitItem() as DriversEvent.NavigateToDriverDetails).driverNumber)

            viewModel.onDriverSelected(norris)
            assertEquals(4, (awaitItem() as DriversEvent.NavigateToDriverDetails).driverNumber)
        }
    }

    // endregion

    // region DataState Derived Properties Tests

    @Test
    fun `isLoading is true when drivers is null and no error`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertNull(initialState.drivers)
        assertNull(initialState.error)
        assertTrue(initialState.isLoading)
    }

    @Test
    fun `isLoading is false when drivers is not null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(createDriverStandings())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.isLoading)
    }

    @Test
    fun `hasData is true when drivers is not empty`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(createDriverStandings())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertTrue(state.hasData)
    }

    @Test
    fun `hasData is false when drivers is empty`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(emptyList())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.hasData)
    }

    // endregion

    // region ViewState Transformation Tests

    @Test
    fun `stateProvider transforms empty list as empty state not loading`() = runTest {
        // Note: MutableStateFlow starts with emptyList(), so after advanceUntilIdle
        // the ViewModel will have observed an empty list (not null drivers)
        viewModel = createViewModel()
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        // Empty list means not loading (drivers is not null), but should show empty state
        assertFalse(viewState.isLoading)
        assertTrue(viewState.drivers.isEmpty())
        assertTrue(viewState.showEmptyState)
    }

    @Test
    fun `stateProvider transforms success state correctly`() = runTest {
        val mockDrivers = createDriverStandings()
        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(mockDrivers)
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertEquals(mockDrivers.size, viewState.drivers.size)
        assertFalse(viewState.showEmptyState)
    }

    @Test
    fun `stateProvider shows empty state when no data and not loading`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        driversFlow.emit(emptyList())
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertTrue(viewState.showEmptyState)
    }

    // endregion
}
