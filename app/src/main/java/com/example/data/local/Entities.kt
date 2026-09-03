package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sos_requests")
data class SosRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val peopleCount: Int,
    val riskLevel: String, // LOW, MODERATE, HIGH, EXTREME
    val batteryPercent: Int,
    val emergencyDescription: String,
    val timestamp: Long,
    val deliveryStatus: String, // DELIVERED, SENDING, RETRYING, NO_CONNECTION
    val commChannel: String, // INTERNET, CELLULAR_SMS, LORA_MESH, EMERGENCY_GATEWAY, SATELLITE_BACKHAUL
    val rescueStatus: String, // WAITING, ASSIGNED, EN_ROUTE, REACHED, RESCUED, CLOSED
    val assignedTeamName: String = "",
    val assignedTeamEta: String = "",
    val isLocationSharingActive: Boolean = true,
    val lastLocationUpdate: Long = System.currentTimeMillis(),
    val gpsAccuracyMeters: Float = 12.0f
)

@Entity(tableName = "flood_reports")
data class FloodReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val reportCategory: String, // FLOODED_AREA, WATER_LEVEL, etc.
    val waterLevelMeters: Double,
    val description: String,
    val photoUri: String = "",
    val timestamp: Long,
    val isVerifiedByAuthority: Boolean = false
)

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val capacityTotal: Int,
    val capacityOccupied: Int,
    val riskLevel: String, // LOW, MODERATE, HIGH, EXTREME
    val facilities: String, // "Medical Aid, Food Supply, Clean Water, Power Generator, Elevated Bedding"
    val isRecommended: Boolean = true,
    val contactPhone: String = "+91 98480 22334"
)

@Entity(tableName = "early_warnings")
data class EarlyWarningEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val warningLevel: String, // NORMAL, WATCH, ALERT, EXTREME
    val locationName: String,
    val expectedRainfallMm: Double,
    val expectedPeriod: String,
    val inundationProbability: Int,
    val message: String,
    val recommendedActions: String, // JSON or newline separated actions
    val timestamp: Long,
    val isActive: Boolean = true
)

@Entity(tableName = "map_zones")
data class MapZoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val zoneCode: String, // "ZONE 01", "ZONE 04"
    val name: String,
    val centerLat: Double,
    val centerLon: Double,
    val riskLevel: String,
    val expectedRainfallMm: Double,
    val inundationProbability: Int,
    val forecastPeriod: String,
    val elevationMeters: Double,
    val statusSummary: String
)
