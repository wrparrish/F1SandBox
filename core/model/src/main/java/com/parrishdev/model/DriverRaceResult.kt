package com.parrishdev.model

data class DriverRaceResult(
    val season: String,
    val round: String,
    val raceName: String,
    val date: String,
    val constructorName: String,
    val grid: String,
    val position: String,
    val points: String,
    val status: String,
    val driverId: String, // For context if needed
    val circuitName: String // Added for more detail
)
