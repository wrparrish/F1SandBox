package com.parrishdev.results

data class ResultViewState(
    val isLoading: Boolean = true,
    val results: List<RaceResult> = emptyList(),
    val events: (ResultEvent) -> Unit = {}
)