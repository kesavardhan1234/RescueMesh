package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.EarlyWarningEntity
import com.example.data.local.FloodReportEntity
import com.example.data.local.MapZoneEntity
import com.example.data.local.ShelterEntity
import com.example.data.local.SosRequestEntity
import com.example.data.repository.FloodRepository
import com.example.data.service.NetworkStatus
import com.example.data.service.RescueTeam
import com.example.data.service.SafeRouteOption
import com.example.model.CommunicationChannel
import com.example.model.DeliveryStatus
import com.example.model.FloodRiskLevel
import com.example.model.RescueStatus
import com.example.model.RiskAnalysis
import com.example.model.UserGpsLocation
import com.example.model.WeatherInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DemoScenarioState(
    val currentStep: Int = 1,
    val totalSteps: Int = 14,
    val stepTitle: String = "1. Normal Weather Baseline",
    val stepDescription: String = "Clear conditions, low rainfall, normal drainage capacity.",
    val isAutoPlaying: Boolean = false
)

class FloodViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FloodRepository(database)

    // Current State Flows
    val weather: StateFlow<WeatherInfo> = repository.weatherState
    val userLocation: StateFlow<UserGpsLocation> = repository.userLocation
    val riskAnalysis: StateFlow<RiskAnalysis> = repository.currentRiskAnalysis
    val networkStatus: StateFlow<NetworkStatus> = repository.networkStatus
    val appRole: StateFlow<String> = repository.currentAppRole
    val rescueTeams: StateFlow<List<RescueTeam>> = repository.rescueTeams

    // Database Reactive Flows
    val allSosRequests: StateFlow<List<SosRequestEntity>> = repository.allSosRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSos: StateFlow<SosRequestEntity?> = repository.activeSosRequest
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val floodReports: StateFlow<List<FloodReportEntity>> = repository.allFloodReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shelters: StateFlow<List<ShelterEntity>> = repository.allShelters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWarnings: StateFlow<List<EarlyWarningEntity>> = repository.activeWarnings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mapZones: StateFlow<List<MapZoneEntity>> = repository.allMapZones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & Dialog State
    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Map, 2: Alerts, 3: SOS, 4: Profile
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedZoneForDetail = MutableStateFlow<MapZoneEntity?>(null)
    val selectedZoneForDetail: StateFlow<MapZoneEntity?> = _selectedZoneForDetail.asStateFlow()

    private val _selectedShelterForRoute = MutableStateFlow<ShelterEntity?>(null)
    val selectedShelterForRoute: StateFlow<ShelterEntity?> = _selectedShelterForRoute.asStateFlow()

    private val _routeOptions = MutableStateFlow<List<SafeRouteOption>>(emptyList())
    val routeOptions: StateFlow<List<SafeRouteOption>> = _routeOptions.asStateFlow()

    private val _isReportFloodDialogOpen = MutableStateFlow(false)
    val isReportFloodDialogOpen: StateFlow<Boolean> = _isReportFloodDialogOpen.asStateFlow()

    private val _isDemoControlPanelOpen = MutableStateFlow(false)
    val isDemoControlPanelOpen: StateFlow<Boolean> = _isDemoControlPanelOpen.asStateFlow()

    // Demo Scenario State
    private val _demoState = MutableStateFlow(DemoScenarioState())
    val demoState: StateFlow<DemoScenarioState> = _demoState.asStateFlow()
    private var autoPlayJob: Job? = null

    // Multilingual support
    private val _appLanguage = MutableStateFlow(com.example.ui.localization.AppLanguage.ENGLISH)
    val appLanguage: StateFlow<com.example.ui.localization.AppLanguage> = _appLanguage.asStateFlow()

    fun setLanguage(language: com.example.ui.localization.AppLanguage) {
        _appLanguage.value = language
    }

    init {
        _routeOptions.value = repository.getSafeRoutes()
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setAppRole(role: String) {
        repository.setAppRole(role)
    }

    fun setSimulatedOfflineMode(offline: Boolean) {
        repository.commManager.setSimulatedOfflineMode(offline)
    }

    fun selectZoneForDetail(zone: MapZoneEntity?) {
        _selectedZoneForDetail.value = zone
    }

    fun selectShelterForRoute(shelter: ShelterEntity?) {
        _selectedShelterForRoute.value = shelter
        if (shelter != null) {
            _routeOptions.value = repository.getSafeRoutes(shelter.name)
        }
    }

    fun openReportFloodDialog(open: Boolean) {
        _isReportFloodDialogOpen.value = open
    }

    fun toggleDemoControlPanel(open: Boolean? = null) {
        _isDemoControlPanelOpen.value = open ?: !_isDemoControlPanelOpen.value
    }

    // Trigger SOS
    fun triggerSos(peopleCount: Int = 3, description: String = "", batteryPercent: Int = 42) {
        viewModelScope.launch {
            repository.createAndSendSos(peopleCount, description, batteryPercent)
            _selectedTab.value = 3 // Switch to SOS tab
        }
    }

    fun stopSos(sosId: Long) {
        viewModelScope.launch {
            repository.stopSos(sosId)
        }
    }

    // Authority Actions
    fun assignRescueTeam(sosId: Long, teamId: String) {
        viewModelScope.launch {
            val assignedTeam = repository.rescueFleetService.assignTeamToSos(teamId, sosId)
            val teamName = assignedTeam?.name ?: "NDRF Rescue Alpha"
            repository.updateSosRescueStatus(sosId, RescueStatus.ASSIGNED, teamName, "12 mins")
        }
    }

    fun advanceRescueStatus(sosId: Long, nextStatus: RescueStatus) {
        viewModelScope.launch {
            repository.updateSosRescueStatus(sosId, nextStatus)
        }
    }

    fun submitFloodReport(category: String, waterLevel: Double, description: String) {
        viewModelScope.launch {
            repository.submitFloodReport(category, waterLevel, description)
            _isReportFloodDialogOpen.value = false
        }
    }

    fun verifyReport(reportId: Long) {
        viewModelScope.launch {
            repository.verifyFloodReport(reportId, true)
        }
    }

    // --- Hackathon 14-Step Demo Mode Engine ---
    fun goToDemoStep(step: Int) {
        val targetStep = step.coerceIn(1, 14)
        when (targetStep) {
            1 -> {
                // Normal Weather
                repository.updateWeather(
                    WeatherInfo(
                        rainfallMm = 4.0,
                        temperatureCelsius = 29.0,
                        humidityPercent = 65,
                        windSpeedKmh = 12.0,
                        condition = "Partly Cloudy",
                        stationName = "Hydro-Met Station East #04",
                        lastUpdated = "10:00 AM"
                    )
                )
                repository.commManager.setSimulatedOfflineMode(false)
                _demoState.value = DemoScenarioState(
                    currentStep = 1,
                    stepTitle = "1. Baseline / Normal Weather",
                    stepDescription = "Sunny/cloudy weather, rainfall 4mm/h. Flood risk is LOW 🟢 across all sectors."
                )
            }
            2 -> {
                // Rainfall Increases
                repository.updateWeather(
                    WeatherInfo(
                        rainfallMm = 42.0,
                        temperatureCelsius = 26.0,
                        humidityPercent = 88,
                        windSpeedKmh = 28.0,
                        condition = "Moderate Monsoon Rain",
                        stationName = "Hydro-Met Station East #04",
                        lastUpdated = "10:30 AM"
                    )
                )
                _demoState.value = DemoScenarioState(
                    currentStep = 2,
                    stepTitle = "2. Rainfall Increases",
                    stepDescription = "Precipitation rises to 42 mm/h. Drainage channels reaching 50% capacity. Risk shifts to MODERATE 🟡."
                )
            }
            3 -> {
                // AI Predicts High Flood Risk
                repository.updateWeather(
                    WeatherInfo(
                        rainfallMm = 88.0,
                        temperatureCelsius = 23.5,
                        humidityPercent = 96,
                        windSpeedKmh = 45.0,
                        condition = "Severe Downpour & Storm Surge",
                        stationName = "Hydro-Met Station East #04",
                        lastUpdated = "11:00 AM"
                    )
                )
                _demoState.value = DemoScenarioState(
                    currentStep = 3,
                    stepTitle = "3. AI Predicts High Inundation Risk",
                    stepDescription = "AI Risk Engine detects 88mm downpour + low elevation (6.8m). Inundation probability surges to 88% (HIGH 🟠)."
                )
            }
            4 -> {
                // User receives early warning
                _selectedTab.value = 2 // Alerts tab
                _demoState.value = DemoScenarioState(
                    currentStep = 4,
                    stepTitle = "4. Early Warning Alert Broadcast",
                    stepDescription = "Automated push notification & Extreme Warning Banner alert citizens: Avoid low-lying roads, prepare for evacuation."
                )
            }
            5 -> {
                // User opens live flood map
                _selectedTab.value = 1 // Map tab
                _demoState.value = DemoScenarioState(
                    currentStep = 5,
                    stepTitle = "5. Live GIS Flood Map Inspection",
                    stepDescription = "Interactive GIS map highlights Zone 04 in EXTREME RED 🔴 with blocked road markers and safe shelter markers."
                )
            }
            6 -> {
                // User chooses safe shelter & safe route
                _demoState.value = DemoScenarioState(
                    currentStep = 6,
                    stepTitle = "6. Safe Shelter & Routing Guidance",
                    stepDescription = "Flood-aware routing calculates North Ridge Bypass (3.2 km, 🟢 LOW RISK) over Direct Canal Road (2.0 km, 🔴 HIGH RISK)."
                )
            }
            7 -> {
                // User presses SOS button
                _selectedTab.value = 3 // SOS Tab
                triggerSos(peopleCount = 3, description = "Water reaching ground floor balcony. 1 senior citizen present.", batteryPercent = 36)
                _demoState.value = DemoScenarioState(
                    currentStep = 7,
                    stepTitle = "7. SOS Packet Generated",
                    stepDescription = "Citizen holds SOS: Emergency packet created with Lat/Lon (16.496, 80.628), People: 3, Battery: 36%, Risk: EXTREME."
                )
            }
            8 -> {
                // GPS location sharing active
                _demoState.value = DemoScenarioState(
                    currentStep = 8,
                    stepTitle = "8. Live GPS Location Sharing Active",
                    stepDescription = "GPS beacon transmits high-precision coordinates with ±8.5m accuracy. Rescue beacon active."
                )
            }
            9 -> {
                // Cellular drops / Offline emergency mode
                repository.commManager.setSimulatedOfflineMode(true)
                _demoState.value = DemoScenarioState(
                    currentStep = 9,
                    stepTitle = "9. Cellular Network Outage Detected",
                    stepDescription = "Cellular towers submerged: 4G/5G drops ❌. Offline-First mode engages automatically."
                )
            }
            10 -> {
                // Failover switches to LoRa Mesh / Emergency Gateway
                repository.commManager.forceChannel(CommunicationChannel.LORA_MESH)
                _demoState.value = DemoScenarioState(
                    currentStep = 10,
                    stepTitle = "10. Failover to LoRa Mesh & Emergency Gateway",
                    stepDescription = "SOS packet successfully routes via 868MHz LoRa Mesh to the local ESP32 Emergency Gateway ✅."
                )
            }
            11 -> {
                // Rescue Dashboard receives SOS
                repository.setAppRole("RESCUE_TEAM")
                _demoState.value = DemoScenarioState(
                    currentStep = 11,
                    stepTitle = "11. Rescue Command Dashboard Triage",
                    stepDescription = "Authority dashboard receives prioritized SOS #1024 ranked as CRITICAL 🔴 based on inundation and elderly victims."
                )
            }
            12 -> {
                // Rescue Team Assigned
                viewModelScope.launch {
                    val active = activeSos.value
                    if (active != null) {
                        assignRescueTeam(active.id, "RT_01")
                    }
                }
                _demoState.value = DemoScenarioState(
                    currentStep = 12,
                    stepTitle = "12. NDRF Rescue Unit Alpha Dispatched",
                    stepDescription = "Authority dispatches Motorized Zodiac Boat Unit Alpha. Citizen app receives live confirmation."
                )
            }
            13 -> {
                // Safe route generated for responders
                viewModelScope.launch {
                    val active = activeSos.value
                    if (active != null) {
                        advanceRescueStatus(active.id, RescueStatus.EN_ROUTE)
                    }
                }
                _demoState.value = DemoScenarioState(
                    currentStep = 13,
                    stepTitle = "13. Flood-Avoidance Route for Responders",
                    stepDescription = "Navigation system guides rescue boat along safest elevated channel, avoiding debris and submerged culverts."
                )
            }
            14 -> {
                // Rescued & Closed
                viewModelScope.launch {
                    val active = activeSos.value
                    if (active != null) {
                        advanceRescueStatus(active.id, RescueStatus.RESCUED)
                    }
                }
                _demoState.value = DemoScenarioState(
                    currentStep = 14,
                    stepTitle = "14. Victims Successfully Rescued!",
                    stepDescription = "Rescue team reaches citizens and transports them to St. Jude Elevated Shelter. Status updated to RESCUED 🟢."
                )
            }
        }
    }

    fun nextDemoStep() {
        val current = _demoState.value.currentStep
        if (current < 14) {
            goToDemoStep(current + 1)
        }
    }

    fun prevDemoStep() {
        val current = _demoState.value.currentStep
        if (current > 1) {
            goToDemoStep(current - 1)
        }
    }

    fun toggleAutoPlay() {
        if (_demoState.value.isAutoPlaying) {
            autoPlayJob?.cancel()
            _demoState.value = _demoState.value.copy(isAutoPlaying = false)
        } else {
            _demoState.value = _demoState.value.copy(isAutoPlaying = true)
            autoPlayJob = viewModelScope.launch {
                var step = _demoState.value.currentStep
                while (_demoState.value.isAutoPlaying && step <= 14) {
                    goToDemoStep(step)
                    delay(3500)
                    step++
                    if (step > 14) {
                        step = 1
                    }
                }
                _demoState.value = _demoState.value.copy(isAutoPlaying = false)
            }
        }
    }
}
