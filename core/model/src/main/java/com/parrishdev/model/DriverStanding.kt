package com.parrishdev.model

data class DriverStanding(
    val driverId: String,
    val position: String,
    val points: String,
    val wins: String,
    val constructorName: String,
    val driverName: String, // Added for easier display
    val driverCode: String?, // Added for easier display
    val driverNumber: String? // Added for easier display
)
