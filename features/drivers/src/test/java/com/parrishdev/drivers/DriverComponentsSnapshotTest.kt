package com.parrishdev.drivers

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import com.parrishdev.model.Driver
import com.parrishdev.model.DriverRaceResult
import com.parrishdev.ui.F1SandboxTheme // Assuming this is the correct theme
import io.github.takahirom.roborazzi.RoborazziRule
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DriverComponentsSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Basic RoborazziRule setup.
    @get:Rule
    val roborazziRule = RoborazziRule(
        composeRule = composeTestRule,
        captureRoot = composeTestRule.onRoot(), // Capture the entire content set by setContent
        options = RoborazziRule.Options(
            outputDirectoryPath = "src/test/snapshots/images", // Standard output dir
            captureType = RoborazziRule.CaptureType.LastImage() // Capture the last image after setContent
        )
    )

    @Test
    @RoborazziTest
    fun testDriverCardSnapshot() {
        val driver = Driver(
            driverNumber = 1,
            fullName = "Max Verstappen",
            teamName = "Red Bull Racing",
            headshotUrl = "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/M/MAXVER01_Max_Verstappen/maxver01.png.transform/1col/image.png", // Example URL
            points = "300",
            position = "1",
            nameAcronym = "VER",
            teamColour = "0600EF" // Example color, ensure your DriverCard handles this
        )
        composeTestRule.setContent {
            F1SandboxTheme {
                DriverCard(driver = driver)
            }
        }
        // RoborazziRule with captureRoot = composeTestRule.onRoot() will capture this.
        // No explicit captureRoboImage call is needed here for this setup.
    }

    @Test
    @RoborazziTest
    fun testRaceResultCardSnapshot() {
        val raceResult = DriverRaceResult(
            season = "2023",
            round = "10",
            raceName = "Austrian Grand Prix",
            date = "2023-07-02",
            constructorName = "Red Bull Racing",
            grid = "1",
            position = "1",
            points = "26",
            status = "Finished",
            driverId = "max_verstappen", // Corresponds to nameAcronym in Driver model
            circuitName = "Red Bull Ring"
        )
        composeTestRule.setContent {
            F1SandboxTheme {
                // Assuming the Composable is RaceResultCard(result = ...)
                // The subtask example has RaceResultCard(driverRaceResult = ...) which might be a typo
                // My actual RaceResultCard composable uses `result` as parameter name.
                RaceResultCard(result = raceResult)
            }
        }
        // RoborazziRule with captureRoot = composeTestRule.onRoot() will capture this.
    }
}
