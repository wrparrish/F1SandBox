package com.parrishdev.race.fixtures

import com.parrishdev.race.model.AverageSpeed
import com.parrishdev.race.model.Constructor
import com.parrishdev.race.model.FastestLap
import com.parrishdev.race.model.RaceDriver
import com.parrishdev.race.model.RaceResult

/**
 * Factory function for creating [RaceResult] test fixtures.
 */
fun createRaceResult(
    position: Int = 1,
    positionText: String = position.toString(),
    points: Float = when (position) {
        1 -> 25f
        2 -> 18f
        3 -> 15f
        4 -> 12f
        5 -> 10f
        6 -> 8f
        7 -> 6f
        8 -> 4f
        9 -> 2f
        10 -> 1f
        else -> 0f
    },
    driver: RaceDriver = createRaceDriver(),
    constructor: Constructor = createConstructor(),
    grid: Int = position,
    laps: Int = 78,
    status: String = "Finished",
    fastestLap: FastestLap? = if (position == 1) createFastestLap() else null
): RaceResult = RaceResult(
    position = position,
    positionText = positionText,
    points = points,
    driver = driver,
    constructor = constructor,
    grid = grid,
    laps = laps,
    status = status,
    fastestLap = fastestLap
)

/**
 * Factory function for creating [RaceDriver] test fixtures.
 */
fun createRaceDriver(
    id: String = "norris",
    permanentNumber: String = "4",
    code: String = "NOR",
    givenName: String = "Lando",
    familyName: String = "Norris",
    nationality: String = "British",
    url: String = "https://en.wikipedia.org/wiki/Lando_Norris"
): RaceDriver = RaceDriver(
    id = id,
    permanentNumber = permanentNumber,
    code = code,
    givenName = givenName,
    familyName = familyName,
    nationality = nationality,
    url = url
)

/**
 * Factory function for creating [Constructor] test fixtures.
 */
fun createConstructor(
    id: String = "mclaren",
    name: String = "McLaren",
    nationality: String = "British",
    url: String = "https://en.wikipedia.org/wiki/McLaren"
): Constructor = Constructor(
    id = id,
    name = name,
    nationality = nationality,
    url = url
)

/**
 * Factory function for creating [FastestLap] test fixtures.
 */
fun createFastestLap(
    rank: Int = 1,
    lap: Int = 42,
    time: String = "1:12.345",
    averageSpeed: AverageSpeed? = createAverageSpeed()
): FastestLap = FastestLap(
    rank = rank,
    lap = lap,
    time = time,
    averageSpeed = averageSpeed
)

/**
 * Factory function for creating [AverageSpeed] test fixtures.
 */
fun createAverageSpeed(
    units: String = "kph",
    speed: String = "198.567"
): AverageSpeed = AverageSpeed(
    units = units,
    speed = speed
)

/**
 * Creates a realistic podium (P1, P2, P3) for testing.
 * Based on 2025 standings.
 */
fun createPodiumResults(): List<RaceResult> = listOf(
    createRaceResult(
        position = 1,
        driver = createRaceDriver(
            id = "norris",
            code = "NOR",
            givenName = "Lando",
            familyName = "Norris",
            permanentNumber = "4"
        ),
        constructor = createConstructor(
            id = "mclaren",
            name = "McLaren"
        ),
        grid = 1,
        fastestLap = createFastestLap(rank = 1)
    ),
    createRaceResult(
        position = 2,
        driver = createRaceDriver(
            id = "verstappen",
            code = "VER",
            givenName = "Max",
            familyName = "Verstappen",
            permanentNumber = "1",
            nationality = "Dutch"
        ),
        constructor = createConstructor(
            id = "red_bull",
            name = "Red Bull Racing",
            nationality = "Austrian"
        ),
        grid = 2,
        fastestLap = null
    ),
    createRaceResult(
        position = 3,
        driver = createRaceDriver(
            id = "piastri",
            code = "PIA",
            givenName = "Oscar",
            familyName = "Piastri",
            permanentNumber = "81",
            nationality = "Australian"
        ),
        constructor = createConstructor(
            id = "mclaren",
            name = "McLaren"
        ),
        grid = 3,
        fastestLap = null
    )
)

/**
 * Creates a full race results grid (top 10) for testing.
 */
fun createFullRaceResults(): List<RaceResult> = listOf(
    // P1-P3 (Podium)
    createRaceResult(
        position = 1,
        driver = createRaceDriver(id = "norris", code = "NOR", givenName = "Lando", familyName = "Norris"),
        constructor = createConstructor(id = "mclaren", name = "McLaren")
    ),
    createRaceResult(
        position = 2,
        driver = createRaceDriver(id = "verstappen", code = "VER", givenName = "Max", familyName = "Verstappen", permanentNumber = "1", nationality = "Dutch"),
        constructor = createConstructor(id = "red_bull", name = "Red Bull Racing", nationality = "Austrian")
    ),
    createRaceResult(
        position = 3,
        driver = createRaceDriver(id = "piastri", code = "PIA", givenName = "Oscar", familyName = "Piastri", permanentNumber = "81", nationality = "Australian"),
        constructor = createConstructor(id = "mclaren", name = "McLaren")
    ),
    // P4-P10 (Points)
    createRaceResult(
        position = 4,
        driver = createRaceDriver(id = "russell", code = "RUS", givenName = "George", familyName = "Russell", permanentNumber = "63"),
        constructor = createConstructor(id = "mercedes", name = "Mercedes", nationality = "German")
    ),
    createRaceResult(
        position = 5,
        driver = createRaceDriver(id = "leclerc", code = "LEC", givenName = "Charles", familyName = "Leclerc", permanentNumber = "16", nationality = "Monegasque"),
        constructor = createConstructor(id = "ferrari", name = "Ferrari", nationality = "Italian")
    ),
    createRaceResult(
        position = 6,
        driver = createRaceDriver(id = "hamilton", code = "HAM", givenName = "Lewis", familyName = "Hamilton", permanentNumber = "44"),
        constructor = createConstructor(id = "ferrari", name = "Ferrari", nationality = "Italian")
    ),
    createRaceResult(
        position = 7,
        driver = createRaceDriver(id = "antonelli", code = "ANT", givenName = "Kimi", familyName = "Antonelli", permanentNumber = "12", nationality = "Italian"),
        constructor = createConstructor(id = "mercedes", name = "Mercedes", nationality = "German")
    ),
    createRaceResult(
        position = 8,
        driver = createRaceDriver(id = "albon", code = "ALB", givenName = "Alexander", familyName = "Albon", permanentNumber = "23", nationality = "Thai"),
        constructor = createConstructor(id = "williams", name = "Williams")
    ),
    createRaceResult(
        position = 9,
        driver = createRaceDriver(id = "sainz", code = "SAI", givenName = "Carlos", familyName = "Sainz", permanentNumber = "55", nationality = "Spanish"),
        constructor = createConstructor(id = "williams", name = "Williams")
    ),
    createRaceResult(
        position = 10,
        driver = createRaceDriver(id = "alonso", code = "ALO", givenName = "Fernando", familyName = "Alonso", permanentNumber = "14", nationality = "Spanish"),
        constructor = createConstructor(id = "aston_martin", name = "Aston Martin")
    )
)
