package com.parrishdev.model

object TestFactory {
    fun createResultsItem(): RacesItem {
        return RacesItem(
            raceName = "Test Race",
            round = "1",
            results = listOf(
                ResultsItem(
                    constructor = Constructor(),
                    driver = Driver(),
                    fastestLap = FastestLap(time = Time()),

                    )
            ),
            circuit = Circuit(location = Location())
        )

    }
}