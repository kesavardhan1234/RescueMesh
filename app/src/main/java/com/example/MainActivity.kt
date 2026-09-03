package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmergencyHeader
import com.example.ui.localization.StringsProvider
import com.example.ui.dialogs.DemoPanelDialog
import com.example.ui.dialogs.ReportFloodDialog
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveFloodMapScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RescueDashboardScreen
import com.example.ui.screens.SheltersAndRouteScreen
import com.example.ui.screens.SosScreen
import com.example.ui.theme.FloodBlueDark
import com.example.ui.theme.FloodBlueLight
import com.example.ui.theme.FloodBluePrimary
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCriticalRed
import com.example.ui.theme.SleekCriticalRedBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.viewmodel.FloodViewModel

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String,
    val badgeCount: Int = 0
)

class MainActivity : ComponentActivity() {

    private val viewModel: FloodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: FloodViewModel) {
    val currentRole by viewModel.appRole.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val riskAnalysis by viewModel.riskAnalysis.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()

    val allSosRequests by viewModel.allSosRequests.collectAsState()
    val activeSos by viewModel.activeSos.collectAsState()
    val floodReports by viewModel.floodReports.collectAsState()
    val shelters by viewModel.shelters.collectAsState()
    val activeWarnings by viewModel.activeWarnings.collectAsState()
    val mapZones by viewModel.mapZones.collectAsState()
    val rescueTeams by viewModel.rescueTeams.collectAsState()

    val selectedZone by viewModel.selectedZoneForDetail.collectAsState()
    val selectedShelter by viewModel.selectedShelterForRoute.collectAsState()
    val routeOptions by viewModel.routeOptions.collectAsState()

    val isReportDialogOpen by viewModel.isReportFloodDialogOpen.collectAsState()
    val isDemoPanelOpen by viewModel.isDemoControlPanelOpen.collectAsState()
    val demoState by viewModel.demoState.collectAsState()
    val currentLanguage by viewModel.appLanguage.collectAsState()

    val navItems = listOf(
        BottomNavItem(StringsProvider.get("nav_home", currentLanguage), Icons.Default.Home, "nav_home"),
        BottomNavItem(StringsProvider.get("nav_map", currentLanguage), Icons.Default.Map, "nav_map"),
        BottomNavItem(StringsProvider.get("nav_alerts", currentLanguage), Icons.Default.Campaign, "nav_alerts", badgeCount = activeWarnings.size),
        BottomNavItem(StringsProvider.get("nav_sos", currentLanguage), Icons.Default.Emergency, "nav_sos", badgeCount = if (activeSos != null && activeSos?.rescueStatus != "CLOSED") 1 else 0),
        BottomNavItem(StringsProvider.get("nav_shelters", currentLanguage), Icons.Default.HomeWork, "nav_shelters"),
        BottomNavItem(StringsProvider.get("nav_profile", currentLanguage), Icons.Default.Person, "nav_profile")
    )

    Scaffold(
        topBar = {
            EmergencyHeader(
                currentRole = currentRole,
                onRoleChange = { viewModel.setAppRole(it) },
                networkStatus = networkStatus,
                onToggleOfflineMode = { viewModel.setSimulatedOfflineMode(it) },
                onOpenDemoPanel = { viewModel.toggleDemoControlPanel(true) },
                currentLanguage = currentLanguage,
                onLanguageChange = { viewModel.setLanguage(it) }
            )
        },
        bottomBar = {
            if (currentRole == "CITIZEN") {
                NavigationBar(
                    containerColor = SleekSurface,
                    contentColor = SleekTextPrimary,
                    tonalElevation = 2.dp,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = SleekBorder,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        val isSos = index == 3

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(index) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.badgeCount > 0) {
                                            Badge(
                                                containerColor = SleekCriticalRed,
                                                contentColor = Color.White
                                            ) {
                                                Text("${item.badgeCount}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (isSos) SleekCriticalRed else if (isSelected) SleekPrimary else SleekTextMuted,
                                        modifier = Modifier.size(if (isSos) 24.dp else 22.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSos) SleekCriticalRed else if (isSelected) SleekPrimary else SleekTextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = if (isSos) SleekCriticalRedBg else SleekPrimaryContainer
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentRole == "RESCUE_TEAM") {
                // Rescue Authority Dashboard Screen
                RescueDashboardScreen(
                    sosRequests = allSosRequests,
                    rescueTeams = rescueTeams,
                    floodReports = floodReports,
                    onAssignTeam = { sosId, teamId -> viewModel.assignRescueTeam(sosId, teamId) },
                    onAdvanceStatus = { sosId, status -> viewModel.advanceRescueStatus(sosId, status) },
                    onVerifyReport = { reportId -> viewModel.verifyReport(reportId) }
                )
            } else {
                // Citizen Screens
                when (selectedTab) {
                    0 -> HomeScreen(
                        userLocation = userLocation,
                        weather = weather,
                        risk = riskAnalysis,
                        activeWarnings = activeWarnings,
                        activeSos = activeSos,
                        networkStatus = networkStatus,
                        language = currentLanguage,
                        onNavigateToMap = { viewModel.selectTab(1) },
                        onNavigateToShelters = {
                            viewModel.selectShelterForRoute(null)
                            viewModel.selectTab(4)
                        },
                        onNavigateToSafeRoute = {
                            viewModel.selectTab(4)
                        },
                        onNavigateToAlerts = { viewModel.selectTab(2) },
                        onNavigateToSos = { viewModel.selectTab(3) },
                        onOpenReportFlood = { viewModel.openReportFloodDialog(true) },
                        onTriggerSos = { count, desc -> viewModel.triggerSos(count, desc) }
                    )
                    1 -> LiveFloodMapScreen(
                        userLocation = userLocation,
                        mapZones = mapZones,
                        shelters = shelters,
                        selectedZone = selectedZone,
                        onSelectZone = { viewModel.selectZoneForDetail(it) }
                    )
                    2 -> AlertsScreen(
                        risk = riskAnalysis,
                        warnings = activeWarnings
                    )
                    3 -> SosScreen(
                        activeSos = activeSos,
                        userLocation = userLocation,
                        risk = riskAnalysis,
                        networkStatus = networkStatus,
                        onTriggerSos = { count, desc -> viewModel.triggerSos(count, desc) },
                        onStopSos = { sosId -> viewModel.stopSos(sosId) }
                    )
                    4 -> SheltersAndRouteScreen(
                        shelters = shelters,
                        routeOptions = routeOptions,
                        selectedShelter = selectedShelter,
                        onSelectShelter = { shelter -> viewModel.selectShelterForRoute(shelter) }
                    )
                    5 -> ProfileScreen()
                }
            }

            // Report Flood Dialog
            if (isReportDialogOpen) {
                ReportFloodDialog(
                    userLocation = userLocation,
                    onDismiss = { viewModel.openReportFloodDialog(false) },
                    onSubmitReport = { cat, waterM, desc ->
                        viewModel.submitFloodReport(cat, waterM, desc)
                    }
                )
            }

            // Demo Scenario Controller Dialog
            if (isDemoPanelOpen) {
                DemoPanelDialog(
                    demoState = demoState,
                    onNextStep = { viewModel.nextDemoStep() },
                    onPrevStep = { viewModel.prevDemoStep() },
                    onGoToStep = { viewModel.goToDemoStep(it) },
                    onToggleAutoPlay = { viewModel.toggleAutoPlay() },
                    onDismiss = { viewModel.toggleDemoControlPanel(false) }
                )
            }
        }
    }
}
