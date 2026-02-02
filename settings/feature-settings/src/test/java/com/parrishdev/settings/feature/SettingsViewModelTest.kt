package com.parrishdev.settings.feature

import app.cash.turbine.test
import com.parrishdev.common.udf.test.DataStateHolder
import com.parrishdev.common.udf.test.MainDispatcherRule
import com.parrishdev.common.udf.test.createTestViewModelBundle
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [SettingsViewModel].
 *
 * Tests verify:
 * - Initial state with default setting values
 * - Toggle interactions for each setting
 * - Event emission for settings that notify users
 * - DataState mutations through StateProvider
 *
 * Note: SettingsViewModel has no Store dependency - all state
 * is managed locally (would be persisted to DataStore in production).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stateHolder = DataStateHolder<SettingsDataState>()

    private val stateProvider = mockk<SettingsStateProvider> {
        every { reduce(any()) } answers {
            val dataState = firstArg<SettingsDataState>()
            stateHolder.capture(dataState)
            SettingsStateProvider().reduce(dataState)
        }
    }

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        stateHolder.clear()
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            stateProvider = stateProvider,
            viewModelBundle = createTestViewModelBundle()
        )
    }

    // region Initialization Tests

    @Test
    fun `init starts with default values`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = stateHolder.first()
        assertTrue("Notifications should be enabled by default", initialState.notificationsEnabled)
        assertFalse("Dark mode should be disabled by default", initialState.darkModeEnabled)
        assertFalse("Advanced options should be hidden by default", initialState.showAdvancedOptions)
        assertTrue("Data logging should be enabled by default", initialState.dataLoggingEnabled)
    }

    @Test
    fun `viewState reflects initial data state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertTrue(viewState.notificationsEnabled)
        assertFalse(viewState.darkModeEnabled)
        assertFalse(viewState.showAdvancedOptions)
        assertTrue(viewState.dataLoggingEnabled)
    }

    // endregion

    // region Notifications Toggle Tests

    @Test
    fun `onNotificationsToggled updates state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onNotificationsToggled(false)
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.notificationsEnabled)
    }

    @Test
    fun `onNotificationsToggled emits ShowSettingChanged event`() = runTest {
        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onNotificationsToggled(false)

            val event = awaitItem()
            assertTrue(event is SettingsEvent.ShowSettingChanged)
            val settingEvent = event as SettingsEvent.ShowSettingChanged
            assertEquals("Notifications", settingEvent.settingName)
            assertFalse(settingEvent.enabled)
        }
    }

    @Test
    fun `onNotificationsToggled can toggle back and forth`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Toggle off
        viewModel.onNotificationsToggled(false)
        advanceUntilIdle()
        assertFalse(stateHolder.latest().notificationsEnabled)

        // Toggle on
        viewModel.onNotificationsToggled(true)
        advanceUntilIdle()
        assertTrue(stateHolder.latest().notificationsEnabled)
    }

    // endregion

    // region Dark Mode Toggle Tests

    @Test
    fun `onDarkModeToggled updates state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onDarkModeToggled(true)
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertTrue(state.darkModeEnabled)
    }

    @Test
    fun `onDarkModeToggled emits ShowSettingChanged event`() = runTest {
        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onDarkModeToggled(true)

            val event = awaitItem()
            assertTrue(event is SettingsEvent.ShowSettingChanged)
            val settingEvent = event as SettingsEvent.ShowSettingChanged
            assertEquals("Dark Mode", settingEvent.settingName)
            assertTrue(settingEvent.enabled)
        }
    }

    @Test
    fun `onDarkModeToggled can toggle back and forth`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Toggle on
        viewModel.onDarkModeToggled(true)
        advanceUntilIdle()
        assertTrue(stateHolder.latest().darkModeEnabled)

        // Toggle off
        viewModel.onDarkModeToggled(false)
        advanceUntilIdle()
        assertFalse(stateHolder.latest().darkModeEnabled)
    }

    // endregion

    // region Advanced Options Toggle Tests

    @Test
    fun `onAdvancedOptionsToggled updates state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAdvancedOptionsToggled(true)
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertTrue(state.showAdvancedOptions)
    }

    @Test
    fun `onAdvancedOptionsToggled does NOT emit event`() = runTest {
        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onAdvancedOptionsToggled(true)
            advanceUntilIdle()

            // No event should be emitted
            expectNoEvents()
        }
    }

    @Test
    fun `onAdvancedOptionsToggled can toggle back and forth`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Toggle on
        viewModel.onAdvancedOptionsToggled(true)
        advanceUntilIdle()
        assertTrue(stateHolder.latest().showAdvancedOptions)

        // Toggle off
        viewModel.onAdvancedOptionsToggled(false)
        advanceUntilIdle()
        assertFalse(stateHolder.latest().showAdvancedOptions)
    }

    // endregion

    // region Data Logging Toggle Tests

    @Test
    fun `onDataLoggingToggled updates state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onDataLoggingToggled(false)
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.dataLoggingEnabled)
    }

    @Test
    fun `onDataLoggingToggled emits ShowSettingChanged event`() = runTest {
        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onDataLoggingToggled(false)

            val event = awaitItem()
            assertTrue(event is SettingsEvent.ShowSettingChanged)
            val settingEvent = event as SettingsEvent.ShowSettingChanged
            assertEquals("Data Logging", settingEvent.settingName)
            assertFalse(settingEvent.enabled)
        }
    }

    @Test
    fun `onDataLoggingToggled can toggle back and forth`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Toggle off
        viewModel.onDataLoggingToggled(false)
        advanceUntilIdle()
        assertFalse(stateHolder.latest().dataLoggingEnabled)

        // Toggle on
        viewModel.onDataLoggingToggled(true)
        advanceUntilIdle()
        assertTrue(stateHolder.latest().dataLoggingEnabled)
    }

    // endregion

    // region Multiple Settings Tests

    @Test
    fun `multiple settings can be toggled independently`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onNotificationsToggled(false)
        viewModel.onDarkModeToggled(true)
        viewModel.onAdvancedOptionsToggled(true)
        viewModel.onDataLoggingToggled(false)
        advanceUntilIdle()

        val state = stateHolder.latest()
        assertFalse(state.notificationsEnabled)
        assertTrue(state.darkModeEnabled)
        assertTrue(state.showAdvancedOptions)
        assertFalse(state.dataLoggingEnabled)
    }

    @Test
    fun `settings events are emitted in order`() = runTest {
        viewModel = createViewModel()

        viewModel.eventFlow.test {
            viewModel.onNotificationsToggled(false)
            viewModel.onDarkModeToggled(true)
            viewModel.onDataLoggingToggled(false)

            val event1 = awaitItem() as SettingsEvent.ShowSettingChanged
            assertEquals("Notifications", event1.settingName)

            val event2 = awaitItem() as SettingsEvent.ShowSettingChanged
            assertEquals("Dark Mode", event2.settingName)

            val event3 = awaitItem() as SettingsEvent.ShowSettingChanged
            assertEquals("Data Logging", event3.settingName)
        }
    }

    // endregion

    // region ViewState Transformation Tests

    @Test
    fun `stateProvider transforms all settings correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onNotificationsToggled(false)
        viewModel.onDarkModeToggled(true)
        viewModel.onAdvancedOptionsToggled(true)
        viewModel.onDataLoggingToggled(false)
        advanceUntilIdle()

        val viewState = viewModel.stateFlow.value
        assertFalse(viewState.notificationsEnabled)
        assertTrue(viewState.darkModeEnabled)
        assertTrue(viewState.showAdvancedOptions)
        assertFalse(viewState.dataLoggingEnabled)
    }

    // endregion
}
