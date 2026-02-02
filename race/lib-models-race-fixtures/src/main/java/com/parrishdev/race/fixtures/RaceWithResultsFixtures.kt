package com.parrishdev.race.fixtures

import com.parrishdev.race.model.Race
import com.parrishdev.race.model.RaceResult
import com.parrishdev.race.model.RaceWithResults

/**
 * Factory function for creating [RaceWithResults] test fixtures.
 *
 * Usage in tests:
 * ```
 * val raceWithResults = createRaceWithResults()
 * assertEquals("Lando Norris", raceWithResults.winner?.driver?.fullName)
 * ```
 *
 * Usage in Compose previews:
 * ```
 * @Preview
 * @Composable
 * fun RaceDetailsPreview() {
 *     RaceDetailsScreen(raceWithResults = createRaceWithResults())
 * }
 * ```
 */
fun createRaceWithResults(
    race: Race = createRace(),
    results: List<RaceResult> = createFullRaceResults()
): RaceWithResults = RaceWithResults(
    race = race,
    results = results
)

/**
 * Creates a race with only podium finishers (for testing minimal data scenarios).
 */
fun createRaceWithPodium(): RaceWithResults = RaceWithResults(
    race = createRace(),
    results = createPodiumResults()
)

/**
 * Creates a race with no results (for testing loading/empty states).
 */
fun createRaceWithoutResults(): RaceWithResults = RaceWithResults(
    race = createRace(),
    results = emptyList()
)

/**
 * Creates a list of races with results for testing race list screens.
 */
fun createRaceWithResultsList(): List<RaceWithResults> = createRaceList().map { race ->
    createRaceWithResults(race = race)
}
