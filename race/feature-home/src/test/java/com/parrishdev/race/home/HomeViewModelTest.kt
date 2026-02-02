package com.parrishdev.race.home

import app.cash.turbine.test
import com.parrishdev.common.udf.test.DataStateHolder
import com.parrishdev.common.udf.test.MainDispatcherRule
import com.parrishdev.common.udf.test.TestLifecycleOwner
import com.parrishdev.common.udf.test.createTestViewModelBundle
import com.parrishdev.race.fixtures.createRace
import com.parrishdev.race.fixtures.createRaceList
import com.parrishdev.race.store.RaceStore
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
 * Unit tests for [HomeViewModel].
 *
 * Tests verify:
 * - Initial state and data loading
 * - Pull-to-refresh behavior
 * - Error handling and retry logic
 * - Race selection events
 * - DataState mutations through StateProvider
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stateHolder = DataStateHolder<HomeDataState>()
    private val raceStore = mockk<RaceStore>(relaxed = true)
    private val racesFlow = MutableStateFlow<List<com.parrishdev.race.model.Race>>(emptyList())
    private val testLifecycleOwner = TestLifecycleOwner()

    private val stateProvider = mockk<HomeStateProvider> {
        every { reduce(any()) } answers {
            val dataState = firstArg<HomeDataState>()
            stateHolder.capture(dataState)
            HomeStateProvider().reduce(dataState)
        }
    }

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        stateHolder.clear()
        coEvery { raceStore.streamRacesForSeason(any()) } returns racesFlow
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            raceStore = raceStore,
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
        assertNull(initialState.races)
        assertTrue(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `init triggers refresh races call`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coVerify { raceStore.refreshRaces(any(), force = false) }
    }

    @Test
    fun `races flow emission updates data state`() = runTest {
        val mockRaces = createRaceList()
        viewModel = createViewModel()
        advanceUntilIdle()

        racesFlow.emit(mockRaces)
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(mockRaces, latestState.races)
        assertFalse(latestState.isLoading)
        // Note: error is cleared by successful refresh, not by flow emission
        // This is tested separately in refresh success tests
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
        // Verify refresh was actually called with force=true
        coVerify { raceStore.refreshRaces(any(), force = true) }
    }

    @Test
    fun `onRefresh calls raceStore refreshRaces with force true`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify { raceStore.refreshRaces(any(), force = true) }
    }

    // endregion

    // region Error Handling Tests

    @Test
    fun `refresh error sets error state`() = runTest {
        val errorMessage = "Network error"
        coEvery { raceStore.refreshRaces(any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(errorMessage, latestState.error)
        assertFalse(latestState.isRefreshing)
    }

    @Test
    fun `refresh error emits ShowError event`() = runTest {
        val errorMessage = "Network error"
        coEvery { raceStore.refreshRaces(any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()

        viewModel.eventFlow.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is HomeEvent.ShowError)
            assertEquals(errorMessage, (event as HomeEvent.ShowError).message)
        }
    }

    @Test
    fun `onRetry clears error and triggers refresh`() = runTest {
        val errorMessage = "Network error"
        coEvery { raceStore.refreshRaces(any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Reset mock to succeed on retry
        coEvery { raceStore.refreshRaces(any(), any()) } returns Unit
        stateHolder.clear()

        viewModel.onRetry()
        advanceUntilIdle()

        val states = stateHolder.all()
        // First mutation should clear error
        assertTrue(states.any { it.error == null })
        coVerify(atLeast = 2) { raceStore.refreshRaces(any(), any()) }
    }

    // endregion

    // region Race Selection Tests

    @Test
    fun `onRaceSelected emits NavigateToRaceResults event`() = runTest {
        val race = createRace(season = 2025, round = 7, name = "Monaco Grand Prix")

        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onRaceSelected(race)

            val event = awaitItem()
            assertTrue(event is HomeEvent.NavigateToRaceResults)
            val navEvent = event as HomeEvent.NavigateToRaceResults
            assertEquals(2025, navEvent.season)
            assertEquals(7, navEvent.round)
            assertEquals("Monaco Grand Prix", navEvent.raceName)
        }
    }

    // endregion

    // region DataState Derived Properties Tests

    @Test
    fun `isLoading is true when races is null and no error`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertNull(initialState.races)
        assertNull(initialState.error)
        assertTrue(initialState.isLoading)
    }

    @Test
    fun `isLoading is false when races is not null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        racesFlow.emit(createRaceList())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.isLoading)
    }

    @Test
    fun `hasData is true when races is not empty`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        racesFlow.emit(createRaceList())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertTrue(state.hasData)
    }

    @Test
    fun `hasData is false when races is empty`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        racesFlow.emit(emptyList())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.hasData)
    }

    // endregion

    // region ViewState Transformation Tests

    @Test
    fun `stateProvider transforms empty list as empty state not loading`() = runTest {
        // Note: MutableStateFlow starts with emptyList(), so after advanceUntilIdle
        // the ViewModel will have observed an empty list (not null races)
        viewModel = createViewModel()
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        // Empty list means not loading (races is not null), but should show empty state
        assertFalse(viewState.isLoading)
        assertTrue(viewState.races.isEmpty())
        assertTrue(viewState.showEmptyState)
    }

    @Test
    fun `stateProvider transforms success state correctly`() = runTest {
        val mockRaces = createRaceList()
        viewModel = createViewModel()
        advanceUntilIdle()

        racesFlow.emit(mockRaces)
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertEquals(mockRaces.size, viewState.races.size)
        assertFalse(viewState.showEmptyState)
    }

    @Test
    fun `stateProvider shows empty state when no data and not loading`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        racesFlow.emit(emptyList())
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertTrue(viewState.showEmptyState)
    }

    // endregion
}
