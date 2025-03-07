package com.parrishdev.drivers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test

import com.parrishdev.data.F1DriversApi
import com.parrishdev.model.Driver
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DriversViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: DriversViewModel
    private val mockF1DriversApi: F1DriversApi = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchDrivers() should update uiState with drivers on success`() = runTest {
        // Arrange
        val drivers = listOf(Driver(driverNumber = 1, fullName = "Max Verstappen"))
        coEvery { mockF1DriversApi.fetchDrivers() } returns Result.success(drivers)

        // Act
        viewModel = DriversViewModel(mockF1DriversApi) // fetch happens in .init

        // Assert
        assertEquals(drivers, viewModel.uiState.value.drivers)
    }

    @Test
    fun `fetchDrivers() should update uiState with error message on failure`() = runTest {
        // Arrange
        val errorMessage = "Error fetching drivers"
        coEvery { mockF1DriversApi.fetchDrivers() } returns Result.failure(Throwable(errorMessage))


        // Act
        viewModel = DriversViewModel(mockF1DriversApi)

        // Assert
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `fetchDrivers() should update uiState with error message on exception`() = runTest {
        // Arrange
        val errorMessage = "Error fetching drivers"
        coEvery { mockF1DriversApi.fetchDrivers() } throws Exception(errorMessage)

        // Act
        viewModel = DriversViewModel(mockF1DriversApi)

        // Assert
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
    }


    @Test
    fun `onDriverSelected() should emit NavigateToDetails event`() = runTest {
        // Arrange
        val driver = Driver(driverNumber = 1, fullName = "Max Verstappen")
        coEvery { mockF1DriversApi.fetchDrivers() } returns Result.success(listOf(driver))
        viewModel = DriversViewModel(mockF1DriversApi)


        // Act
        viewModel.navigationEvents.test {
            viewModel.onDriverSelected(driver)

            // Assert
            val expectedEvent = NavigationEvent.NavigateToDetails(driver.driverNumber.toString())
            assertEquals(expectedEvent, awaitItem())
        }
    }
}
