package com.parrishdev.driver.fixtures

import com.parrishdev.driver.model.Driver

/**
 * Factory function for creating [Driver] test fixtures.
 *
 * Usage in tests:
 * ```
 * val driver = createDriver(driverNumber = 4, lastName = "Norris")
 * ```
 *
 * Usage in Compose previews:
 * ```
 * @Preview
 * @Composable
 * fun DriverCardPreview() {
 *     DriverCard(driver = createDriver())
 * }
 * ```
 */
fun createDriver(
    driverNumber: Int = 4,
    firstName: String = "Lando",
    lastName: String = "Norris",
    fullName: String = "$firstName $lastName",
    nameAcronym: String = "NOR",
    countryCode: String = "GBR",
    teamName: String = "McLaren",
    teamColour: String = "F47600",
    headshotUrl: String? = "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/L/LANNOR01_Lando_Norris/lannor01.png",
    broadcastName: String = "L NORRIS",
    sessionKey: Int = 9839,
    championshipPosition: Int = 1,
    championshipPoints: Float = 423f,
    wins: Int = 7
): Driver = Driver(
    driverNumber = driverNumber,
    firstName = firstName,
    lastName = lastName,
    fullName = fullName,
    nameAcronym = nameAcronym,
    countryCode = countryCode,
    teamName = teamName,
    teamColour = teamColour,
    headshotUrl = headshotUrl,
    broadcastName = broadcastName,
    sessionKey = sessionKey,
    championshipPosition = championshipPosition,
    championshipPoints = championshipPoints,
    wins = wins
)

/**
 * Creates a complete championship standings fixture (2025 season top 10).
 */
fun createDriverStandings(): List<Driver> = listOf(
    createDriver(
        driverNumber = 4,
        firstName = "Lando",
        lastName = "Norris",
        nameAcronym = "NOR",
        teamName = "McLaren",
        teamColour = "F47600",
        championshipPosition = 1,
        championshipPoints = 423f,
        wins = 7
    ),
    createDriver(
        driverNumber = 1,
        firstName = "Max",
        lastName = "Verstappen",
        nameAcronym = "VER",
        countryCode = "NLD",
        teamName = "Red Bull Racing",
        teamColour = "4781D7",
        broadcastName = "M VERSTAPPEN",
        championshipPosition = 2,
        championshipPoints = 421f,
        wins = 8
    ),
    createDriver(
        driverNumber = 81,
        firstName = "Oscar",
        lastName = "Piastri",
        nameAcronym = "PIA",
        countryCode = "AUS",
        teamName = "McLaren",
        teamColour = "F47600",
        broadcastName = "O PIASTRI",
        championshipPosition = 3,
        championshipPoints = 410f,
        wins = 7
    ),
    createDriver(
        driverNumber = 63,
        firstName = "George",
        lastName = "Russell",
        nameAcronym = "RUS",
        teamName = "Mercedes",
        teamColour = "00D7B6",
        broadcastName = "G RUSSELL",
        championshipPosition = 4,
        championshipPoints = 319f,
        wins = 2
    ),
    createDriver(
        driverNumber = 16,
        firstName = "Charles",
        lastName = "Leclerc",
        nameAcronym = "LEC",
        countryCode = "MCO",
        teamName = "Ferrari",
        teamColour = "ED1131",
        broadcastName = "C LECLERC",
        championshipPosition = 5,
        championshipPoints = 242f,
        wins = 0
    ),
    createDriver(
        driverNumber = 44,
        firstName = "Lewis",
        lastName = "Hamilton",
        nameAcronym = "HAM",
        teamName = "Ferrari",
        teamColour = "ED1131",
        broadcastName = "L HAMILTON",
        championshipPosition = 6,
        championshipPoints = 156f,
        wins = 0
    ),
    createDriver(
        driverNumber = 12,
        firstName = "Kimi",
        lastName = "Antonelli",
        nameAcronym = "ANT",
        countryCode = "ITA",
        teamName = "Mercedes",
        teamColour = "00D7B6",
        broadcastName = "K ANTONELLI",
        championshipPosition = 7,
        championshipPoints = 150f,
        wins = 0
    ),
    createDriver(
        driverNumber = 23,
        firstName = "Alexander",
        lastName = "Albon",
        nameAcronym = "ALB",
        countryCode = "THA",
        teamName = "Williams",
        teamColour = "1868DB",
        broadcastName = "A ALBON",
        championshipPosition = 8,
        championshipPoints = 73f,
        wins = 0
    ),
    createDriver(
        driverNumber = 55,
        firstName = "Carlos",
        lastName = "Sainz",
        nameAcronym = "SAI",
        countryCode = "ESP",
        teamName = "Williams",
        teamColour = "1868DB",
        broadcastName = "C SAINZ",
        championshipPosition = 9,
        championshipPoints = 64f,
        wins = 0
    ),
    createDriver(
        driverNumber = 14,
        firstName = "Fernando",
        lastName = "Alonso",
        nameAcronym = "ALO",
        countryCode = "ESP",
        teamName = "Aston Martin",
        teamColour = "229971",
        broadcastName = "F ALONSO",
        championshipPosition = 10,
        championshipPoints = 56f,
        wins = 0
    )
)

/**
 * Creates a driver without championship standings data (for testing loading states).
 */
fun createDriverWithoutStandings(): Driver = createDriver(
    championshipPosition = 0,
    championshipPoints = 0f,
    wins = 0
)

/**
 * Creates a driver without a headshot URL (for testing fallback UI).
 */
fun createDriverWithoutHeadshot(): Driver = createDriver(
    headshotUrl = null
)

/**
 * Creates a minimal list of drivers for quick testing.
 */
fun createDriverList(): List<Driver> = listOf(
    createDriver(),
    createDriver(
        driverNumber = 1,
        firstName = "Max",
        lastName = "Verstappen",
        nameAcronym = "VER",
        teamName = "Red Bull Racing",
        championshipPosition = 2,
        championshipPoints = 421f
    ),
    createDriver(
        driverNumber = 81,
        firstName = "Oscar",
        lastName = "Piastri",
        nameAcronym = "PIA",
        teamName = "McLaren",
        championshipPosition = 3,
        championshipPoints = 410f
    )
)
