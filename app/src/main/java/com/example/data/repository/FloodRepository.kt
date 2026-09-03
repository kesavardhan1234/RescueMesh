package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.EarlyWarningEntity
import com.example.data.local.FloodReportEntity
import com.example.data.local.MapZoneEntity
import com.example.data.local.ShelterEntity
import com.example.data.local.SosRequestEntity
import com.example.data.service.CommunicationManager
import com.example.data.service.FloodRiskEngine
import com.example.data.service.NetworkStatus
import com.example.data.service.RescueFleetService
import com.example.data.service.RescueTeam
import com.example.data.service.SafeRouteOption
import com.example.data.service.SafeRouteService
import com.example.model.CommunicationChannel
import com.example.model.DeliveryStatus
import com.example.model.FloodRiskLevel
import com.example.model.RescueStatus
import com.example.model.RiskAnalysis
import com.example.model.UserGpsLocation
import com.example.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloodRepository(
    private val database: AppDatabase,
    val commManager: CommunicationManager = CommunicationManager(),
    val riskEngine: FloodRiskEngine = FloodRiskEngine(),
    val routeService: SafeRouteService = SafeRouteService(),
    val rescueFleetService: RescueFleetService = RescueFleetService()
) {
    // Current Weather State
    private val _weatherState = MutableStateFlow(
        WeatherInfo(
            rainfallMm = 72.0,
            temperatureCelsius = 24.5,
            humidityPercent = 94,
            windSpeedKmh = 38.0,
            condition = "Heavy Monsoon Downpour",
            stationName = "Hydro-Met Station East #04",
            lastUpdated = "Just now"
        )
    )
    val weatherState: StateFlow<WeatherInfo> = _weatherState.asStateFlow()

    // Current User Location State
    private val _userLocation = MutableStateFlow(
        UserGpsLocation(
            latitude = 16.4962,
            longitude = 80.6281,
            accuracyMeters = 8.5f,
            locationName = "Riverbank Ward 12, Krishna Basin",
            zoneName = "ZONE 04"
        )
    )
    val userLocation: StateFlow<UserGpsLocation> = _userLocation.asStateFlow()

    // Live AI Risk Prediction
    private val _currentRiskAnalysis = MutableStateFlow(
        RiskAnalysis(
            currentRainfallMm = 72.0,
            predictedRainfallMm = 98.0,
            rainfallProbability = 88,
            inundationProbability = 78,
            riskLevel = FloodRiskLevel.HIGH,
            forecastPeriod = "Next 2–3 Hours",
            riskTrend = "RISING",
            elevationMeters = 6.8,
            nearbyWaterBody = true,
            drainageCapacity = "Critical (Backflow from Canal)",
            aiExplanation = "Critical runoff volume exceeds drainage limits at elevation 6.8m. High inundation probability (78%) in the next 2-3 hours."
        )
    )
    val currentRiskAnalysis: StateFlow<RiskAnalysis> = _currentRiskAnalysis.asStateFlow()

    // Active App Role: CITIZEN vs RESCUE_DASHBOARD vs DEMO_SIMULATION
    private val _currentAppRole = MutableStateFlow("CITIZEN") // "CITIZEN", "RESCUE_TEAM", "DEMO_PANEL"
    val currentAppRole: StateFlow<String> = _currentAppRole.asStateFlow()

    // Room Database Flows
    val allSosRequests: Flow<List<SosRequestEntity>> = database.sosDao().getAllSosRequests()
    val activeSosRequest: Flow<SosRequestEntity?> = database.sosDao().getActiveSosRequest()
    val allFloodReports: Flow<List<FloodReportEntity>> = database.floodReportDao().getAllFloodReports()
    val allShelters: Flow<List<ShelterEntity>> = database.shelterDao().getAllShelters()
    val activeWarnings: Flow<List<EarlyWarningEntity>> = database.earlyWarningDao().getActiveWarnings()
    val allMapZones: Flow<List<MapZoneEntity>> = database.mapZoneDao().getAllMapZones()

    // Communication Status Flow
    val networkStatus: StateFlow<NetworkStatus> = commManager.networkStatus
    val rescueTeams: StateFlow<List<RescueTeam>> = rescueFleetService.teams

    fun setAppRole(role: String) {
        _currentAppRole.value = role
    }

    fun updateWeather(weather: WeatherInfo) {
        _weatherState.value = weather
        recalculateRisk()
    }

    fun updateUserLocation(lat: Double, lon: Double, name: String, zone: String) {
        _userLocation.value = _userLocation.value.copy(
            latitude = lat,
            longitude = lon,
            locationName = name,
            zoneName = zone,
            timestamp = System.currentTimeMillis()
        )
        recalculateRisk()
    }

    fun recalculateRisk() {
        val weather = _weatherState.value
        val loc = _userLocation.value
        val isExtremeZone = loc.zoneName.contains("04", ignoreCase = true) || weather.rainfallMm > 80.0
        val elev = if (isExtremeZone) 6.8 else 26.0

        val rainfall = weather.rainfallMm
        val pred = (rainfall * 1.3).coerceAtLeast(rainfall)
        val inundation = when {
            rainfall >= 90.0 || (isExtremeZone && rainfall >= 65.0) -> 92
            rainfall >= 60.0 -> 78
            rainfall >= 30.0 -> 45
            else -> 12
        }

        val riskLevel = when {
            inundation >= 85 -> FloodRiskLevel.EXTREME
            inundation >= 65 -> FloodRiskLevel.HIGH
            inundation >= 35 -> FloodRiskLevel.MODERATE
            else -> FloodRiskLevel.LOW
        }

        _currentRiskAnalysis.value = RiskAnalysis(
            currentRainfallMm = rainfall,
            predictedRainfallMm = pred,
            rainfallProbability = (inundation + 8).coerceAtMost(99),
            inundationProbability = inundation,
            riskLevel = riskLevel,
            forecastPeriod = if (riskLevel == FloodRiskLevel.EXTREME) "Next 1–2 Hours" else "Next 3 Hours",
            riskTrend = if (rainfall > 35) "RISING" else "STABLE",
            elevationMeters = elev,
            nearbyWaterBody = isExtremeZone,
            drainageCapacity = if (elev < 10.0) "Critical Backflow" else "Adequate Drainage",
            aiExplanation = if (riskLevel == FloodRiskLevel.EXTREME || riskLevel == FloodRiskLevel.HIGH) {
                "AI Hydrological Assessment: Heavy precipitation ($rainfall mm) combined with saturated low-lying canal basin creates severe flash flood conditions."
            } else {
                "AI Hydrological Assessment: Safe runoff parameters maintained for present zone."
            }
        )
    }

    suspend fun createAndSendSos(
        peopleCount: Int,
        description: String,
        batteryPercent: Int = 42
    ): Long {
        val loc = _userLocation.value
        val risk = _currentRiskAnalysis.value.riskLevel.name

        // Construct initial pending SOS
        val entity = SosRequestEntity(
            userId = "citizen_current",
            userName = "Citizen (You)",
            latitude = loc.latitude,
            longitude = loc.longitude,
            peopleCount = peopleCount,
            riskLevel = risk,
            batteryPercent = batteryPercent,
            emergencyDescription = description.ifBlank { "Immediate flood rescue needed. Water rising rapidly." },
            timestamp = System.currentTimeMillis(),
            deliveryStatus = DeliveryStatus.SENDING.name,
            commChannel = commManager.networkStatus.value.activeChannel.name,
            rescueStatus = RescueStatus.WAITING.name,
            assignedTeamName = "",
            assignedTeamEta = "",
            isLocationSharingActive = true,
            gpsAccuracyMeters = loc.accuracyMeters
        )

        val id = database.sosDao().insertSosRequest(entity)

        // Transmit through Communication Manager priority channels
        val packetStr = "SOS_ID:$id|LAT:${loc.latitude}|LON:${loc.longitude}|PEOPLE:$peopleCount|RISK:$risk|BAT:$batteryPercent"
        commManager.transmitEmergencyPacket(packetStr) { status, channel ->
            database.sosDao().updateDeliveryStatus(id, status.name, channel.name)
        }

        return id
    }

    suspend fun stopSos(id: Long) {
        database.sosDao().updateLocationSharing(
            id = id,
            isActive = false,
            lat = _userLocation.value.latitude,
            lon = _userLocation.value.longitude,
            accuracy = _userLocation.value.accuracyMeters,
            timestamp = System.currentTimeMillis()
        )
        database.sosDao().updateRescueStatus(id, RescueStatus.CLOSED.name)
    }

    suspend fun updateSosRescueStatus(sosId: Long, status: RescueStatus, teamName: String = "", eta: String = "") {
        database.sosDao().updateRescueStatus(sosId, status.name)
    }

    suspend fun submitFloodReport(
        category: String,
        waterLevelM: Double,
        description: String
    ): Long {
        val loc = _userLocation.value
        val report = FloodReportEntity(
            userId = "user_me",
            userName = "Resident Reporter",
            latitude = loc.latitude,
            longitude = loc.longitude,
            reportCategory = category,
            waterLevelMeters = waterLevelM,
            description = description,
            timestamp = System.currentTimeMillis(),
            isVerifiedByAuthority = false
        )
        val id = database.floodReportDao().insertFloodReport(report)
        recalculateRisk()
        return id
    }

    suspend fun verifyFloodReport(reportId: Long, isVerified: Boolean) {
        database.floodReportDao().verifyReport(reportId, isVerified)
    }

    fun getSafeRoutes(destShelterName: String = "St. Jude Elevated Community Center"): List<SafeRouteOption> {
        val loc = _userLocation.value
        return routeService.calculateRoutes(
            originLat = loc.latitude,
            originLon = loc.longitude,
            destinationShelterName = destShelterName
        )
    }
}
