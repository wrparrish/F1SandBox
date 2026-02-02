package com.parrishdev.race.fixtures

import com.parrishdev.race.model.Circuit
import com.parrishdev.race.model.Location
import com.parrishdev.race.model.Race

/**
 * Factory function for creating [Race] test fixtures.
 *
 * Usage in tests:
 * ```
 * val race = createRace(name = "Monaco Grand Prix", round = 7)
 * ```
 *
 * Usage in Compose previews:
 * ```
 * @Preview
 * @Composable
 * fun RaceCardPreview() {
 *     RaceCard(race = createRace())
 * }
 * ```
 */
fun createRace(
    id: String = "2025_monaco",
    season: Int = 2025,
    round: Int = 7,
    name: String = "Monaco Grand Prix",
    date: String = "2025-05-25",
    time: String = "13:00:00Z",
    circuit: Circuit = createCircuit(),
    url: String = "https://en.wikipedia.org/wiki/2025_Monaco_Grand_Prix"
): Race = Race(
    id = id,
    season = season,
    round = round,
    name = name,
    date = date,
    time = time,
    circuit = circuit,
    url = url
)

/**
 * Factory function for creating [Circuit] test fixtures.
 */
fun createCircuit(
    id: String = "monaco",
    name: String = "Circuit de Monaco",
    location: Location = createLocation(),
    url: String = "https://en.wikipedia.org/wiki/Circuit_de_Monaco"
): Circuit = Circuit(
    id = id,
    name = name,
    location = location,
    url = url
)

/**
 * Factory function for creating [Location] test fixtures.
 */
fun createLocation(
    locality: String = "Monte Carlo",
    country: String = "Monaco",
    latitude: String = "43.7347",
    longitude: String = "7.4206"
): Location = Location(
    locality = locality,
    country = country,
    latitude = latitude,
    longitude = longitude
)

/**
 * Creates a collection of realistic race fixtures for testing lists.
 * Includes races from the 2025 season.
 */
fun createRaceList(season: Int = 2025): List<Race> = listOf(
    createRace(
        id = "${season}_bahrain",
        season = season,
        round = 1,
        name = "Bahrain Grand Prix",
        date = "$season-03-02",
        circuit = createCircuit(
            id = "bahrain",
            name = "Bahrain International Circuit",
            location = createLocation(
                locality = "Sakhir",
                country = "Bahrain",
                latitude = "26.0325",
                longitude = "50.5106"
            )
        )
    ),
    createRace(
        id = "${season}_saudi_arabia",
        season = season,
        round = 2,
        name = "Saudi Arabian Grand Prix",
        date = "$season-03-09",
        circuit = createCircuit(
            id = "jeddah",
            name = "Jeddah Corniche Circuit",
            location = createLocation(
                locality = "Jeddah",
                country = "Saudi Arabia",
                latitude = "21.6319",
                longitude = "39.1044"
            )
        )
    ),
    createRace(
        id = "${season}_australia",
        season = season,
        round = 3,
        name = "Australian Grand Prix",
        date = "$season-03-24",
        circuit = createCircuit(
            id = "albert_park",
            name = "Albert Park Circuit",
            location = createLocation(
                locality = "Melbourne",
                country = "Australia",
                latitude = "-37.8497",
                longitude = "144.968"
            )
        )
    ),
    createRace(
        id = "${season}_japan",
        season = season,
        round = 4,
        name = "Japanese Grand Prix",
        date = "$season-04-07",
        circuit = createCircuit(
            id = "suzuka",
            name = "Suzuka Circuit",
            location = createLocation(
                locality = "Suzuka",
                country = "Japan",
                latitude = "34.8431",
                longitude = "136.5407"
            )
        )
    ),
    createRace(
        id = "${season}_monaco",
        season = season,
        round = 7,
        name = "Monaco Grand Prix",
        date = "$season-05-25",
        circuit = createCircuit()
    )
)
