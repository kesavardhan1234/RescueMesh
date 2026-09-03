package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.EarlyWarningEntity
import com.example.data.local.SosRequestEntity
import com.example.model.FloodRiskLevel
import com.example.model.RiskAnalysis
import com.example.model.UserGpsLocation
import com.example.model.WeatherInfo
import com.example.ui.components.HoldForSosButton
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.RiskExtremeRedBg
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskHighOrangeBg
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskLowGreenBg
import com.example.ui.theme.RiskModerateYellow
import com.example.ui.theme.RiskModerateYellowBg
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCriticalRed
import com.example.ui.theme.SleekCriticalRedBg
import com.example.ui.theme.SleekCriticalRedDark
import com.example.ui.theme.SleekOnPrimaryContainer
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary

import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsProvider
import com.example.data.service.NetworkStatus

@Composable
fun HomeScreen(
    userLocation: UserGpsLocation,
    weather: WeatherInfo,
    risk: RiskAnalysis,
    activeWarnings: List<EarlyWarningEntity>,
    activeSos: SosRequestEntity?,
    networkStatus: NetworkStatus = NetworkStatus(),
    language: AppLanguage = AppLanguage.ENGLISH,
    onNavigateToMap: () -> Unit,
    onNavigateToShelters: () -> Unit,
    onNavigateToSafeRoute: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToSos: () -> Unit,
    onOpenReportFlood: () -> Unit,
    onTriggerSos: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFlashlightModal by remember { mutableStateOf(false) }
    var showImSafeModal by remember { mutableStateOf(false) }
    var showHelplineModal by remember { mutableStateOf(false) }
    var isSirenActive by remember { mutableStateOf(false) }

    // Interactive Evacuation Checklist State
    val checklistState = remember {
        mutableStateMapOf(
            "💧 Drinking Water (3L/person)" to true,
            "🔋 Charged Power Bank & Cable" to true,
            "📄 IDs & Documents Waterproof Pouch" to false,
            "🩹 First Aid & Prescription Medicines" to true,
            "🔦 Torch, Whistle & Batteries" to false,
            "🥪 Dry Rations & Emergency Snacks" to false
        )
    }

    val isEmergencyMode = risk.riskLevel == FloodRiskLevel.EXTREME || (activeSos != null && activeSos.rescueStatus != "CLOSED")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(2.dp))
        }

        // 1. Dynamic Status Badge (🟢 YOU ARE SAFE / 🟡 CAUTION / 🔴 MOVE TO SAFETY / 🚨 RESCUE ACTIVE)
        item {
            EmergencyStatusBadge(
                risk = risk,
                activeSos = activeSos,
                language = language
            )
        }

        // 2. High-Priority Emergency Mode Hero Card (if in critical flood risk or active SOS)
        if (isEmergencyMode) {
            item {
                EmergencyModeHeroCard(
                    userLocation = userLocation,
                    risk = risk,
                    activeSos = activeSos,
                    language = language,
                    onNavigateToSafeRoute = onNavigateToSafeRoute,
                    onTriggerSos = onTriggerSos,
                    onNavigateToSos = onNavigateToSos
                )
            }
        }

        // Active SOS Banner (if emergency is active and not in standalone emergency mode)
        if (activeSos != null && activeSos.rescueStatus != "CLOSED" && !isEmergencyMode) {
            item {
                ActiveSosBannerCard(
                    sos = activeSos,
                    onClick = onNavigateToSos
                )
            }
        }

        // 3. 📍 WHERE AM I? - Sleek Location Card with GPS accuracy
        item {
            LocationCard(userLocation = userLocation)
        }

        // 4. ⚠️ AM I IN DANGER? - High-Impact Flood Risk Hero Card
        item {
            FloodRiskCard(
                risk = risk,
                onViewFullAnalysis = onNavigateToAlerts
            )
        }

        // 5. 🏠 WHERE SHOULD I GO? - Nearest Safe Haven Card with direct route trigger
        item {
            NearestSafeHavenCard(
                userLocation = userLocation,
                language = language,
                onNavigateToShelters = onNavigateToShelters,
                onNavigateToSafeRoute = onNavigateToSafeRoute
            )
        }

        // 6. 🆘 HOW DO I GET HELP? - Primary Emergency SOS Trigger Hero Card
        item {
            HomeEmergencySosHeroCard(
                userLocation = userLocation,
                activeSos = activeSos,
                onTriggerSos = onTriggerSos,
                onNavigateToSos = onNavigateToSos
            )
        }

        // 7. Actionable Guidance - What to do immediately
        item {
            WhatToDoImmediatelyCard(language = language)
        }

        // 8. Current Active Warning Card (if warning is active)
        val highestWarning = activeWarnings.firstOrNull()
        if (highestWarning != null) {
            item {
                WarningAlertCard(
                    warning = highestWarning,
                    onViewAllAlerts = onNavigateToAlerts
                )
            }
        }

        // 9. Sleek Emergency Actions 2x2 Grid (Flood Map, Safe Route, Shelters, Report Flood)
        item {
            Text(
                text = "EMERGENCY ACTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            ActionGrid(
                onNavigateToMap = onNavigateToMap,
                onNavigateToSafeRoute = onNavigateToSafeRoute,
                onNavigateToShelters = onNavigateToShelters,
                onOpenReportFlood = onOpenReportFlood
            )
        }

        // 10. Sleek Weather & Telemetry Summary
        item {
            WeatherSummaryCard(weather = weather)
        }

        // 11. User-Friendly Quick Safety Tools
        item {
            Text(
                text = "QUICK SAFETY UTILITIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            QuickSafetyToolsBar(
                isSirenActive = isSirenActive,
                onToggleSiren = { isSirenActive = !isSirenActive },
                onOpenFlashlight = { showFlashlightModal = true },
                onOpenImSafe = { showImSafeModal = true },
                onOpenHelplines = { showHelplineModal = true }
            )
        }

        // 12. Interactive Evacuation Preparedness Checklist
        item {
            PreparednessChecklistCard(
                items = checklistState,
                onToggleItem = { itemKey ->
                    checklistState[itemKey] = !(checklistState[itemKey] ?: false)
                }
            )
        }

        // 13. Hold for SOS Button (2.5s hold duration with progress)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            HoldForSosButton(
                onTriggerSos = {
                    onTriggerSos(3, "Emergency: Flood water rising. Immediate assistance required.")
                },
                networkStatus = networkStatus
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- DIALOGS & USER-FRIENDLY MODALS ---
    if (showFlashlightModal) {
        FlashlightBeaconDialog(onDismiss = { showFlashlightModal = false })
    }

    if (showImSafeModal) {
        ImSafeBroadcastDialog(
            userLocation = userLocation,
            onDismiss = { showImSafeModal = false }
        )
    }

    if (showHelplineModal) {
        QuickHelplineSpeedDialDialog(onDismiss = { showHelplineModal = false })
    }
}

@Composable
fun LocationCard(userLocation: UserGpsLocation) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SleekSurface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("location_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = SleekPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT LOCATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary,
                        letterSpacing = 0.6.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekPrimaryContainer
                    ) {
                        Text(
                            text = userLocation.zoneName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekOnPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userLocation.locationName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${"%.4f".format(userLocation.latitude)}°N, ${"%.4f".format(userLocation.longitude)}°E • ±${userLocation.accuracyMeters.toInt()}m accuracy",
                    fontSize = 11.sp,
                    color = SleekTextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FloodRiskCard(
    risk: RiskAnalysis,
    onViewFullAnalysis: () -> Unit
) {
    val isCritical = risk.riskLevel == FloodRiskLevel.EXTREME || risk.riskLevel == FloodRiskLevel.HIGH

    if (isCritical) {
        // Sleek High-Impact Warning Aesthetic matching Sleek Interface design
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = SleekCriticalRed,
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("flood_risk_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                SleekCriticalRed,
                                SleekCriticalRedDark
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI FLOOD RISK LEVEL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFDAD6),
                                letterSpacing = 1.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${risk.riskTrend} RISK ↗",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = risk.riskLevel.label.uppercase(),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "High inundation probability detected",
                                fontSize = 12.sp,
                                color = Color(0xFFFFDAD6)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${risk.inundationProbability}%",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Inundation",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFDAD6)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { (risk.inundationProbability / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Glassmorphism telemetry stat chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SleekGlassStat(
                            title = "Rainfall",
                            value = "${risk.currentRainfallMm.toInt()} mm/h",
                            modifier = Modifier.weight(1f)
                        )
                        SleekGlassStat(
                            title = "Forecast",
                            value = risk.forecastPeriod.take(9),
                            modifier = Modifier.weight(1f)
                        )
                        SleekGlassStat(
                            title = "Elevation",
                            value = "${risk.elevationMeters}m",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Explanation banner inside card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewFullAnalysis() }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = risk.aiExplanation,
                                fontSize = 11.sp,
                                color = Color(0xFFFFF1F1),
                                lineHeight = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Sleek Moderate/Low presentation
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = SleekSurface,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("flood_risk_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI FLOOD RISK ASSESSMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMuted,
                        letterSpacing = 0.8.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellowBg else RiskLowGreenBg
                    ) {
                        Text(
                            text = "${risk.riskTrend} RISK ↗",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellow else RiskLowGreen,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = risk.riskLevel.label,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellow else RiskLowGreen
                        )
                        Text(
                            text = "Condition normal in your sector",
                            fontSize = 12.sp,
                            color = SleekTextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellowBg else RiskLowGreenBg
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${risk.inundationProbability}%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellow else RiskLowGreen
                            )
                            Text(
                                text = "Prob.",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellow else RiskLowGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { (risk.inundationProbability / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (risk.riskLevel == FloodRiskLevel.MODERATE) RiskModerateYellow else RiskLowGreen,
                    trackColor = SleekBorder
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = risk.aiExplanation,
                            fontSize = 11.sp,
                            color = SleekTextSecondary,
                            lineHeight = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleekGlassStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = Color(0xFFFFDAD6), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun WarningAlertCard(
    warning: EarlyWarningEntity,
    onViewAllAlerts: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SleekCriticalRedBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCriticalRed.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewAllAlerts() }
            .testTag("warning_alert_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SleekCriticalRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚠️", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = warning.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekCriticalRedDark
                    )
                    Text(
                        text = "Window: ${warning.expectedPeriod}",
                        fontSize = 11.sp,
                        color = SleekCriticalRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = warning.message,
                fontSize = 12.sp,
                color = SleekTextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun WeatherSummaryCard(weather: WeatherInfo) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SleekSurface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌧️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE WEATHER & TELEMETRY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMuted,
                        letterSpacing = 0.8.sp
                    )
                }
                Text(
                    text = weather.condition,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherTile(
                    icon = Icons.Default.WaterDrop,
                    label = "Rainfall",
                    value = "${weather.rainfallMm} mm/h",
                    tint = SleekPrimary
                )
                WeatherTile(
                    icon = Icons.Default.Thermostat,
                    label = "Temp",
                    value = "${weather.temperatureCelsius}°C",
                    tint = Color(0xFFEA580C)
                )
                WeatherTile(
                    icon = Icons.Default.WaterDamage,
                    label = "Humidity",
                    value = "${weather.humidityPercent}%",
                    tint = Color(0xFF0284C7)
                )
                WeatherTile(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "${weather.windSpeedKmh} km/h",
                    tint = SleekTextSecondary
                )
            }
        }
    }
}

@Composable
fun WeatherTile(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
        Text(text = label, fontSize = 10.sp, color = SleekTextMuted)
    }
}

@Composable
fun ActionGrid(
    onNavigateToMap: () -> Unit,
    onNavigateToSafeRoute: () -> Unit,
    onNavigateToShelters: () -> Unit,
    onOpenReportFlood: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SleekActionCard(
                icon = "🗺️",
                title = "MAP GIS",
                subtitle = "Inundation zones",
                iconBg = Color(0xFFE0F2FE),
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_flood_map"),
                onClick = onNavigateToMap
            )
            SleekActionCard(
                icon = "🚗",
                title = "SAFE ROUTE",
                subtitle = "Flood-free path",
                iconBg = Color(0xFFDCFCE7),
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_safe_route"),
                onClick = onNavigateToSafeRoute
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SleekActionCard(
                icon = "🏠",
                title = "SHELTERS",
                subtitle = "Havens & capacity",
                iconBg = Color(0xFFEEF2FF),
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_find_shelter"),
                onClick = onNavigateToShelters
            )
            SleekActionCard(
                icon = "📢",
                title = "REPORT FLOOD",
                subtitle = "Submit hazard",
                iconBg = Color(0xFFFFEDD5),
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_report_flood"),
                onClick = onOpenReportFlood
            )
        }
    }
}

@Composable
fun SleekActionCard(
    icon: String,
    title: String,
    subtitle: String,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = SleekTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.3.sp
                )
                Text(
                    text = subtitle,
                    color = SleekTextMuted,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
fun ActiveSosBannerCard(
    sos: SosRequestEntity,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SleekCriticalRedDark,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("active_sos_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SleekCriticalRed),
                contentAlignment = Alignment.Center
            ) {
                Text("🆘", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ACTIVE SOS RESCUE BEACON",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
                Text(
                    text = "Status: ${sos.rescueStatus} • Channel: ${sos.commChannel}",
                    color = Color(0xFFFFDAD6),
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View SOS",
                tint = Color.White
            )
        }
    }
}

@Composable
fun HomeEmergencySosHeroCard(
    userLocation: UserGpsLocation,
    activeSos: SosRequestEntity?,
    onTriggerSos: (Int, String) -> Unit,
    onNavigateToSos: () -> Unit
) {
    var selectedSituation by remember { mutableStateOf("Trapped in Flood Water") }
    var selectedPeopleCount by remember { mutableIntStateOf(1) }

    val quickSituations = listOf(
        "🌊 Trapped in Water",
        "🏠 Trapped on Roof",
        "🚑 Medical Emergency",
        "👵 Elderly / Children",
        "🚗 Submerged Vehicle"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "sos_hero_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_hero_scale"
    )

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SleekSurface,
        border = BorderStroke(1.5.dp, SleekCriticalRed.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_emergency_sos_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SleekCriticalRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EMERGENCY RESCUE SOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekCriticalRed,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SleekCriticalRedBg,
                    border = BorderStroke(1.dp, SleekCriticalRed.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "GPS: ${String.format(java.util.Locale.US, "%.3f, %.3f", userLocation.latitude, userLocation.longitude)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekCriticalRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap or dispatch instant SOS with live GPS broadcast to NDRF & State Emergency Operations.",
                fontSize = 12.sp,
                color = SleekTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick situation selector pills
            Text(
                text = "SELECT SITUATION PRESET:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 0.6.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(quickSituations) { situation ->
                    val cleanText = situation.substring(situation.indexOf(' ') + 1)
                    val isSelected = selectedSituation == cleanText || selectedSituation == situation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SleekCriticalRed else SleekSurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) SleekCriticalRedDark else SleekBorder),
                        modifier = Modifier.clickable { selectedSituation = cleanText }
                    ) {
                        Text(
                            text = situation,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SleekTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large SOS Action Trigger Button
            Button(
                onClick = {
                    onTriggerSos(selectedPeopleCount, selectedSituation)
                    onNavigateToSos()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SleekCriticalRed),
                shape = RoundedCornerShape(18.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(pulseScale)
                    .testTag("home_trigger_sos_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🆘", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "DISPATCH EMERGENCY SOS NOW",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Link to Full SOS Hub & Helplines
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📡 Auto-relays via LoRa Mesh if offline",
                    fontSize = 10.sp,
                    color = SleekTextMuted
                )

                Text(
                    text = "Open Full SOS Hub →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToSos() }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
fun QuickSafetyToolsBar(
    isSirenActive: Boolean,
    onToggleSiren: () -> Unit,
    onOpenFlashlight: () -> Unit,
    onOpenImSafe: () -> Unit,
    onOpenHelplines: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickToolButton(
            icon = "🔦",
            label = "Screen Light",
            isActive = false,
            modifier = Modifier.weight(1f),
            onClick = onOpenFlashlight
        )
        QuickToolButton(
            icon = if (isSirenActive) "📢" else "🔔",
            label = if (isSirenActive) "Siren ON" else "SOS Siren",
            isActive = isSirenActive,
            modifier = Modifier.weight(1f),
            onClick = onToggleSiren
        )
        QuickToolButton(
            icon = "🛡️",
            label = "I'm Safe",
            isActive = false,
            modifier = Modifier.weight(1f),
            onClick = onOpenImSafe
        )
        QuickToolButton(
            icon = "☎️",
            label = "Helplines",
            isActive = false,
            modifier = Modifier.weight(1f),
            onClick = onOpenHelplines
        )
    }
}

@Composable
fun QuickToolButton(
    icon: String,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) SleekCriticalRed else SleekSurface,
        border = BorderStroke(1.dp, if (isActive) SleekCriticalRedDark else SleekBorder),
        shadowElevation = 1.dp,
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else SleekTextPrimary
            )
        }
    }
}

@Composable
fun PreparednessChecklistCard(
    items: Map<String, Boolean>,
    onToggleItem: (String) -> Unit
) {
    val completedCount = items.count { it.value }
    val totalCount = items.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val isAllReady = completedCount == totalCount

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurface,
        border = BorderStroke(1.dp, SleekBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎒", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FLOOD EVACUATION KIT CHECKLIST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMuted,
                        letterSpacing = 0.8.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAllReady) SleekPrimaryContainer else SleekSurfaceVariant
                ) {
                    Text(
                        text = "$completedCount/$totalCount Ready",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAllReady) SleekOnPrimaryContainer else SleekTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isAllReady) SleekPrimary else Color(0xFF38BDF8),
                trackColor = SleekBorder
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items.forEach { (itemText, isChecked) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleItem(itemText) }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleItem(itemText) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SleekPrimary,
                                uncheckedColor = SleekTextMuted,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = itemText,
                            fontSize = 12.sp,
                            fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal,
                            color = if (isChecked) SleekTextPrimary else SleekTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlashlightBeaconDialog(onDismiss: () -> Unit) {
    var isStrobeMode by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "strobe_transition")
    val strobeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe_alpha"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isStrobeMode) Color.White.copy(alpha = strobeAlpha) else Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RESCUE SCREEN BEACON",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        letterSpacing = 0.8.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF0F172A))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF08A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💡", fontSize = 50.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isStrobeMode) "High-Frequency Rescue Strobe Active" else "Maximum Brightness Screen Light",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "Point screen toward sky or rescue boats in the dark to signal your location.",
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { isStrobeMode = !isStrobeMode },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isStrobeMode) "Constant" else "Strobe SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Turn Off", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ImSafeBroadcastDialog(
    userLocation: UserGpsLocation,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val safeMessage = "Flood Safety Status: I am SAFE. My GPS Coordinates: ${userLocation.latitude}, ${userLocation.longitude} (${userLocation.locationName}, ${userLocation.zoneName}). Sent via FloodGuard AI."

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SleekSurface,
            border = BorderStroke(1.dp, SleekBorder),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I'M SAFE STATUS CHECK-IN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Notify your family and loved ones that you are safe with your current location stamp:",
                    fontSize = 12.sp,
                    color = SleekTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekSurfaceVariant,
                    border = BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = safeMessage,
                        fontSize = 12.sp,
                        color = SleekTextPrimary,
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(safeMessage))
                        copied = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (copied) SleekPrimary else Color(0xFF0284C7)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (copied) "Copied to Clipboard! Ready to Paste" else "Copy Status to Share (SMS / WhatsApp)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyStatusBadge(
    risk: RiskAnalysis,
    activeSos: SosRequestEntity?,
    language: AppLanguage
) {
    val hasActiveSos = activeSos != null && activeSos.rescueStatus != "CLOSED"
    val isExtreme = risk.riskLevel == FloodRiskLevel.EXTREME
    val isHigh = risk.riskLevel == FloodRiskLevel.HIGH
    val isModerate = risk.riskLevel == FloodRiskLevel.MODERATE

    val (bgColor, textColor, borderColor, icon, statusText) = when {
        hasActiveSos -> Quadruple(
            SleekCriticalRed,
            Color.White,
            SleekCriticalRedDark,
            "🚨",
            StringsProvider.get("rescue_active", language)
        )
        isExtreme -> Quadruple(
            SleekCriticalRed,
            Color.White,
            SleekCriticalRedDark,
            "🔴",
            StringsProvider.get("move_to_safety", language)
        )
        isHigh -> Quadruple(
            RiskHighOrangeBg,
            RiskHighOrange,
            RiskHighOrange.copy(alpha = 0.4f),
            "🟠",
            StringsProvider.get("move_to_safety", language)
        )
        isModerate -> Quadruple(
            RiskModerateYellowBg,
            RiskModerateYellow,
            RiskModerateYellow.copy(alpha = 0.4f),
            "🟡",
            StringsProvider.get("caution", language)
        )
        else -> Quadruple(
            RiskLowGreenBg,
            RiskLowGreen,
            RiskLowGreen.copy(alpha = 0.4f),
            "🟢",
            StringsProvider.get("you_are_safe", language)
        )
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_status_badge")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

@Composable
fun EmergencyModeHeroCard(
    userLocation: UserGpsLocation,
    risk: RiskAnalysis,
    activeSos: SosRequestEntity?,
    language: AppLanguage,
    onNavigateToSafeRoute: () -> Unit,
    onTriggerSos: (Int, String) -> Unit,
    onNavigateToSos: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = SleekCriticalRed,
        border = BorderStroke(2.dp, SleekCriticalRedDark),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_mode_hero_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(SleekCriticalRed, SleekCriticalRedDark)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚨 EMERGENCY MODE ACTIVE",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "PRIORITY 1",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Where am I?
            Text(
                text = "📍 YOU ARE IN A HIGH-RISK AREA",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "${userLocation.locationName} (${userLocation.zoneName})",
                color = Color(0xFFFFDAD6),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Where should I go?
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSafeRoute() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏠", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MOVE TO: Community Safe Haven",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Distance: 2.4 km • Safe Elevated Ground",
                            color = Color(0xFFFFDAD6),
                            fontSize = 11.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "ROUTE →",
                            color = SleekCriticalRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. How to get help (Hold for SOS)
            HoldForSosButton(
                onTriggerSos = {
                    onTriggerSos(3, "Emergency Mode Evacuation Request from ${userLocation.locationName}")
                    onNavigateToSos()
                }
            )
        }
    }
}

@Composable
fun NearestSafeHavenCard(
    userLocation: UserGpsLocation,
    language: AppLanguage,
    onNavigateToShelters: () -> Unit,
    onNavigateToSafeRoute: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurface,
        border = BorderStroke(1.dp, SleekBorder),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("nearest_safe_haven_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏠", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = StringsProvider.get("where_should_i_go", language),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMuted,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = StringsProvider.get("safe_shelter", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekTextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RiskLowGreenBg,
                    border = BorderStroke(1.dp, RiskLowGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "🟢 " + StringsProvider.get("low_risk", language),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RiskLowGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Community Hall & Relief Center • Sector 4",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
            )
            Text(
                text = "📍 Distance: 2.4 km away • 👥 ${StringsProvider.get("capacity_available", language)} (120 spots)",
                fontSize = 12.sp,
                color = SleekTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onNavigateToSafeRoute,
                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("get_safe_route_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = StringsProvider.get("get_safe_route", language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WhatToDoImmediatelyCard(language: AppLanguage) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurface,
        border = BorderStroke(1.dp, SleekBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚠️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = StringsProvider.get("what_to_do", language),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMuted,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SafetyBullet(number = "1", text = StringsProvider.get("action_1", language))
                SafetyBullet(number = "2", text = StringsProvider.get("action_2", language))
                SafetyBullet(number = "3", text = StringsProvider.get("action_3", language))
            }
        }
    }
}

@Composable
private fun SafetyBullet(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(SleekPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = SleekTextPrimary,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickHelplineSpeedDialDialog(onDismiss: () -> Unit) {
    val helplines = listOf(
        Triple("NDRF Disaster Helpline", "1078", "National Response Force • Toll Free"),
        Triple("State Disaster Management", "1070", "SDMA Control Room"),
        Triple("Medical & Ambulance", "108", "Emergency Patient Evacuation"),
        Triple("Police Emergency", "100 / 112", "Law & Order Assistance"),
        Triple("Fire & Flood Rescue", "101", "Boat and Inundation Services")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SleekSurface,
            border = BorderStroke(1.dp, SleekBorder),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("☎️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMERGENCY SPEED DIAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    helplines.forEach { (name, number, desc) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SleekSurfaceVariant,
                            border = BorderStroke(1.dp, SleekBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                                    Text(text = desc, fontSize = 10.sp, color = SleekTextMuted)
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SleekPrimary
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

