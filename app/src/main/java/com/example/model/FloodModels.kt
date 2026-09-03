package com.example.model

enum class FloodRiskLevel(val label: String, val severityRank: Int) {
    LOW("LOW", 1),
    MODERATE("MODERATE", 2),
    HIGH("HIGH", 3),
    EXTREME("EXTREME", 4);

    companion object {
        fun fromString(value: String): FloodRiskLevel {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: LOW
        }
    }
}

enum class WarningLevel(val label: String) {
    NORMAL("NORMAL"),
    WATCH("WATCH"),
    ALERT("ALERT"),
    EXTREME("EXTREME")
}

enum class CommunicationChannel(val displayName: String, val description: String) {
    INTERNET("Internet (4G/5G/Wi-Fi)", "Direct high-speed connection to disaster response cloud"),
    CELLULAR_SMS("Cellular SMS", "Low-bandwidth SMS fallback to emergency gateway"),
    LORA_MESH("Local LoRa / BLE Mesh", "Off-grid device-to-device mesh network (868/915 MHz)"),
    EMERGENCY_GATEWAY("Emergency Gateway", "Simulated ESP32/LoRa bridge relay station"),
    SATELLITE_BACKHAUL("Satellite Backhaul", "Relayed through external emergency gateway satellite link")
}

enum class DeliveryStatus(val label: String) {
    DELIVERED("DELIVERED"),
    SENDING("SENDING"),
    RETRYING("RETRYING"),
    NO_CONNECTION("NO CONNECTION")
}

enum class RescueStatus(val label: String, val stepIndex: Int) {
    WAITING("WAITING", 0),
    ASSIGNED("ASSIGNED", 1),
    EN_ROUTE("EN ROUTE", 2),
    REACHED("REACHED", 3),
    RESCUED("RESCUED", 4),
    CLOSED("CLOSED", 5)
}

enum class ReportCategory(val displayName: String, val iconEmoji: String) {
    FLOODED_AREA("Flooded Area", "🌊"),
    WATER_LEVEL("Water Level", "💧"),
    ROAD_BLOCKED("Road Blocked", "🚧"),
    BRIDGE_DAMAGE("Bridge Damage", "🌉"),
    BUILDING_FLOODED("Building Flooded", "🏠"),
    FALLEN_TREE("Fallen Tree", "🌳"),
    OTHER_HAZARD("Other Hazard", "⚠️")
}

data class WeatherInfo(
    val rainfallMm: Double,
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val condition: String,
    val stationName: String,
    val lastUpdated: String
)

data class RiskAnalysis(
    val currentRainfallMm: Double,
    val predictedRainfallMm: Double,
    val rainfallProbability: Int,
    val inundationProbability: Int,
    val riskLevel: FloodRiskLevel,
    val forecastPeriod: String,
    val riskTrend: String, // "RISING", "STABLE", "FALLING"
    val elevationMeters: Double,
    val nearbyWaterBody: Boolean,
    val drainageCapacity: String,
    val aiExplanation: String
)

data class UserGpsLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val locationName: String,
    val zoneName: String,
    val timestamp: Long = System.currentTimeMillis()
)
