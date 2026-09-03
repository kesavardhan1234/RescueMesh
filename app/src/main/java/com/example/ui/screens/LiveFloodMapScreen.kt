package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MapZoneEntity
import com.example.data.local.ShelterEntity
import com.example.model.UserGpsLocation
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskModerateYellow
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCriticalRed
import com.example.ui.theme.SleekCriticalRedBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.math.roundToInt

enum class MapBasemapMode {
    SATELLITE_HYBRID,
    TOPOGRAPHIC_DEM,
    STREET_VECTOR
}

enum class CityMapRegion(
    val id: String,
    val displayName: String,
    val categoryLabel: String,
    val subtitle: String,
    val emoji: String
) {
    SMALL_CITY_TOWN(
        "small_city",
        "Amaravathi Town & Rural",
        "Small City / Town",
        "Canal bunds, Market bazaar, Temple hillock shelter, Primary Health Centre & Paddy plains",
        "🏘️"
    ),
    VIJAYAWADA_METRO(
        "metro",
        "Vijayawada Metro Delta",
        "Metropolitan City",
        "High-density urban wards, Prakasam Barrage, NH-16 highway flyover & Island Wards",
        "🏙️"
    ),
    DIVISEEMA_ISLAND(
        "diviseema",
        "Diviseema Delta Island",
        "River Island Settlement",
        "Bifurcated Krishna river estuary, Nagayalanka ferry ghat, cyclone tower & mangrove belt",
        "🏝️"
    ),
    TIRUPATI_FOOTHILLS(
        "tirupati",
        "Tirupati Foothills Town",
        "Temple Hill Basin",
        "Mountain flash-flood streams, Kapila Theertham cascade, reservoir weirs & pilgrim shelters",
        "⛰️"
    ),
    KANDALERU_INDUSTRIAL(
        "kandaleru",
        "Kandaleru Industrial Hub",
        "Industrial Township",
        "Thermal power & chemical park, factory colonies, hazmat containment dykes & sirens",
        "🏭"
    ),
    POLAVARAM_VALLEY(
        "valley",
        "Polavaram River Gorge",
        "Valley / Hillside Town",
        "Narrow gorge, Upstream reservoir dam, Ghat road evacuation & Highland tribal camps",
        "🏞️"
    ),
    RAMPACHODAVARAM_FOREST(
        "rampa_forest",
        "Rampachodavaram Forest Hamlets",
        "Forest Tribal Basin",
        "Hill stream flash floods, wooden suspension footbridges, solar relay towers & 4x4 rescue",
        "🌲"
    ),
    MACHILIPATNAM_COASTAL(
        "coastal",
        "Machilipatnam Port Town",
        "Coastal Municipality",
        "Tidal estuary, Sea storm surge, Multi-purpose cyclone shelter & Fishing harbor",
        "🌊"
    )
}

data class RealisticMapMarker(
    val id: String,
    val title: String,
    val category: String, // USER, SHELTER, HOSPITAL, BARRAGE, RESCUE_BOAT, DRONE, ROADBLOCK, GAUGE
    val emoji: String,
    val relX: Float, // 0.0 to 1.0 (relative coordinate space)
    val relY: Float,
    val elevationMeters: Double,
    val waterDepthMeters: Double,
    val details: String,
    val telemetry: String = ""
)

@Composable
fun LiveFloodMapScreen(
    userLocation: UserGpsLocation,
    mapZones: List<MapZoneEntity>,
    shelters: List<ShelterEntity>,
    selectedZone: MapZoneEntity?,
    onSelectZone: (MapZoneEntity?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Selected City Map Region (Defaults to Small City / Town)
    var selectedCityRegion by remember { mutableStateOf(CityMapRegion.SMALL_CITY_TOWN) }

    // Map Camera & Transformation State (Pan & Zoom)
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Basemap Mode
    var basemapMode by remember { mutableStateOf(MapBasemapMode.SATELLITE_HYBRID) }

    // Time-Lapse Flood Surge Simulation Slider (0 = Baseline Now, 1 = +1h (+0.5m), 2 = +3h (+1.2m), 3 = +6h (+2.2m Peak))
    var surgeTimeStep by remember { mutableFloatStateOf(1.0f) }
    var isSurgeAutoPlaying by remember { mutableStateOf(false) }

    // Layer Toggles
    var showRiskHeatmap by remember { mutableStateOf(true) }
    var showContours by remember { mutableStateOf(true) }
    var showRoads by remember { mutableStateOf(true) }
    var showEvacCorridor by remember { mutableStateOf(true) }
    var showShelters by remember { mutableStateOf(true) }
    var showHospitals by remember { mutableStateOf(true) }
    var showGauges by remember { mutableStateOf(true) }
    var showRescueFleet by remember { mutableStateOf(true) }
    var showBlockedRoads by remember { mutableStateOf(true) }
    var showLegendDialog by remember { mutableStateOf(false) }
    var isRulerModeActive by remember { mutableStateOf(false) }

    // Marker Inspection
    var selectedMarker by remember { mutableStateOf<RealisticMapMarker?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Animations for water flow, boat patrol, and pulse beacons
    val infiniteTransition = rememberInfiniteTransition(label = "map_animations")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_phase"
    )

    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    val boatMovePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "boat_phase"
    )

    // Auto-play time-lapse simulation
    LaunchedEffect(isSurgeAutoPlaying) {
        while (isSurgeAutoPlaying) {
            delay(1300)
            surgeTimeStep = (surgeTimeStep + 1.0f) % 4.0f
        }
    }

    // Dynamic water rise multiplier based on surgeTimeStep (0.0 to 3.0)
    val waterLevelSurgeMeters = when {
        surgeTimeStep <= 0.5f -> 0.0f
        surgeTimeStep <= 1.5f -> 0.5f + (surgeTimeStep - 1.0f) * 0.4f
        surgeTimeStep <= 2.5f -> 1.2f + (surgeTimeStep - 2.0f) * 0.6f
        else -> 2.2f + (surgeTimeStep - 3.0f) * 0.3f
    }

    // Realistic Markers dynamically generated per City Region
    val realisticMarkers = remember(selectedCityRegion, waterLevelSurgeMeters, boatMovePhase) {
        when (selectedCityRegion) {
            CityMapRegion.SMALL_CITY_TOWN -> listOf(
                RealisticMapMarker(
                    id = "sm_user",
                    title = "You (Town Market Bazaar)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.32f,
                    relY = 0.68f,
                    elevationMeters = 8.2,
                    waterDepthMeters = (0.65 + waterLevelSurgeMeters * 0.4).coerceAtLeast(0.0),
                    details = "Main Bazaar Ward 03 • Low-Lying Canal Basin • Water ingress detected",
                    telemetry = "GPS Accuracy: ±3m • Elevation: 8.2m MSL • LoRa Mesh: Connected"
                ),
                RealisticMapMarker(
                    id = "sm_panchayat",
                    title = "Amaravathi Panchayat Bhavan Shelter",
                    category = "SHELTER",
                    emoji = "🏛️",
                    relX = 0.72f,
                    relY = 0.24f,
                    elevationMeters = 42.0,
                    waterDepthMeters = 0.0,
                    details = "Elevated Civic Complex • Hot Kitchen, Backup Solar Generator & Clean Water Tanks",
                    telemetry = "Capacity: 210 / 450 Occupied (240 Free) • Status: 100% Dry 🟢"
                ),
                RealisticMapMarker(
                    id = "sm_school",
                    title = "Zilla Parishad High School Shelter",
                    category = "SHELTER",
                    emoji = "🏫",
                    relX = 0.78f,
                    relY = 0.48f,
                    elevationMeters = 29.0,
                    waterDepthMeters = 0.0,
                    details = "East Village High Grounds • Emergency Blankets & Baby Formula Station",
                    telemetry = "Capacity: 110 / 250 Occupied (140 Free) • Medical Volunteer Onsite"
                ),
                RealisticMapMarker(
                    id = "sm_phc",
                    title = "Rural Primary Health Centre (PHC)",
                    category = "HOSPITAL",
                    emoji = "🏥",
                    relX = 0.64f,
                    relY = 0.36f,
                    elevationMeters = 34.0,
                    waterDepthMeters = 0.0,
                    details = "24x7 Emergency Dispensary • Snake Bite Antivenom & Trauma Care Ready",
                    telemetry = "Doctors on Duty: 4 • Ambulance Fleet: 2 4x4s • Power: Solar Battery Active"
                ),
                RealisticMapMarker(
                    id = "sm_sluice",
                    title = "Canal Sluice Regulator Gate #03",
                    category = "BARRAGE",
                    emoji = "🌊",
                    relX = 0.46f,
                    relY = 0.52f,
                    elevationMeters = 11.5,
                    waterDepthMeters = (6.4 + waterLevelSurgeMeters * 0.5),
                    details = "Main Irrigation Diversion • Discharge: 42,000 cusecs • 8 Sluice Shutters Open",
                    telemetry = "Canal Water Level: 6.4m (Warning Mark: 5.8m) • Inflow Rising"
                ),
                RealisticMapMarker(
                    id = "sm_gauge",
                    title = "Town Canal Hydro Sensor #02",
                    category = "GAUGE",
                    emoji = "📡",
                    relX = 0.24f,
                    relY = 0.76f,
                    elevationMeters = 6.8,
                    waterDepthMeters = (1.9 + waterLevelSurgeMeters * 0.5),
                    details = "Acoustic Doppler Flow Sensor • Rising Rate: +14 cm/hour",
                    telemetry = "Rainfall Rate: 68mm/h • Velocity: 2.8 m/s • Trend: RISING 🔴"
                ),
                RealisticMapMarker(
                    id = "sm_tractor",
                    title = "SDRF High-Clearance Tractor Squad",
                    category = "RESCUE_BOAT",
                    emoji = "🚜",
                    relX = 0.38f + (boatMovePhase * 0.07f),
                    relY = 0.60f - (boatMovePhase * 0.05f),
                    elevationMeters = 9.0,
                    waterDepthMeters = 0.8,
                    details = "Modified Amphibious Evacuation Tractor with 2 Inflatable Dinghies",
                    telemetry = "Evacuees Onboard: 6 • Patrol Frequency: VHF 156.8 MHz • Status: En Route"
                ),
                RealisticMapMarker(
                    id = "sm_drone",
                    title = "Village Aerial Recon Drone #02",
                    category = "DRONE",
                    emoji = "🚁",
                    relX = 0.48f - (boatMovePhase * 0.05f),
                    relY = 0.44f + (boatMovePhase * 0.06f),
                    elevationMeters = 90.0,
                    waterDepthMeters = 0.0,
                    details = "Thermal Aerial Scanning for Stranded Farm Hamlets",
                    telemetry = "Battery: 82% • Infrared Camera: Online • Range: 4.5km"
                ),
                RealisticMapMarker(
                    id = "sm_culvert_block",
                    title = "Low Canal Culvert Bridge",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.40f,
                    relY = 0.62f,
                    elevationMeters = 7.1,
                    waterDepthMeters = (1.3 + waterLevelSurgeMeters * 0.5),
                    details = "IMPASSABLE: Submerged under 1.3m strong current. Village link cut.",
                    telemetry = "Barricade: Police Red Tape • Use North Bypass Road"
                )
            )

            CityMapRegion.VIJAYAWADA_METRO -> listOf(
                RealisticMapMarker(
                    id = "m_user",
                    title = "You (GPS Current Location)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.34f,
                    relY = 0.66f,
                    elevationMeters = 6.8,
                    waterDepthMeters = (0.75 + waterLevelSurgeMeters * 0.4).coerceAtLeast(0.0),
                    details = "Riverbank Ward 12 • High Inundation Hazard • Immediate Evacuation Recommended",
                    telemetry = "Accuracy: ±4m • Elevation: 6.8m MSL • LoRa Mesh: Connected"
                ),
                RealisticMapMarker(
                    id = "m_barrage",
                    title = "Prakasam Hydro Barrage",
                    category = "BARRAGE",
                    emoji = "🌊",
                    relX = 0.48f,
                    relY = 0.50f,
                    elevationMeters = 14.0,
                    waterDepthMeters = (18.2 + waterLevelSurgeMeters * 0.6),
                    details = "Discharge: 4,50,000 cusecs • Spillway Gates Open: 42/70",
                    telemetry = "Current Water Level: 18.2m (Danger Mark 17.5m EXCEEDED 🔴) • Inflow: 4.8L cusecs"
                ),
                RealisticMapMarker(
                    id = "m_gauge1",
                    title = "Krishna River Hydro Station #04",
                    category = "GAUGE",
                    emoji = "📡",
                    relX = 0.22f,
                    relY = 0.74f,
                    elevationMeters = 5.2,
                    waterDepthMeters = (2.1 + waterLevelSurgeMeters * 0.5),
                    details = "Live Hydro Sensor: +2.1m above baseline • Flow Velocity: 3.4 m/s",
                    telemetry = "Rainfall Rate: 72mm/h • Rising Trend (+12cm/h)"
                ),
                RealisticMapMarker(
                    id = "m_shelter1",
                    title = "St. Jude Safe Haven Relief Shelter",
                    category = "SHELTER",
                    emoji = "🏠",
                    relX = 0.70f,
                    relY = 0.26f,
                    elevationMeters = 34.0,
                    waterDepthMeters = 0.0,
                    details = "High Ground Haven • Hot Meals, Clean Water, Medical Station & Generators",
                    telemetry = "Capacity: 180 / 450 Occupied (270 Free Slots) • Ground Status: 100% Dry 🟢"
                ),
                RealisticMapMarker(
                    id = "m_shelter2",
                    title = "Govt High School Relief Center",
                    category = "SHELTER",
                    emoji = "🏫",
                    relX = 0.80f,
                    relY = 0.42f,
                    elevationMeters = 22.0,
                    waterDepthMeters = 0.0,
                    details = "Sector 8 Elevated Ground • Clean Water Supply & Power",
                    telemetry = "Capacity: 140 / 300 Occupied (160 Free) • Medical Aid: Available"
                ),
                RealisticMapMarker(
                    id = "m_hosp1",
                    title = "Apex City Emergency Trauma Hospital",
                    category = "HOSPITAL",
                    emoji = "🏥",
                    relX = 0.62f,
                    relY = 0.34f,
                    elevationMeters = 28.0,
                    waterDepthMeters = 0.0,
                    details = "Emergency Trauma Unit • 24x7 Flood Evacuation Triage Center",
                    telemetry = "ICU Ready: 8 beds • Oxygen Supply: 100% • Backup Generator Active"
                ),
                RealisticMapMarker(
                    id = "m_boat1",
                    title = "NDRF Zodiac Rescue Boat #02",
                    category = "RESCUE_BOAT",
                    emoji = "🚤",
                    relX = 0.30f + (boatMovePhase * 0.09f),
                    relY = 0.62f - (boatMovePhase * 0.07f),
                    elevationMeters = 7.0,
                    waterDepthMeters = 1.4,
                    details = "4 Operators Onboard • Deploying Inflatable Life Rafts & First Aid",
                    telemetry = "Speed: 11 knots • Patrol VHF Channel: 16 • LoRa Mesh Beacon: Active"
                ),
                RealisticMapMarker(
                    id = "m_drone1",
                    title = "SDRF Aero-Surveillance Drone #01",
                    category = "DRONE",
                    emoji = "🚁",
                    relX = 0.42f - (boatMovePhase * 0.06f),
                    relY = 0.56f + (boatMovePhase * 0.05f),
                    elevationMeters = 120.0,
                    waterDepthMeters = 0.0,
                    details = "Thermal & Optical Aerial Survey of Submerged Alleys",
                    telemetry = "Altitude: 120m AGL • Battery: 78% • Thermal Camera: Tracking Stranded Persons"
                ),
                RealisticMapMarker(
                    id = "m_block1",
                    title = "Old Canal Causeway Bridge",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.38f,
                    relY = 0.58f,
                    elevationMeters = 6.2,
                    waterDepthMeters = (1.4 + waterLevelSurgeMeters * 0.5),
                    details = "IMPASSABLE: Submerged by 1.4m Rapid Water Flow • High Undercurrents",
                    telemetry = "Barricade Status: POLICE BLOCKED • Do NOT attempt crossing"
                )
            )

            CityMapRegion.DIVISEEMA_ISLAND -> listOf(
                RealisticMapMarker(
                    id = "dv_user",
                    title = "You (Nagayalanka Lowland Hamlet)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.32f,
                    relY = 0.64f,
                    elevationMeters = 3.2,
                    waterDepthMeters = (0.95 + waterLevelSurgeMeters * 0.5).coerceAtLeast(0.0),
                    details = "Estuary Island Lowland • Surrounded by rising tidal channels",
                    telemetry = "GPS Accuracy: ±3m • Elevation: 3.2m MSL • LoRa Relay: Active"
                ),
                RealisticMapMarker(
                    id = "dv_shelter",
                    title = "Diviseema Cyclone Citadel Tower",
                    category = "SHELTER",
                    emoji = "🗼",
                    relX = 0.68f,
                    relY = 0.28f,
                    elevationMeters = 24.0,
                    waterDepthMeters = 0.0,
                    details = "Elevated 4-Storey Reinforced Pylon Shelter with Satellite Transceiver",
                    telemetry = "Capacity: 280 / 500 Occupied (220 Free) • Solar Water Desalination: 100%"
                ),
                RealisticMapMarker(
                    id = "dv_ghat",
                    title = "Nagayalanka River Ferry Ghat",
                    category = "BARRAGE",
                    emoji = "⛴️",
                    relX = 0.44f,
                    relY = 0.48f,
                    elevationMeters = 4.0,
                    waterDepthMeters = (3.2 + waterLevelSurgeMeters * 0.6),
                    details = "Motor Launch Ferry Terminal • Evacuating Island Hamlets to Mainland",
                    telemetry = "Ferries Active: 3 • River Current: 4.2 knots • Status: HIGH SURGE ⚠️"
                ),
                RealisticMapMarker(
                    id = "dv_hover",
                    title = "Amphibious Hovercraft Patrol Unit",
                    category = "RESCUE_BOAT",
                    emoji = "🛸",
                    relX = 0.36f + (boatMovePhase * 0.08f),
                    relY = 0.56f - (boatMovePhase * 0.06f),
                    elevationMeters = 2.0,
                    waterDepthMeters = 1.6,
                    details = "Skimming across submerged mudflats and sand spits",
                    telemetry = "Speed: 24 knots • Crew: 4 SDRF Divers • VHF: Ch 12"
                ),
                RealisticMapMarker(
                    id = "dv_block",
                    title = "Island South Causeway Bridge",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.40f,
                    relY = 0.58f,
                    elevationMeters = 2.8,
                    waterDepthMeters = (1.5 + waterLevelSurgeMeters * 0.5),
                    details = "Causeway Breach: Rapid estuary water cutoff • Use North Ferry route",
                    telemetry = "Barricade: Coastal Police • Warning: High Rip Currents"
                )
            )

            CityMapRegion.TIRUPATI_FOOTHILLS -> listOf(
                RealisticMapMarker(
                    id = "tp_user",
                    title = "You (Kapila Theertham Low Basin)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.30f,
                    relY = 0.70f,
                    elevationMeters = 152.0,
                    waterDepthMeters = (0.8 + waterLevelSurgeMeters * 0.5).coerceAtLeast(0.0),
                    details = "Hill Basin Gully • Heavy hill runoff cascading from upper ghats",
                    telemetry = "GPS Accuracy: ±4m • Elevation: 152m MSL • Mountain Stream Alert"
                ),
                RealisticMapMarker(
                    id = "tp_shelter",
                    title = "TTD Pilgrim Rest Choultry Complex",
                    category = "SHELTER",
                    emoji = "🏛️",
                    relX = 0.72f,
                    relY = 0.26f,
                    elevationMeters = 210.0,
                    waterDepthMeters = 0.0,
                    details = "Massive Elevated High-Ground Hall • Annaprasadam Kitchen & Emergency Beds",
                    telemetry = "Capacity: 850 / 2000 Occupied (1150 Free) • Clean Borewell Supply"
                ),
                RealisticMapMarker(
                    id = "tp_weir",
                    title = "Kalyani Dam Reservoir Surplus Weir",
                    category = "BARRAGE",
                    emoji = "🌊",
                    relX = 0.48f,
                    relY = 0.46f,
                    elevationMeters = 180.0,
                    waterDepthMeters = (8.5 + waterLevelSurgeMeters * 0.6),
                    details = "Surplus Discharge: 18,000 cusecs • Mountain Spills Active",
                    telemetry = "Reservoir Level: 98% Full • Alert Siren: Rung"
                ),
                RealisticMapMarker(
                    id = "tp_rescue",
                    title = "Mountain Ghat Quick Response 4x4",
                    category = "RESCUE_BOAT",
                    emoji = "🚙",
                    relX = 0.38f + (boatMovePhase * 0.07f),
                    relY = 0.54f - (boatMovePhase * 0.05f),
                    elevationMeters = 168.0,
                    waterDepthMeters = 0.3,
                    details = "Winch-equipped Off-Road Mountain Rescue Squad clearing gully traps",
                    telemetry = "Speed: 22 km/h • Gear: Ropes & Inflatables • Call Sign: HILL-RES-01"
                ),
                RealisticMapMarker(
                    id = "tp_block",
                    title = "Alipiri Foothill Underpass",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.36f,
                    relY = 0.62f,
                    elevationMeters = 148.0,
                    waterDepthMeters = (1.7 + waterLevelSurgeMeters * 0.5),
                    details = "Submerged Underpass: 1.7m mud and water slush. Traffic diverted.",
                    telemetry = "Status: CLOSED • Use Elevated Flyover Link"
                )
            )

            CityMapRegion.KANDALERU_INDUSTRIAL -> listOf(
                RealisticMapMarker(
                    id = "kd_user",
                    title = "You (Industrial Colony Sector 4)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.34f,
                    relY = 0.68f,
                    elevationMeters = 18.0,
                    waterDepthMeters = (0.7 + waterLevelSurgeMeters * 0.4).coerceAtLeast(0.0),
                    details = "Factory Township Low Ring • Flood dyke monitoring required",
                    telemetry = "GPS Accuracy: ±3m • Elevation: 18.0m MSL • Siren: Active"
                ),
                RealisticMapMarker(
                    id = "kd_shelter",
                    title = "Industrial Township High Auditorium",
                    category = "SHELTER",
                    emoji = "🏢",
                    relX = 0.74f,
                    relY = 0.24f,
                    elevationMeters = 48.0,
                    waterDepthMeters = 0.0,
                    details = "Highland Civic Centre with Heavy Power Grid & Medical Infirmary",
                    telemetry = "Capacity: 320 / 700 Occupied (380 Free) • Power: Diesel Generators 100%"
                ),
                RealisticMapMarker(
                    id = "kd_dyke",
                    title = "Chemical Plant Protective Flood Dyke",
                    category = "BARRAGE",
                    emoji = "🧱",
                    relX = 0.50f,
                    relY = 0.50f,
                    elevationMeters = 24.0,
                    waterDepthMeters = (4.8 + waterLevelSurgeMeters * 0.5),
                    details = "Hazmat Flood Containment Wall • High Capacity Dewatering Sump Pumps",
                    telemetry = "Pumps Running: 12/12 • Freeboard Remaining: 0.8m ⚠️"
                ),
                RealisticMapMarker(
                    id = "kd_hazmat",
                    title = "NDRF Hazmat & Flood Response Truck",
                    category = "RESCUE_BOAT",
                    emoji = "🚒",
                    relX = 0.40f + (boatMovePhase * 0.06f),
                    relY = 0.58f - (boatMovePhase * 0.04f),
                    elevationMeters = 21.0,
                    waterDepthMeters = 0.4,
                    details = "Equipped with Gas Sensors, Decontamination & High-water Rafts",
                    telemetry = "Status: Patrolling Sector 4 Perimeter • Air Quality: Normal"
                ),
                RealisticMapMarker(
                    id = "kd_block",
                    title = "Thermal Plant Discharge Canal Bridge",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.38f,
                    relY = 0.60f,
                    elevationMeters = 16.5,
                    waterDepthMeters = (1.6 + waterLevelSurgeMeters * 0.5),
                    details = "Overflowing Industrial Culvert: Water overtopping bridge deck",
                    telemetry = "Barricade: Industrial Security • Use North Ring Road"
                )
            )

            CityMapRegion.POLAVARAM_VALLEY -> listOf(
                RealisticMapMarker(
                    id = "pv_user",
                    title = "You (Lowland Riverside Hamlet)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.28f,
                    relY = 0.72f,
                    elevationMeters = 14.5,
                    waterDepthMeters = (1.1 + waterLevelSurgeMeters * 0.5).coerceAtLeast(0.0),
                    details = "Gorge Bottom Settlement • Rapid Flash Flood Risk • Ascend to Ridge immediately",
                    telemetry = "GPS Accuracy: ±5m • Elevation: 14.5m MSL • LoRa Relay: Active"
                ),
                RealisticMapMarker(
                    id = "pv_shelter",
                    title = "Highland Ridge Relief Camp",
                    category = "SHELTER",
                    emoji = "🏕️",
                    relX = 0.74f,
                    relY = 0.22f,
                    elevationMeters = 98.0,
                    waterDepthMeters = 0.0,
                    details = "Secure High Ridge Complex • Helipad & Satellite Communications Hub",
                    telemetry = "Capacity: 340 / 600 Occupied (260 Free) • Air Drop Zone: Ready"
                ),
                RealisticMapMarker(
                    id = "pv_dam",
                    title = "Polavaram Hydro Dam Reservoir",
                    category = "BARRAGE",
                    emoji = "🌊",
                    relX = 0.52f,
                    relY = 0.46f,
                    elevationMeters = 45.7,
                    waterDepthMeters = (38.4 + waterLevelSurgeMeters * 0.7),
                    details = "Spillway Discharge: 7,80,000 cusecs • 48 Radial Gates Operational",
                    telemetry = "Full Reservoir Level: 45.72m • Current: 43.1m • Warning: RED 🔴"
                ),
                RealisticMapMarker(
                    id = "pv_rescue",
                    title = "SDRF Mountain 4x4 Quick Response Unit",
                    category = "RESCUE_BOAT",
                    emoji = "🚙",
                    relX = 0.44f + (boatMovePhase * 0.06f),
                    relY = 0.54f - (boatMovePhase * 0.04f),
                    elevationMeters = 35.0,
                    waterDepthMeters = 0.3,
                    details = "Equipped with Winch Lines, Inflatable Rafts & Satellite Mesh Node",
                    telemetry = "Speed: 25 km/h • Radio: Ch 09 • Call Sign: MOUNTAIN-RESCUE-1"
                ),
                RealisticMapMarker(
                    id = "pv_block",
                    title = "Ghat Road Landslide Chokepoint",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.36f,
                    relY = 0.62f,
                    elevationMeters = 18.0,
                    waterDepthMeters = (1.8 + waterLevelSurgeMeters * 0.4),
                    details = "Rockfall & Mud Debris Inundation • Ghat Pass Closed for all vehicles",
                    telemetry = "Clearance Heavy Machinery En Route • Estimated Delay: 6 hrs"
                )
            )

            CityMapRegion.RAMPACHODAVARAM_FOREST -> listOf(
                RealisticMapMarker(
                    id = "rf_user",
                    title = "You (Stream-Side Tribal Hamlet)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.28f,
                    relY = 0.74f,
                    elevationMeters = 84.0,
                    waterDepthMeters = (1.2 + waterLevelSurgeMeters * 0.5).coerceAtLeast(0.0),
                    details = "Valley Stream Basin • Mountain rivulet swollen by cloudburst rain",
                    telemetry = "GPS Accuracy: ±6m • Elevation: 84m MSL • Forest Mesh Active"
                ),
                RealisticMapMarker(
                    id = "rf_shelter",
                    title = "High Plateau ITDA Tribal School",
                    category = "SHELTER",
                    emoji = "🏫",
                    relX = 0.72f,
                    relY = 0.22f,
                    elevationMeters = 180.0,
                    waterDepthMeters = 0.0,
                    details = "Solid Masonry Plateau Complex with Solar Microgrid & Grain Stocks",
                    telemetry = "Capacity: 160 / 350 Occupied (190 Free) • Medical Nurse Onsite"
                ),
                RealisticMapMarker(
                    id = "rf_stream",
                    title = "Sokuleru Forest Stream Flow Gauge",
                    category = "GAUGE",
                    emoji = "📡",
                    relX = 0.46f,
                    relY = 0.52f,
                    elevationMeters = 92.0,
                    waterDepthMeters = (5.2 + waterLevelSurgeMeters * 0.6),
                    details = "Ultrasonic River Gauge • Flow Speed: 4.8 m/s (Torrents Active)",
                    telemetry = "Rainfall: 112mm in 3h • Stream Level: DANGER 🔴"
                ),
                RealisticMapMarker(
                    id = "rf_jeep",
                    title = "Forest Dept Heavy Winch 4x4",
                    category = "RESCUE_BOAT",
                    emoji = "🚙",
                    relX = 0.38f + (boatMovePhase * 0.07f),
                    relY = 0.58f - (boatMovePhase * 0.05f),
                    elevationMeters = 96.0,
                    waterDepthMeters = 0.4,
                    details = "Off-road vehicle transporting village elders up to high plateau",
                    telemetry = "Speed: 18 km/h • High Snorkel Equipped • Forest Radio: Ch 04"
                ),
                RealisticMapMarker(
                    id = "rf_bridge_block",
                    title = "Wooden Suspension Footbridge",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.38f,
                    relY = 0.64f,
                    elevationMeters = 86.0,
                    waterDepthMeters = (2.1 + waterLevelSurgeMeters * 0.5),
                    details = "UNSAFE: Timber bridge destabilized by heavy tree trunks in stream",
                    telemetry = "Crossing Prohibited • Follow Northern Forest Ridge Trail"
                )
            )

            CityMapRegion.MACHILIPATNAM_COASTAL -> listOf(
                RealisticMapMarker(
                    id = "mc_user",
                    title = "You (Coastal Fishermen Colony)",
                    category = "USER",
                    emoji = "📍",
                    relX = 0.30f,
                    relY = 0.70f,
                    elevationMeters = 2.4,
                    waterDepthMeters = (1.2 + waterLevelSurgeMeters * 0.6).coerceAtLeast(0.0),
                    details = "High Storm Surge Zone • High Tide Tidal Wave Danger • Move Inward",
                    telemetry = "GPS Accuracy: ±3m • Elevation: 2.4m MSL • Sea Surge Ingress: Active"
                ),
                RealisticMapMarker(
                    id = "mc_shelter",
                    title = "Cyclone Multi-Purpose Safe Shelter #01",
                    category = "SHELTER",
                    emoji = "🏢",
                    relX = 0.72f,
                    relY = 0.28f,
                    elevationMeters = 18.0,
                    waterDepthMeters = 0.0,
                    details = "Reinforced Cyclone Resistant Concrete Citadel • 3-Storey Elevated Safe Floor",
                    telemetry = "Capacity: 450 / 800 Occupied (350 Free) • Diesel Generators: 100%"
                ),
                RealisticMapMarker(
                    id = "mc_tide_gauge",
                    title = "Port Maritime Tide Gauge #01",
                    category = "GAUGE",
                    emoji = "📡",
                    relX = 0.20f,
                    relY = 0.78f,
                    elevationMeters = 1.2,
                    waterDepthMeters = (3.4 + waterLevelSurgeMeters * 0.6),
                    details = "Live Tidal Surge Height: +3.4m over Astronomical High Tide",
                    telemetry = "Wind: 75 km/h ENE • Wave Height: 4.2m • Tidal Inflow: Peak"
                ),
                RealisticMapMarker(
                    id = "mc_hovercraft",
                    title = "Coast Guard Hovercraft ACV-04",
                    category = "RESCUE_BOAT",
                    emoji = "🛸",
                    relX = 0.36f + (boatMovePhase * 0.08f),
                    relY = 0.58f - (boatMovePhase * 0.06f),
                    elevationMeters = 1.5,
                    waterDepthMeters = 1.8,
                    details = "Amphibious Air-Cushion Vehicle skimming across flooded mudflats",
                    telemetry = "Speed: 28 knots • Payload: 16 Evacuees • Indian Coast Guard"
                ),
                RealisticMapMarker(
                    id = "mc_block",
                    title = "Coastal Bund Breach Road",
                    category = "ROADBLOCK",
                    emoji = "🚧",
                    relX = 0.34f,
                    relY = 0.64f,
                    elevationMeters = 1.8,
                    waterDepthMeters = (1.9 + waterLevelSurgeMeters * 0.5),
                    details = "Sea Dyke Breached: High salt water ingress across coastal corridor",
                    telemetry = "Status: PASSAGE FORBIDDEN • Evacuate via West Highway"
                )
            )
        }
    }

    // Filtered markers based on search query
    val displayedMarkers = remember(realisticMarkers, searchQuery) {
        if (searchQuery.isBlank()) {
            realisticMarkers
        } else {
            realisticMarkers.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.details.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Nearest shelter marker for ruler calculation
    val userMarker = displayedMarkers.firstOrNull { it.category == "USER" }
    val shelterMarker = displayedMarkers.firstOrNull { it.category == "SHELTER" }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                when (basemapMode) {
                    MapBasemapMode.SATELLITE_HYBRID -> Color(0xFF090E17)
                    MapBasemapMode.TOPOGRAPHIC_DEM -> Color(0xFF101923)
                    MapBasemapMode.STREET_VECTOR -> Color(0xFF0B1120)
                }
            )
            .testTag("live_flood_map_screen")
    ) {
        val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(100f)
        val heightPx = constraints.maxHeight.toFloat().coerceAtLeast(100f)

        // -------------------------------------------------------------
        // 1. REALISTIC MULTI-LAYER MAP CANVAS (Interactive Pan/Zoom)
        // -------------------------------------------------------------
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedCityRegion) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 4.5f)
                        offsetX = (offsetX + pan.x).coerceIn(-widthPx * 2f, widthPx * 2f)
                        offsetY = (offsetY + pan.y).coerceIn(-heightPx * 2f, heightPx * 2f)
                    }
                }
                .pointerInput(selectedCityRegion) {
                    detectTapGestures { tapOffset ->
                        val hitMarker = displayedMarkers.firstOrNull { marker ->
                            val markerScreenX = (marker.relX * widthPx * scale) + offsetX
                            val markerScreenY = (marker.relY * heightPx * scale) + offsetY
                            val dx = tapOffset.x - markerScreenX
                            val dy = tapOffset.y - markerScreenY
                            (dx * dx + dy * dy) < (42f * 42f)
                        }
                        selectedMarker = hitMarker
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // -------------------------------------------------------------
            // A. BASEMAP BACKGROUND & TERRAIN TEXTURE (City-Adaptive)
            // -------------------------------------------------------------
            when (basemapMode) {
                MapBasemapMode.SATELLITE_HYBRID -> {
                    drawSatelliteTerrain(this, canvasW, canvasH, scale, offsetX, offsetY, selectedCityRegion)
                }
                MapBasemapMode.TOPOGRAPHIC_DEM -> {
                    drawTopographicDEM(this, canvasW, canvasH, scale, offsetX, offsetY, selectedCityRegion)
                }
                MapBasemapMode.STREET_VECTOR -> {
                    drawStreetVectorBasemap(this, canvasW, canvasH, scale, offsetX, offsetY, selectedCityRegion)
                }
            }

            // -------------------------------------------------------------
            // B. TOPOGRAPHIC ELEVATION CONTOURS
            // -------------------------------------------------------------
            if (showContours) {
                drawContourElevationLines(this, canvasW, canvasH, scale, offsetX, offsetY, selectedCityRegion)
            }

            // -------------------------------------------------------------
            // C. STREET & ROAD NETWORK (Major Highways & Local Small Town Lanes)
            // -------------------------------------------------------------
            if (showRoads) {
                drawRoadNetwork(
                    this,
                    canvasW,
                    canvasH,
                    scale,
                    offsetX,
                    offsetY,
                    waterLevelSurgeMeters,
                    selectedCityRegion
                )
            }

            // -------------------------------------------------------------
            // D. REALISTIC HYDROGRAPHY (River, Town Canal or Gorge)
            // -------------------------------------------------------------
            drawCityHydrography(this, canvasW, canvasH, scale, offsetX, offsetY, flowPhase, selectedCityRegion)

            // -------------------------------------------------------------
            // E. FLOOD INUNDATION RISK HEATMAP & WATER SURGE LAYER
            // -------------------------------------------------------------
            if (showRiskHeatmap) {
                drawCityInundationHeatmap(
                    this,
                    canvasW,
                    canvasH,
                    scale,
                    offsetX,
                    offsetY,
                    waterLevelSurgeMeters,
                    beaconPulse,
                    selectedCityRegion
                )
            }

            // -------------------------------------------------------------
            // F. EVACUATION CORRIDOR & SAFE ESCAPE PATH
            // -------------------------------------------------------------
            if (showEvacCorridor) {
                drawCityEvacuationCorridors(
                    this,
                    canvasW,
                    canvasH,
                    scale,
                    offsetX,
                    offsetY,
                    flowPhase,
                    selectedCityRegion
                )
            }

            // -------------------------------------------------------------
            // G. INTERACTIVE RULER LINE (When Ruler Mode is Active)
            // -------------------------------------------------------------
            if (isRulerModeActive && userMarker != null && shelterMarker != null) {
                val ux = (userMarker.relX * canvasW * scale) + offsetX
                val uy = (userMarker.relY * canvasH * scale) + offsetY
                val sx = (shelterMarker.relX * canvasW * scale) + offsetX
                val sy = (shelterMarker.relY * canvasH * scale) + offsetY

                drawLine(
                    color = Color(0xFFF59E0B),
                    start = Offset(ux, uy),
                    end = Offset(sx, sy),
                    strokeWidth = 3f * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            }

            // -------------------------------------------------------------
            // H. GRID / COORDINATE TICKS
            // -------------------------------------------------------------
            drawCoordinateGrid(this, canvasW, canvasH, scale, offsetX, offsetY)

            // -------------------------------------------------------------
            // I. CANVAS DRAWN ACTIVE MARKER RIPPLE BEACONS
            // -------------------------------------------------------------
            displayedMarkers.forEach { marker ->
                val isVisible = when (marker.category) {
                    "USER" -> true
                    "SHELTER" -> showShelters
                    "HOSPITAL" -> showHospitals
                    "BARRAGE", "GAUGE" -> showGauges
                    "RESCUE_BOAT", "DRONE" -> showRescueFleet
                    "ROADBLOCK" -> showBlockedRoads
                    else -> true
                }
                if (isVisible) {
                    val mx = (marker.relX * canvasW * scale) + offsetX
                    val my = (marker.relY * canvasH * scale) + offsetY

                    // Beacon wave ring
                    if (marker.category == "USER") {
                        drawCircle(
                            color = SleekPrimary.copy(alpha = (0.4f / beaconPulse).coerceIn(0.1f, 0.5f)),
                            radius = 28f * scale * beaconPulse,
                            center = Offset(mx, my),
                            style = Stroke(width = 2.5f)
                        )
                        drawCircle(
                            color = SleekPrimary,
                            radius = 7f * scale,
                            center = Offset(mx, my)
                        )
                    } else if (marker.category == "RESCUE_BOAT") {
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
                            radius = 18f * scale * beaconPulse,
                            center = Offset(mx, my)
                        )
                    } else if (marker.category == "ROADBLOCK") {
                        drawCircle(
                            color = SleekCriticalRed.copy(alpha = 0.3f),
                            radius = 16f * scale,
                            center = Offset(mx, my)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. MARKER OVERLAYS (Positioned with safe Modifier.offset)
        // -------------------------------------------------------------
        displayedMarkers.forEach { marker ->
            val isVisible = when (marker.category) {
                "USER" -> true
                "SHELTER" -> showShelters
                "HOSPITAL" -> showHospitals
                "BARRAGE", "GAUGE" -> showGauges
                "RESCUE_BOAT", "DRONE" -> showRescueFleet
                "ROADBLOCK" -> showBlockedRoads
                else -> true
            }

            if (isVisible) {
                val screenX = (marker.relX * widthPx * scale) + offsetX - 36f
                val screenY = (marker.relY * heightPx * scale) + offsetY - 42f

                // Render within or near viewport boundaries
                if (screenX > -150f && screenX < widthPx + 150f && screenY > -150f && screenY < heightPx + 150f) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(screenX.roundToInt(), screenY.roundToInt()) }
                            .clickable {
                                selectedMarker = marker
                                val matchedZone = mapZones.firstOrNull { it.zoneCode == "ZONE 04" }
                                onSelectZone(matchedZone)
                            }
                    ) {
                        RealisticMarkerBadge(
                            marker = marker,
                            isSelected = selectedMarker?.id == marker.id
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 3. TOP HUD: CITY SELECTOR (8 Regions), BASEMAP SWITCHER, SEARCH & HOTSPOTS
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SleekSurface.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, SleekBorder),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Row 1: City / Region Selector Tabs (8 Diverse Regions)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        items(CityMapRegion.entries) { region ->
                            val isSelected = selectedCityRegion == region
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SleekPrimary else SleekSurfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) SleekPrimary else SleekBorder
                                ),
                                modifier = Modifier.clickable {
                                    selectedCityRegion = region
                                    selectedMarker = null
                                    scale = 1.0f
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(region.emoji, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Column {
                                        Text(
                                            text = region.displayName,
                                            color = if (isSelected) Color.White else SleekTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = region.categoryLabel,
                                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else SleekTextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Row 2: Subtitle Info & Basemap Toggle Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(SleekPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = selectedCityRegion.subtitle,
                                    color = SleekTextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // Basemap Toggle Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SleekSurfaceVariant,
                            border = BorderStroke(1.dp, SleekBorder)
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                BasemapPill("🛰️ Sat", basemapMode == MapBasemapMode.SATELLITE_HYBRID) {
                                    basemapMode = MapBasemapMode.SATELLITE_HYBRID
                                }
                                BasemapPill("⛰️ Topo", basemapMode == MapBasemapMode.TOPOGRAPHIC_DEM) {
                                    basemapMode = MapBasemapMode.TOPOGRAPHIC_DEM
                                }
                                BasemapPill("🗺️ Street", basemapMode == MapBasemapMode.STREET_VECTOR) {
                                    basemapMode = MapBasemapMode.STREET_VECTOR
                                }
                            }
                        }
                    }

                    // Expandable Search Bar
                    AnimatedVisibility(visible = isSearchExpanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search shelter, hospital, barrage, boat...", fontSize = 12.sp) },
                                singleLine = true,
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SleekPrimary,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedContainerColor = SleekSurfaceVariant,
                                    unfocusedContainerColor = SleekSurfaceVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 3: Quick Jump Landmark Hotspots
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSearchExpanded) SleekPrimary else SleekSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSearchExpanded) SleekPrimary else SleekBorder),
                                modifier = Modifier.clickable { isSearchExpanded = !isSearchExpanded }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = if (isSearchExpanded) Color.White else SleekTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        item {
                            QuickJumpChip("📍 You") {
                                userMarker?.let {
                                    selectedMarker = it
                                    scale = 1.6f
                                    offsetX = (widthPx / 2f) - (it.relX * widthPx * 1.6f)
                                    offsetY = (heightPx / 2f) - (it.relY * heightPx * 1.6f)
                                }
                            }
                        }
                        item {
                            QuickJumpChip("🏛️ Safe Haven") {
                                shelterMarker?.let {
                                    selectedMarker = it
                                    scale = 1.6f
                                    offsetX = (widthPx / 2f) - (it.relX * widthPx * 1.6f)
                                    offsetY = (heightPx / 2f) - (it.relY * heightPx * 1.6f)
                                }
                            }
                        }
                        item {
                            QuickJumpChip("🌊 River/Dam") {
                                displayedMarkers.firstOrNull { it.category == "BARRAGE" || it.category == "GAUGE" }?.let {
                                    selectedMarker = it
                                    scale = 1.6f
                                    offsetX = (widthPx / 2f) - (it.relX * widthPx * 1.6f)
                                    offsetY = (heightPx / 2f) - (it.relY * heightPx * 1.6f)
                                }
                            }
                        }
                        item {
                            QuickJumpChip("🚤 Rescue Fleet") {
                                displayedMarkers.firstOrNull { it.category == "RESCUE_BOAT" }?.let {
                                    selectedMarker = it
                                    scale = 1.6f
                                    offsetX = (widthPx / 2f) - (it.relX * widthPx * 1.6f)
                                    offsetY = (heightPx / 2f) - (it.relY * heightPx * 1.6f)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 4: GIS Layer Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            MapFilterChip("🌊 Inundation Risk", showRiskHeatmap) { showRiskHeatmap = !showRiskHeatmap }
                        }
                        item {
                            MapFilterChip("🛣️ Evac Corridor", showEvacCorridor) { showEvacCorridor = !showEvacCorridor }
                        }
                        item {
                            MapFilterChip("🏠 Safe Shelters", showShelters) { showShelters = !showShelters }
                        }
                        item {
                            MapFilterChip("📡 River Gauges", showGauges) { showGauges = !showGauges }
                        }
                        item {
                            MapFilterChip("🚤 Rescue Fleet", showRescueFleet) { showRescueFleet = !showRescueFleet }
                        }
                        item {
                            MapFilterChip("🚧 Impassable Roads", showBlockedRoads) { showBlockedRoads = !showBlockedRoads }
                        }
                        item {
                            MapFilterChip("🏥 Hospitals", showHospitals) { showHospitals = !showHospitals }
                        }
                        item {
                            MapFilterChip("📐 Contours", showContours) { showContours = !showContours }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. FLOATING ACTION CONTROLS (Compass, Legend, Ruler, Zoom, Recenter)
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Compass / Reset Bearing
            Surface(
                shape = CircleShape,
                color = SleekSurface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, SleekBorder),
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        scale = 1.0f
                        offsetX = 0f
                        offsetY = 0f
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = "North Compass",
                        tint = SleekCriticalRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Legend Information Dialog Trigger
            Surface(
                shape = CircleShape,
                color = SleekSurface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, SleekBorder),
                modifier = Modifier
                    .size(38.dp)
                    .clickable { showLegendDialog = !showLegendDialog }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Map Legend",
                        tint = SleekPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Distance Ruler Tool Toggle
            Surface(
                shape = CircleShape,
                color = if (isRulerModeActive) SleekPrimary else SleekSurface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, if (isRulerModeActive) SleekPrimary else SleekBorder),
                modifier = Modifier
                    .size(38.dp)
                    .clickable { isRulerModeActive = !isRulerModeActive }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = "Distance Ruler",
                        tint = if (isRulerModeActive) Color.White else SleekTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Zoom In
            FloatingActionButton(
                onClick = { scale = (scale * 1.3f).coerceAtMost(4.5f) },
                containerColor = SleekSurface,
                contentColor = SleekTextPrimary,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
            }

            // Zoom Out
            FloatingActionButton(
                onClick = { scale = (scale / 1.3f).coerceAtLeast(0.6f) },
                containerColor = SleekSurface,
                contentColor = SleekTextPrimary,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
            }

            // Recenter on User
            FloatingActionButton(
                onClick = {
                    userMarker?.let {
                        scale = 1.4f
                        offsetX = (widthPx / 2f) - (it.relX * widthPx * 1.4f)
                        offsetY = (heightPx / 2f) - (it.relY * heightPx * 1.4f)
                        selectedMarker = it
                    } ?: run {
                        scale = 1.0f
                        offsetX = 0f
                        offsetY = 0f
                    }
                },
                containerColor = SleekPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter", modifier = Modifier.size(20.dp))
            }
        }

        // -------------------------------------------------------------
        // 5. DISTANCE RULER HUD (When Active)
        // -------------------------------------------------------------
        if (isRulerModeActive && userMarker != null && shelterMarker != null) {
            val distDx = (shelterMarker.relX - userMarker.relX) * 3.5f // km approx
            val distDy = (shelterMarker.relY - userMarker.relY) * 3.5f
            val totalDistanceKm = hypot(distDx, distDy).coerceAtLeast(0.4f)
            val elevDiff = (shelterMarker.elevationMeters - userMarker.elevationMeters).coerceAtLeast(0.0)
            val walkTimeMin = ((totalDistanceKm / 4.2f) * 60).roundToInt()

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SleekSurface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 156.dp, start = 14.dp, end = 14.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Safe Haven Route Measurement",
                                color = SleekTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Straight-line: ${String.format("%.2f", totalDistanceKm)} km • Est. Walking: ~$walkTimeMin mins",
                                color = SleekTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Text(
                            text = "+${String.format("%.1f", elevDiff)}m MSL ⬆️",
                            color = Color(0xFF10B981),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 6. BOTTOM HUD: TIME-LAPSE FLOOD SURGE CONTROLLER & MARKER INSPECTION CARD
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 14.dp, start = 12.dp, end = 12.dp)
        ) {
            // A. Selected Marker Inspection Card
            AnimatedVisibility(
                visible = selectedMarker != null,
                enter = fadeIn() + androidx.compose.animation.slideInVertically { it / 2 },
                exit = fadeOut() + androidx.compose.animation.slideOutVertically { it / 2 }
            ) {
                selectedMarker?.let { marker ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekBorder),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(marker.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = marker.title,
                                            color = SleekTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = marker.category.replace("_", " "),
                                            color = SleekPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { selectedMarker = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = marker.details,
                                color = SleekTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            if (marker.telemetry.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SleekSurfaceVariant,
                                    border = BorderStroke(1.dp, SleekBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "📡 Telemetry: ${marker.telemetry}",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MarkerStat("Elevation", "${marker.elevationMeters}m MSL")
                                MarkerStat("Water Ingress", if (marker.waterDepthMeters > 0) "+${String.format("%.2f", marker.waterDepthMeters)}m" else "Dry (0.0m)")
                                MarkerStat(
                                    "Safety Status",
                                    if (marker.waterDepthMeters > 1.0) "🔴 HAZARDOUS"
                                    else if (marker.waterDepthMeters > 0.3) "🟠 WATERLOGGED"
                                    else "🟢 SECURE"
                                )
                            }
                        }
                    }
                }
            }

            // B. Time-Lapse Surge Simulator Slider Bar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SleekSurface.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, SleekBorder),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏳", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Surge Time-Lapse Simulation",
                                color = SleekTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0369A1).copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "Surge: +${String.format("%.2f", waterLevelSurgeMeters)}m",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = { isSurgeAutoPlaying = !isSurgeAutoPlaying },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    if (isSurgeAutoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Auto Play",
                                    tint = SleekPrimary
                                )
                            }
                        }
                    }

                    Slider(
                        value = surgeTimeStep,
                        onValueChange = {
                            surgeTimeStep = it
                            isSurgeAutoPlaying = false
                        },
                        valueRange = 0.0f..3.0f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = SleekPrimary,
                            activeTrackColor = SleekPrimary,
                            inactiveTrackColor = SleekSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimeStepLabel("Baseline (Now)", "+0.0m", surgeTimeStep <= 0.5f)
                        TimeStepLabel("+1 Hour", "+0.5m", surgeTimeStep in 0.6f..1.5f)
                        TimeStepLabel("+3 Hours", "+1.2m", surgeTimeStep in 1.6f..2.5f)
                        TimeStepLabel("+6h Peak Surge", "+2.2m", surgeTimeStep > 2.5f)
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 7. MAP LEGEND POPUP DIALOG
        // -------------------------------------------------------------
        if (showLegendDialog) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SleekSurface,
                border = BorderStroke(1.dp, SleekBorder),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GIS Map Layers & Symbology",
                            color = SleekTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showLegendDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LegendItem(Color(0xFFEF4444), "Extreme Inundation Hazard (>1.5m Depth)")
                    LegendItem(Color(0xFFF97316), "High Water Accumulation (0.8m - 1.5m)")
                    LegendItem(Color(0xFFFACC15), "Moderate Waterlog Hazard (0.3m - 0.8m)")
                    LegendItem(Color(0xFF10B981), "Safe High Ground & Evacuation Corridors")
                    LegendItem(Color(0xFF38BDF8), "Active Rivers, Canals & Hydrography")
                    LegendItem(Color(0xFF8B5CF6), "NDRF Zodiac & Amphibious Patrol Fleet")
                    LegendItem(Color(0xFFF59E0B), "Impassable Barricaded Roads")

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showLegendDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("GOT IT", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CANVAS DRAWING HELPER FUNCTIONS (City-Region Adaptive)
// -------------------------------------------------------------

private fun drawSatelliteTerrain(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    region: CityMapRegion
) {
    // Satellite base dark tone
    scope.drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF131D2A), Color(0xFF070B11)),
            center = Offset(w / 2f + ox * 0.3f, h / 2f + oy * 0.3f),
            radius = w * scale * 1.2f
        )
    )

    when (region) {
        CityMapRegion.SMALL_CITY_TOWN -> {
            // Farmland patches, village agricultural fields & town market plots
            val field1 = Path().apply {
                moveTo(ox + w * 0.05f * scale, oy + h * 0.10f * scale)
                lineTo(ox + w * 0.45f * scale, oy + h * 0.12f * scale)
                lineTo(ox + w * 0.40f * scale, oy + h * 0.42f * scale)
                lineTo(ox + w * 0.08f * scale, oy + h * 0.38f * scale)
                close()
            }
            scope.drawPath(field1, Color(0xFF1B3224).copy(alpha = 0.5f))

            val field2 = Path().apply {
                moveTo(ox + w * 0.55f * scale, oy + h * 0.05f * scale)
                lineTo(ox + w * 0.95f * scale, oy + h * 0.08f * scale)
                lineTo(ox + w * 0.92f * scale, oy + h * 0.45f * scale)
                lineTo(ox + w * 0.52f * scale, oy + h * 0.40f * scale)
                close()
            }
            scope.drawPath(field2, Color(0xFF142B20).copy(alpha = 0.6f))

            // Town Center compact residential zone
            val townCenter = Path().apply {
                moveTo(ox + w * 0.20f * scale, oy + h * 0.55f * scale)
                lineTo(ox + w * 0.55f * scale, oy + h * 0.52f * scale)
                lineTo(ox + w * 0.50f * scale, oy + h * 0.85f * scale)
                lineTo(ox + w * 0.15f * scale, oy + h * 0.82f * scale)
                close()
            }
            scope.drawPath(townCenter, Color(0xFF262E3B).copy(alpha = 0.55f))
        }

        CityMapRegion.VIJAYAWADA_METRO -> {
            // Dense urban grid zones
            val urbanZone1 = Path().apply {
                moveTo(ox + w * 0.08f * scale, oy + h * 0.15f * scale)
                lineTo(ox + w * 0.42f * scale, oy + h * 0.18f * scale)
                lineTo(ox + w * 0.38f * scale, oy + h * 0.48f * scale)
                lineTo(ox + w * 0.05f * scale, oy + h * 0.45f * scale)
                close()
            }
            scope.drawPath(urbanZone1, Color(0xFF222B38).copy(alpha = 0.6f))

            val highland = Path().apply {
                moveTo(ox + w * 0.58f * scale, oy + h * 0.10f * scale)
                lineTo(ox + w * 0.92f * scale, oy + h * 0.12f * scale)
                lineTo(ox + w * 0.88f * scale, oy + h * 0.46f * scale)
                lineTo(ox + w * 0.54f * scale, oy + h * 0.42f * scale)
                close()
            }
            scope.drawPath(highland, Color(0xFF2A231C).copy(alpha = 0.5f))
        }

        CityMapRegion.DIVISEEMA_ISLAND -> {
            // Estuary Delta Island shape surrounded by river channels
            val islandBody = Path().apply {
                moveTo(ox + w * 0.15f * scale, oy + h * 0.30f * scale)
                cubicTo(
                    ox + w * 0.50f * scale, oy + h * 0.15f * scale,
                    ox + w * 0.85f * scale, oy + h * 0.25f * scale,
                    ox + w * 0.88f * scale, oy + h * 0.70f * scale
                )
                cubicTo(
                    ox + w * 0.60f * scale, oy + h * 0.85f * scale,
                    ox + w * 0.25f * scale, oy + h * 0.80f * scale,
                    ox + w * 0.15f * scale, oy + h * 0.30f * scale
                )
                close()
            }
            scope.drawPath(islandBody, Color(0xFF1E3326).copy(alpha = 0.75f))
        }

        CityMapRegion.TIRUPATI_FOOTHILLS -> {
            // Hill Range upper terrain
            val hillRange = Path().apply {
                moveTo(ox, oy)
                lineTo(ox + w * scale, oy)
                lineTo(ox + w * scale, oy + h * 0.40f * scale)
                lineTo(ox, oy + h * 0.30f * scale)
                close()
            }
            scope.drawPath(hillRange, Color(0xFF38291F).copy(alpha = 0.7f))

            // Valley basin settlement
            val basin = Path().apply {
                moveTo(ox, oy + h * 0.50f * scale)
                lineTo(ox + w * scale, oy + h * 0.50f * scale)
                lineTo(ox + w * scale, oy + h * scale)
                lineTo(ox, oy + h * scale)
                close()
            }
            scope.drawPath(basin, Color(0xFF202A36).copy(alpha = 0.5f))
        }

        CityMapRegion.KANDALERU_INDUSTRIAL -> {
            // Industrial Grid & Plant structures
            val industrialPark = Path().apply {
                moveTo(ox + w * 0.10f * scale, oy + h * 0.15f * scale)
                lineTo(ox + w * 0.85f * scale, oy + h * 0.15f * scale)
                lineTo(ox + w * 0.85f * scale, oy + h * 0.80f * scale)
                lineTo(ox + w * 0.10f * scale, oy + h * 0.80f * scale)
                close()
            }
            scope.drawPath(industrialPark, Color(0xFF282833).copy(alpha = 0.6f))
        }

        CityMapRegion.POLAVARAM_VALLEY -> {
            // Mountain slopes & hill ridge
            val mountainRidge = Path().apply {
                moveTo(ox + w * 0.50f * scale, oy)
                lineTo(ox + w * 1.0f * scale, oy)
                lineTo(ox + w * 1.0f * scale, oy + h * 0.60f * scale)
                lineTo(ox + w * 0.45f * scale, oy + h * 0.40f * scale)
                close()
            }
            scope.drawPath(mountainRidge, Color(0xFF382E24).copy(alpha = 0.7f))
        }

        CityMapRegion.RAMPACHODAVARAM_FOREST -> {
            // Deep Forest Canopy Texture
            val forestCanopy = Path().apply {
                moveTo(ox, oy)
                lineTo(ox + w * scale, oy)
                lineTo(ox + w * scale, oy + h * scale)
                lineTo(ox, oy + h * scale)
                close()
            }
            scope.drawPath(forestCanopy, Color(0xFF0F2617).copy(alpha = 0.8f))
        }

        CityMapRegion.MACHILIPATNAM_COASTAL -> {
            // Coastal tidal flats & mangrove wetlands
            val mangroveBelt = Path().apply {
                moveTo(ox, oy + h * 0.45f * scale)
                lineTo(ox + w * 0.55f * scale, oy + h * 0.50f * scale)
                lineTo(ox + w * 0.45f * scale, oy + h * 0.90f * scale)
                lineTo(ox, oy + h * 0.85f * scale)
                close()
            }
            scope.drawPath(mangroveBelt, Color(0xFF143026).copy(alpha = 0.65f))
        }
    }
}

private fun drawTopographicDEM(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    region: CityMapRegion
) {
    val demBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF263327),
            Color(0xFF3B3322),
            Color(0xFF422820)
        ),
        start = Offset(ox, oy + h * scale),
        end = Offset(ox + w * scale, oy)
    )
    scope.drawRect(brush = demBrush)
}

private fun drawStreetVectorBasemap(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    region: CityMapRegion
) {
    scope.drawRect(Color(0xFF0A0F1A))
}

private fun drawContourElevationLines(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    region: CityMapRegion
) {
    val contourColor = Color(0x28CBD5E1)
    val contourColorMajor = Color(0x45F59E0B)

    for (i in 1..4) {
        val yBase = oy + (h * (0.15f + i * 0.16f) * scale)
        val p = Path().apply {
            moveTo(ox, yBase)
            cubicTo(
                ox + w * 0.3f * scale, yBase - 35f * scale * i,
                ox + w * 0.7f * scale, yBase + 25f * scale * i,
                ox + w * scale, yBase - 15f * scale
            )
        }
        scope.drawPath(
            path = p,
            color = if (i % 2 == 0) contourColorMajor else contourColor,
            style = Stroke(
                width = if (i % 2 == 0) 1.8f else 1.0f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
            )
        )
    }
}

private fun drawRoadNetwork(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    surgeMeters: Float,
    region: CityMapRegion
) {
    when (region) {
        CityMapRegion.SMALL_CITY_TOWN -> {
            val bazaarRoad = Path().apply {
                moveTo(ox + w * 0.10f * scale, oy + h * 0.80f * scale)
                lineTo(ox + w * 0.35f * scale, oy + h * 0.65f * scale)
                lineTo(ox + w * 0.70f * scale, oy + h * 0.25f * scale)
            }
            scope.drawPath(bazaarRoad, Color(0xFF475569), style = Stroke(width = 7f * scale, cap = StrokeCap.Round))
            scope.drawPath(bazaarRoad, Color(0xFFF8FAFC), style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))

            val canalBundRoad = Path().apply {
                moveTo(ox + w * 0.05f * scale, oy + h * 0.65f * scale)
                lineTo(ox + w * 0.40f * scale, oy + h * 0.60f * scale)
                lineTo(ox + w * 0.85f * scale, oy + h * 0.55f * scale)
            }
            scope.drawPath(canalBundRoad, Color(0xFF334155), style = Stroke(width = 5f * scale, cap = StrokeCap.Round))

            val floodedCulvert = Path().apply {
                moveTo(ox + w * 0.34f * scale, oy + h * 0.61f * scale)
                lineTo(ox + w * 0.44f * scale, oy + h * 0.59f * scale)
            }
            scope.drawPath(
                floodedCulvert,
                SleekCriticalRed,
                style = Stroke(
                    width = 8f * scale,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )

            val templeBypass = Path().apply {
                moveTo(ox + w * 0.35f * scale, oy + h * 0.65f * scale)
                lineTo(ox + w * 0.45f * scale, oy + h * 0.40f * scale)
                lineTo(ox + w * 0.72f * scale, oy + h * 0.24f * scale)
            }
            scope.drawPath(templeBypass, Color(0xFF38BDF8), style = Stroke(width = 4f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.VIJAYAWADA_METRO -> {
            val nh16 = Path().apply {
                moveTo(ox + w * 0.15f * scale, oy + h * 0.88f * scale)
                lineTo(ox + w * 0.45f * scale, oy + h * 0.50f * scale)
                lineTo(ox + w * 0.82f * scale, oy + h * 0.15f * scale)
            }
            scope.drawPath(nh16, Color(0xFF475569), style = Stroke(width = 9f * scale, cap = StrokeCap.Round))
            scope.drawPath(nh16, Color(0xFFF1F5F9), style = Stroke(width = 4f * scale, cap = StrokeCap.Round))

            val canalRoad = Path().apply {
                moveTo(ox + w * 0.10f * scale, oy + h * 0.60f * scale)
                lineTo(ox + w * 0.42f * scale, oy + h * 0.55f * scale)
                lineTo(ox + w * 0.75f * scale, oy + h * 0.45f * scale)
            }
            scope.drawPath(canalRoad, Color(0xFF334155), style = Stroke(width = 6f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.DIVISEEMA_ISLAND -> {
            val islandSpineRoad = Path().apply {
                moveTo(ox + w * 0.25f * scale, oy + h * 0.75f * scale)
                lineTo(ox + w * 0.45f * scale, oy + h * 0.50f * scale)
                lineTo(ox + w * 0.68f * scale, oy + h * 0.28f * scale)
            }
            scope.drawPath(islandSpineRoad, Color(0xFF475569), style = Stroke(width = 7f * scale, cap = StrokeCap.Round))
            scope.drawPath(islandSpineRoad, Color(0xFFF8FAFC), style = Stroke(width = 3f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.TIRUPATI_FOOTHILLS -> {
            val bypassHwy = Path().apply {
                moveTo(ox + w * 0.10f * scale, oy + h * 0.85f * scale)
                lineTo(ox + w * 0.40f * scale, oy + h * 0.60f * scale)
                lineTo(ox + w * 0.72f * scale, oy + h * 0.26f * scale)
            }
            scope.drawPath(bypassHwy, Color(0xFF475569), style = Stroke(width = 8f * scale, cap = StrokeCap.Round))
            scope.drawPath(bypassHwy, Color(0xFFF8FAFC), style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.KANDALERU_INDUSTRIAL -> {
            val plantAvenue = Path().apply {
                moveTo(ox + w * 0.15f * scale, oy + h * 0.80f * scale)
                lineTo(ox + w * 0.48f * scale, oy + h * 0.50f * scale)
                lineTo(ox + w * 0.74f * scale, oy + h * 0.24f * scale)
            }
            scope.drawPath(plantAvenue, Color(0xFF475569), style = Stroke(width = 8f * scale, cap = StrokeCap.Round))
            scope.drawPath(plantAvenue, Color(0xFFF8FAFC), style = Stroke(width = 4f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.POLAVARAM_VALLEY -> {
            val ghatRoad = Path().apply {
                moveTo(ox + w * 0.15f * scale, oy + h * 0.80f * scale)
                lineTo(ox + w * 0.35f * scale, oy + h * 0.62f * scale)
                lineTo(ox + w * 0.48f * scale, oy + h * 0.48f * scale)
                lineTo(ox + w * 0.74f * scale, oy + h * 0.22f * scale)
            }
            scope.drawPath(ghatRoad, Color(0xFF475569), style = Stroke(width = 6f * scale, cap = StrokeCap.Round))
            scope.drawPath(ghatRoad, Color(0xFFF8FAFC), style = Stroke(width = 3f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.RAMPACHODAVARAM_FOREST -> {
            val forestRidgeTrack = Path().apply {
                moveTo(ox + w * 0.15f * scale, oy + h * 0.85f * scale)
                lineTo(ox + w * 0.38f * scale, oy + h * 0.60f * scale)
                lineTo(ox + w * 0.72f * scale, oy + h * 0.22f * scale)
            }
            scope.drawPath(forestRidgeTrack, Color(0xFF64748B), style = Stroke(width = 5f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.MACHILIPATNAM_COASTAL -> {
            val coastalHwy = Path().apply {
                moveTo(ox + w * 0.10f * scale, oy + h * 0.85f * scale)
                lineTo(ox + w * 0.40f * scale, oy + h * 0.65f * scale)
                lineTo(ox + w * 0.72f * scale, oy + h * 0.28f * scale)
            }
            scope.drawPath(coastalHwy, Color(0xFF475569), style = Stroke(width = 7f * scale, cap = StrokeCap.Round))
            scope.drawPath(coastalHwy, Color(0xFFF8FAFC), style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
        }
    }
}

private fun drawCityHydrography(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    flowPhase: Float,
    region: CityMapRegion
) {
    val riverPath = Path()

    when (region) {
        CityMapRegion.SMALL_CITY_TOWN -> {
            riverPath.moveTo(ox, oy + h * 0.50f * scale)
            riverPath.cubicTo(
                ox + w * 0.30f * scale, oy + h * 0.48f * scale,
                ox + w * 0.50f * scale, oy + h * 0.58f * scale,
                ox + w * scale, oy + h * 0.54f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0C4A6E).copy(alpha = 0.5f), style = Stroke(width = 24f * scale, cap = StrokeCap.Round))
            scope.drawPath(
                riverPath,
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0369A1), Color(0xFF0284C7), Color(0xFF38BDF8)),
                    start = Offset(ox, oy + h * 0.50f * scale),
                    end = Offset(ox + w * scale, oy + h * 0.54f * scale)
                ),
                style = Stroke(width = 16f * scale, cap = StrokeCap.Round)
            )
        }

        CityMapRegion.VIJAYAWADA_METRO -> {
            riverPath.moveTo(ox, oy + h * 0.42f * scale)
            riverPath.cubicTo(
                ox + w * 0.25f * scale, oy + h * 0.40f * scale,
                ox + w * 0.48f * scale, oy + h * 0.52f * scale,
                ox + w * scale, oy + h * 0.58f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0C4A6E).copy(alpha = 0.6f), style = Stroke(width = 44f * scale, cap = StrokeCap.Round))
            scope.drawPath(
                riverPath,
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0369A1), Color(0xFF0284C7), Color(0xFF38BDF8)),
                    start = Offset(ox, oy + h * 0.42f * scale),
                    end = Offset(ox + w * scale, oy + h * 0.58f * scale)
                ),
                style = Stroke(width = 30f * scale, cap = StrokeCap.Round)
            )
        }

        CityMapRegion.DIVISEEMA_ISLAND -> {
            // Bifurcated Estuary Streams around island
            riverPath.moveTo(ox, oy + h * 0.25f * scale)
            riverPath.cubicTo(
                ox + w * 0.40f * scale, oy + h * 0.15f * scale,
                ox + w * 0.75f * scale, oy + h * 0.18f * scale,
                ox + w * scale, oy + h * 0.40f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0284C7), style = Stroke(width = 32f * scale, cap = StrokeCap.Round))

            val southBranch = Path().apply {
                moveTo(ox, oy + h * 0.70f * scale)
                cubicTo(
                    ox + w * 0.45f * scale, oy + h * 0.85f * scale,
                    ox + w * 0.75f * scale, oy + h * 0.80f * scale,
                    ox + w * scale, oy + h * 0.65f * scale
                )
            }
            scope.drawPath(southBranch, Color(0xFF0284C7), style = Stroke(width = 30f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.TIRUPATI_FOOTHILLS -> {
            riverPath.moveTo(ox + w * 0.20f * scale, oy)
            riverPath.cubicTo(
                ox + w * 0.35f * scale, oy + h * 0.35f * scale,
                ox + w * 0.45f * scale, oy + h * 0.55f * scale,
                ox + w * 0.30f * scale, oy + h * scale
            )
            scope.drawPath(riverPath, Color(0xFF0369A1), style = Stroke(width = 22f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.KANDALERU_INDUSTRIAL -> {
            riverPath.moveTo(ox, oy + h * 0.45f * scale)
            riverPath.cubicTo(
                ox + w * 0.40f * scale, oy + h * 0.45f * scale,
                ox + w * 0.60f * scale, oy + h * 0.60f * scale,
                ox + w * scale, oy + h * 0.55f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0284C7), style = Stroke(width = 26f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.POLAVARAM_VALLEY -> {
            riverPath.moveTo(ox, oy + h * 0.35f * scale)
            riverPath.cubicTo(
                ox + w * 0.30f * scale, oy + h * 0.42f * scale,
                ox + w * 0.55f * scale, oy + h * 0.46f * scale,
                ox + w * scale, oy + h * 0.62f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0369A1), style = Stroke(width = 28f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.RAMPACHODAVARAM_FOREST -> {
            riverPath.moveTo(ox, oy + h * 0.20f * scale)
            riverPath.cubicTo(
                ox + w * 0.30f * scale, oy + h * 0.45f * scale,
                ox + w * 0.50f * scale, oy + h * 0.55f * scale,
                ox + w * scale, oy + h * 0.70f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0284C7), style = Stroke(width = 20f * scale, cap = StrokeCap.Round))
        }

        CityMapRegion.MACHILIPATNAM_COASTAL -> {
            riverPath.moveTo(ox, oy + h * 0.60f * scale)
            riverPath.cubicTo(
                ox + w * 0.35f * scale, oy + h * 0.55f * scale,
                ox + w * 0.65f * scale, oy + h * 0.70f * scale,
                ox + w * scale, oy + h * 0.75f * scale
            )
            scope.drawPath(riverPath, Color(0xFF0284C7), style = Stroke(width = 40f * scale, cap = StrokeCap.Round))
        }
    }

    // Animated Flow Ripple Lines
    scope.drawPath(
        path = riverPath,
        color = Color.White.copy(alpha = 0.45f),
        style = Stroke(
            width = 2.5f * scale,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 16f), flowPhase)
        )
    )
}

private fun drawCityInundationHeatmap(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    surgeMeters: Float,
    beaconPulse: Float,
    region: CityMapRegion
) {
    val surgeSpread = (surgeMeters * 35f * scale)

    // Red Danger Inundation Zone (Low riverbank basin)
    val redZoneCenter = Offset(ox + w * 0.32f * scale, oy + h * 0.66f * scale)
    scope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                RiskExtremeRed.copy(alpha = (0.55f * (0.8f + surgeMeters * 0.2f)).coerceAtMost(0.85f)),
                RiskExtremeRed.copy(alpha = 0.25f),
                Color.Transparent
            ),
            center = redZoneCenter,
            radius = (110f * scale + surgeSpread) * beaconPulse
        ),
        radius = 110f * scale + surgeSpread,
        center = redZoneCenter
    )

    // Orange Moderate Risk Zone
    val orangeZoneCenter = Offset(ox + w * 0.45f * scale, oy + h * 0.52f * scale)
    scope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                RiskHighOrange.copy(alpha = 0.45f),
                RiskModerateYellow.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = orangeZoneCenter,
            radius = 140f * scale + surgeSpread
        ),
        radius = 140f * scale + surgeSpread,
        center = orangeZoneCenter
    )
}

private fun drawCityEvacuationCorridors(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float,
    flowPhase: Float,
    region: CityMapRegion
) {
    val safeRoute = Path()

    when (region) {
        CityMapRegion.SMALL_CITY_TOWN -> {
            safeRoute.moveTo(ox + w * 0.32f * scale, oy + h * 0.68f * scale)
            safeRoute.lineTo(ox + w * 0.45f * scale, oy + h * 0.40f * scale)
            safeRoute.lineTo(ox + w * 0.72f * scale, oy + h * 0.24f * scale)
        }

        CityMapRegion.VIJAYAWADA_METRO -> {
            safeRoute.moveTo(ox + w * 0.34f * scale, oy + h * 0.66f * scale)
            safeRoute.lineTo(ox + w * 0.48f * scale, oy + h * 0.46f * scale)
            safeRoute.lineTo(ox + w * 0.70f * scale, oy + h * 0.26f * scale)
        }

        CityMapRegion.DIVISEEMA_ISLAND -> {
            safeRoute.moveTo(ox + w * 0.32f * scale, oy + h * 0.64f * scale)
            safeRoute.lineTo(ox + w * 0.46f * scale, oy + h * 0.44f * scale)
            safeRoute.lineTo(ox + w * 0.68f * scale, oy + h * 0.28f * scale)
        }

        CityMapRegion.TIRUPATI_FOOTHILLS -> {
            safeRoute.moveTo(ox + w * 0.30f * scale, oy + h * 0.70f * scale)
            safeRoute.lineTo(ox + w * 0.50f * scale, oy + h * 0.48f * scale)
            safeRoute.lineTo(ox + w * 0.72f * scale, oy + h * 0.26f * scale)
        }

        CityMapRegion.KANDALERU_INDUSTRIAL -> {
            safeRoute.moveTo(ox + w * 0.34f * scale, oy + h * 0.68f * scale)
            safeRoute.lineTo(ox + w * 0.52f * scale, oy + h * 0.46f * scale)
            safeRoute.lineTo(ox + w * 0.74f * scale, oy + h * 0.24f * scale)
        }

        CityMapRegion.POLAVARAM_VALLEY -> {
            safeRoute.moveTo(ox + w * 0.28f * scale, oy + h * 0.72f * scale)
            safeRoute.lineTo(ox + w * 0.48f * scale, oy + h * 0.48f * scale)
            safeRoute.lineTo(ox + w * 0.74f * scale, oy + h * 0.22f * scale)
        }

        CityMapRegion.RAMPACHODAVARAM_FOREST -> {
            safeRoute.moveTo(ox + w * 0.28f * scale, oy + h * 0.74f * scale)
            safeRoute.lineTo(ox + w * 0.48f * scale, oy + h * 0.50f * scale)
            safeRoute.lineTo(ox + w * 0.72f * scale, oy + h * 0.22f * scale)
        }

        CityMapRegion.MACHILIPATNAM_COASTAL -> {
            safeRoute.moveTo(ox + w * 0.30f * scale, oy + h * 0.70f * scale)
            safeRoute.lineTo(ox + w * 0.48f * scale, oy + h * 0.50f * scale)
            safeRoute.lineTo(ox + w * 0.72f * scale, oy + h * 0.28f * scale)
        }
    }

    // Outer Glow
    scope.drawPath(
        path = safeRoute,
        color = Color(0xFF10B981).copy(alpha = 0.3f),
        style = Stroke(width = 12f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Animated Chevrons
    scope.drawPath(
        path = safeRoute,
        color = Color(0xFF10B981),
        style = Stroke(
            width = 5f * scale,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), flowPhase)
        )
    )
}

private fun drawCoordinateGrid(
    scope: DrawScope,
    w: Float,
    h: Float,
    scale: Float,
    ox: Float,
    oy: Float
) {
    val gridStep = (100f * scale).coerceAtLeast(40f)
    var gx = ((ox % gridStep) + gridStep) % gridStep
    while (gx < w && gx >= 0f) {
        scope.drawLine(Color(0x10FFFFFF), Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
        gx += gridStep
    }
    var gy = ((oy % gridStep) + gridStep) % gridStep
    while (gy < h && gy >= 0f) {
        scope.drawLine(Color(0x10FFFFFF), Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
        gy += gridStep
    }
}

// -------------------------------------------------------------
// UI COMPONENT HELPERS & BADGES
// -------------------------------------------------------------

@Composable
fun RealisticMarkerBadge(
    marker: RealisticMapMarker,
    isSelected: Boolean
) {
    val bgColor = when (marker.category) {
        "USER" -> SleekPrimary
        "SHELTER" -> Color(0xFF10B981)
        "HOSPITAL" -> SleekCriticalRed
        "BARRAGE", "GAUGE" -> Color(0xFF0284C7)
        "RESCUE_BOAT", "DRONE" -> Color(0xFF8B5CF6)
        "ROADBLOCK" -> Color(0xFFF59E0B)
        else -> SleekPrimary
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color.White else bgColor,
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) SleekPrimary else Color.White.copy(alpha = 0.5f)
        ),
        shadowElevation = if (isSelected) 8.dp else 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(marker.emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = marker.title.take(13),
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickJumpChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SleekSurfaceVariant,
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = SleekTextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BasemapPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SleekPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else SleekTextMuted
        )
    }
}

@Composable
fun MapFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) SleekPrimary else SleekSurfaceVariant,
        border = BorderStroke(1.dp, if (isSelected) SleekPrimary else SleekBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else SleekTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun TimeStepLabel(label: String, surge: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) Color.White else SleekTextMuted
        )
        Text(
            text = surge,
            fontSize = 8.sp,
            color = if (isActive) Color(0xFF38BDF8) else SleekTextMuted.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MarkerStat(label: String, value: String) {
    Column {
        Text(text = label, color = SleekTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = SleekTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = SleekTextSecondary,
            fontSize = 12.sp
        )
    }
}
