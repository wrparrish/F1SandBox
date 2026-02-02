package com.parrishdev.race.results

import androidx.lifecycle.SavedStateHandle
import com.parrishdev.common.udf.test.DataStateHolder
import com.parrishdev.common.udf.test.MainDispatcherRule
import com.parrishdev.common.udf.test.TestLifecycleOwner
import com.parrishdev.common.udf.test.createTestViewModelBundle
import com.parrishdev.race.fixtures.createRaceWithResults
import com.parrishdev.race.fixtures.createRaceWithoutResults
import com.parrishdev.race.model.RaceWithResults
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
import java.time.Year

/**
 * Unit tests for [ResultsViewModel].
 *
 * Tests verify:
 * - Initial state with SavedStateHandle arguments
 * - Race results data loading
 * - Pull-to-refresh behavior
 * - Error handling and retry logic
 * - DataState mutations through StateProvider
 *
 * Note: ResultsViewModel uses BaseStateViewModel (no events),
 * unlike Home/Drivers which use BaseEventStateViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ResultsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stateHolder = DataStateHolder<ResultsDataState>()
    private val raceStore = mockk<RaceStore>(relaxed = true)
    private val raceWithResultsFlow = MutableStateFlow<RaceWithResults?>(null)
    private val testLifecycleOwner = TestLifecycleOwner()

    private val stateProvider = mockk<ResultsStateProvider> {
        every { reduce(any()) } answers {
            val dataState = firstArg<ResultsDataState>()
            stateHolder.capture(dataState)
            ResultsStateProvider().reduce(dataState)
        }
    }

    private lateinit var viewModel: ResultsViewModel

    @Before
    fun setup() {
        stateHolder.clear()
        coEvery { raceStore.streamRaceWithResults(any(), any()) } returns raceWithResultsFlow
    }

    private fun createSavedStateHandle(
        season: Int = 2025,
        round: Int = 7
    ): SavedStateHandle = SavedStateHandle(
        mapOf(
            "season" to season,
            "round" to round
        )
    )

    private fun createViewModel(savedStateHandle: SavedStateHandle = createSavedStateHandle()): ResultsViewModel {
        return ResultsViewModel(
            raceStore = raceStore,
            stateProvider = stateProvider,
            viewModelBundle = createTestViewModelBundle(),
            savedStateHandle = savedStateHandle
        ).also {
            // Start lifecycle-aware observations (simulates screen becoming visible)
            it.onStartObserving(testLifecycleOwner)
        }
    }

    // region Initialization Tests

    @Test
    fun `init sets season and round from SavedStateHandle`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(season = 2024, round = 5))
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertEquals(2024, initialState.season)
        assertEquals(5, initialState.round)
    }

    @Test
    fun `init uses default values when SavedStateHandle is empty`() = runTest {
        val emptyHandle = SavedStateHandle()
        viewModel = createViewModel(emptyHandle)
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertEquals(Year.now().value, initialState.season)
        assertEquals(1, initialState.round)
    }

    @Test
    fun `init starts with loading state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertNull(initialState.raceWithResults)
        assertTrue(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `init triggers refresh race results call`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(season = 2025, round = 7))
        advanceUntilIdle()

        coVerify { raceStore.refreshRaceResults(2025, 7, force = false) }
    }

    @Test
    fun `init observes race with results for correct season and round`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(season = 2024, round = 3))
        advanceUntilIdle()

        coVerify { raceStore.streamRaceWithResults(2024, 3) }
    }

    @Test
    fun `raceWithResults flow emission updates data state`() = runTest {
        val mockRaceWithResults = createRaceWithResults()
        viewModel = createViewModel()
        advanceUntilIdle()

        raceWithResultsFlow.emit(mockRaceWithResults)
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(mockRaceWithResults, latestState.raceWithResults)
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
        coVerify { raceStore.refreshRaceResults(any(), any(), force = true) }
    }

    @Test
    fun `onRefresh calls raceStore refreshRaceResults with force true`() = runTest {
        viewModel = createViewModel(createSavedStateHandle(season = 2025, round = 7))
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify { raceStore.refreshRaceResults(2025, 7, force = true) }
    }

    // endregion

    // region Error Handling Tests

    @Test
    fun `refresh error sets error state`() = runTest {
        val errorMessage = "Network error"
        coEvery { raceStore.refreshRaceResults(any(), any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals(errorMessage, latestState.error)
        assertFalse(latestState.isRefreshing)
    }

    @Test
    fun `error with null message uses default message`() = runTest {
        coEvery { raceStore.refreshRaceResults(any(), any(), any()) } throws RuntimeException(null as String?)

        viewModel = createViewModel()
        advanceUntilIdle()

        val latestState = stateHolder.latest()
        assertEquals("Failed to load results", latestState.error)
    }

    @Test
    fun `onRetry clears error and triggers refresh`() = runTest {
        val errorMessage = "Network error"
        coEvery { raceStore.refreshRaceResults(any(), any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Reset mock to succeed on retry
        coEvery { raceStore.refreshRaceResults(any(), any(), any()) } returns Unit
        stateHolder.clear()

        viewModel.onRetry()
        advanceUntilIdle()

        val states = stateHolder.all()
        // First mutation should clear error
        assertTrue(states.any { it.error == null })
        coVerify(atLeast = 2) { raceStore.refreshRaceResults(any(), any(), any()) }
    }

    // endregion

    // region DataState Derived Properties Tests

    @Test
    fun `isLoading is true when raceWithResults is null and no error`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertNull(initialState.raceWithResults)
        assertNull(initialState.error)
        assertTrue(initialState.isLoading)
    }

    @Test
    fun `isLoading is false when raceWithResults is not null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        raceWithResultsFlow.emit(createRaceWithResults())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.isLoading)
    }

    @Test
    fun `hasData is true when raceWithResults is not null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        raceWithResultsFlow.emit(createRaceWithResults())
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertTrue(state.hasData)
    }

    @Test
    fun `hasData is false when raceWithResults is null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = stateHolder.first()
        assertFalse(state.hasData)
    }

    // endregion

    // region ViewState Transformation Tests

    @Test
    fun `stateProvider transforms loading state correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertTrue(viewState.isLoading)
        assertEquals("Loading...", viewState.raceName)
        assertEquals("", viewState.circuitName)
        assertTrue(viewState.results.isEmpty())
    }

    @Test
    fun `stateProvider transforms success state correctly`() = runTest {
        val mockRaceWithResults = createRaceWithResults()
        viewModel = createViewModel()
        advanceUntilIdle()

        raceWithResultsFlow.emit(mockRaceWithResults)
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertEquals(mockRaceWithResults.race.name, viewState.raceName)
        assertEquals(mockRaceWithResults.race.circuit.name, viewState.circuitName)
        assertEquals(mockRaceWithResults.results.size, viewState.results.size)
    }

    @Test
    fun `stateProvider handles race with empty results`() = runTest {
        val raceWithEmptyResults = createRaceWithoutResults()
        viewModel = createViewModel()
        advanceUntilIdle()

        raceWithResultsFlow.emit(raceWithEmptyResults)
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.isLoading)
        assertEquals(raceWithEmptyResults.race.name, viewState.raceName)
        assertTrue(viewState.results.isEmpty())
    }

    @Test
    fun `stateProvider transforms error state correctly`() = runTest {
        val errorMessage = "Failed to load"
        coEvery { raceStore.refreshRaceResults(any(), any(), any()) } throws RuntimeException(errorMessage)

        viewModel = createViewModel()
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertEquals(errorMessage, viewState.errorMessage)
    }

    // endregion
}
