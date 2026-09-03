package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SosRequestEntity
import com.example.data.service.NetworkStatus
import com.example.model.RescueStatus
import com.example.model.RiskAnalysis
import com.example.model.UserGpsLocation
import com.example.ui.components.HoldForSosButton
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskModerateYellow
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCriticalRed
import com.example.ui.theme.SleekCriticalRedBg
import com.example.ui.theme.SleekCriticalRedDark
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SosScreen(
    activeSos: SosRequestEntity?,
    userLocation: UserGpsLocation,
    risk: RiskAnalysis,
    networkStatus: NetworkStatus,
    onTriggerSos: (Int, String) -> Unit,
    onStopSos: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var peopleCount by remember { mutableIntStateOf(3) }
    var emergencyDesc by remember { mutableStateOf("Water entered ground floor, trapped on balcony. Need immediate boat evacuation.") }

    val hasActiveSos = activeSos != null && activeSos.rescueStatus != "CLOSED"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (hasActiveSos) {
            // --- 1. ACTIVE SOS DASHBOARD & LIVE GPS LOCATION SHARING ---
            item {
                ActiveSosTrackingCard(
                    sos = activeSos!!,
                    onStopSos = { onStopSos(activeSos.id) }
                )
            }

            // --- 2. RESCUE UNIT TIMELINE STATUS ---
            item {
                RescueStatusTimelineCard(sos = activeSos!!)
            }

            // --- 3. TRANSMISSION PACKET INSPECTOR ---
            item {
                SosPacketCard(
                    sos = activeSos!!,
                    userLocation = userLocation,
                    networkStatus = networkStatus
                )
            }

            // --- 4. NO-NETWORK EMERGENCY GATEWAY PROTOCOL EXPLANATION ---
            item {
                NoNetworkArchitectureCard(networkStatus = networkStatus)
            }
        } else {
            // --- INACTIVE / PRE-TRIGGER SOS CONFIGURATION SCREEN ---
            item {
                SosConfigHeaderCard(
                    userLocation = userLocation,
                    risk = risk,
                    networkStatus = networkStatus
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = SleekSurface,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "EMERGENCY DETAILS FOR RESCUE TEAM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMuted,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // People Count Selector
                        Text(
                            text = "Number of People Needing Evacuation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(1, 2, 3, 4, 5, 8).forEach { count ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (peopleCount == count) SleekPrimary else SleekSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (peopleCount == count) SleekPrimary else SleekBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { peopleCount = count }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count",
                                            color = if (peopleCount == count) Color.White else SleekTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Description
                        Text(
                            text = "Situation Description (Optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = emergencyDesc,
                            onValueChange = { emergencyDesc = it },
                            placeholder = { Text("e.g., Water rising rapidly, elderly/infant present...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sos_description_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SleekPrimary,
                                unfocusedBorderColor = SleekBorder,
                                focusedContainerColor = SleekSurfaceVariant,
                                unfocusedContainerColor = SleekSurfaceVariant
                            ),
                            maxLines = 3
                        )
                    }
                }
            }

            // HOLD FOR SOS BUTTON
            item {
                Spacer(modifier = Modifier.height(6.dp))
                HoldForSosButton(
                    onTriggerSos = {
                        onTriggerSos(peopleCount, emergencyDesc)
                    },
                    networkStatus = networkStatus
                )
            }

            // Architecture details
            item {
                NoNetworkArchitectureCard(networkStatus = networkStatus)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActiveSosTrackingCard(
    sos: SosRequestEntity,
    onStopSos: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_alpha"
    )

    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val lastUpdateStr = timeFormatter.format(Date(sos.lastLocationUpdate))

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SleekCriticalRed,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_sos_tracking_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOCATION SHARING ACTIVE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "SOS #${sos.id}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "GPS Coordinates: ${"%.5f".format(sos.latitude)}, ${"%.5f".format(sos.longitude)}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Last Update: $lastUpdateStr",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
                Text(
                    text = "Accuracy: ±${sos.gpsAccuracyMeters.toInt()} m",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stop SOS Button
            Button(
                onClick = onStopSos,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = SleekCriticalRed),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("stop_sos_button")
            ) {
                Icon(Icons.Default.StopCircle, contentDescription = "Stop", modifier = Modifier.size(18.dp), tint = SleekCriticalRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "STOP SOS / CANCEL TRACKING",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SleekCriticalRed
                )
            }
        }
    }
}

@Composable
fun RescueStatusTimelineCard(sos: SosRequestEntity) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "RESCUE DISPATCH PROGRESS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            val currentStatus = try {
                RescueStatus.valueOf(sos.rescueStatus)
            } catch (e: Exception) {
                RescueStatus.WAITING
            }

            // Status Timeline Steps: WAITING -> ASSIGNED -> EN ROUTE -> REACHED -> RESCUED
            val steps = listOf(RescueStatus.WAITING, RescueStatus.ASSIGNED, RescueStatus.EN_ROUTE, RescueStatus.REACHED, RescueStatus.RESCUED)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, step ->
                    val isDone = currentStatus.stepIndex >= step.stepIndex
                    val isCurrent = currentStatus == step

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDone) SleekPrimary else SleekBorder
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("${index + 1}", fontSize = 11.sp, color = SleekTextMuted, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.label,
                            fontSize = 9.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) SleekPrimary else SleekTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Assigned Unit Card snippet
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SleekSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚑", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (sos.assignedTeamName.isNotBlank()) "Unit: ${sos.assignedTeamName}" else "Unit: NDRF Motorized Zodiac Alpha",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                        Text(
                            text = "Status: ${sos.rescueStatus} • ETA: Available when route is clear (~10 mins)",
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SosPacketCard(
    sos: SosRequestEntity,
    userLocation: UserGpsLocation,
    networkStatus: NetworkStatus
) {
    val (statusColor, statusBg) = when (sos.deliveryStatus) {
        "DELIVERED" -> Pair(RiskLowGreen, Color(0xFFDCFCE7))
        "SENDING" -> Pair(RiskModerateYellow, Color(0xFFFEF9C3))
        "RETRYING" -> Pair(RiskHighOrange, Color(0xFFFFEDD5))
        else -> Pair(SleekCriticalRed, SleekCriticalRedBg)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
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
                Text(
                    text = "TRANSMITTED EMERGENCY PACKET",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMuted,
                    letterSpacing = 0.8.sp
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = "● ${sos.deliveryStatus}",
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Formatted Emergency Packet Monospace box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SleekSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = """
                        SOS ID   : ${sos.id}
                        LAT      : ${"%.6f".format(sos.latitude)}
                        LON      : ${"%.6f".format(sos.longitude)}
                        PEOPLE   : ${sos.peopleCount}
                        RISK     : ${sos.riskLevel}
                        BATTERY  : ${sos.batteryPercent}%
                        CHANNEL  : ${sos.commChannel}
                        TIME     : ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(sos.timestamp))}
                        """.trimIndent(),
                        color = SleekTextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SosConfigHeaderCard(
    userLocation: UserGpsLocation,
    risk: RiskAnalysis,
    networkStatus: NetworkStatus
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
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
                Text(
                    text = "EMERGENCY SOS SYSTEM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMuted,
                    letterSpacing = 0.8.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (networkStatus.isSimulatedOfflineMode) SleekCriticalRedBg else SleekSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (networkStatus.isSimulatedOfflineMode) SleekCriticalRed.copy(alpha = 0.3f) else SleekBorder
                    )
                ) {
                    Text(
                        text = if (networkStatus.isSimulatedOfflineMode) "📡 LoRa Mesh Active" else "🌐 5G Ready",
                        color = if (networkStatus.isSimulatedOfflineMode) SleekCriticalRedDark else SleekPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Instant Flood Distress Beacon",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Transmits GPS coordinates, inundation level, and group count to NDRF / SDRF Command and nearest emergency responders.",
                fontSize = 12.sp,
                color = SleekTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun NoNetworkArchitectureCard(networkStatus: NetworkStatus) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SleekSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Router, contentDescription = "Gateway", tint = SleekPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NO-NETWORK EMERGENCY PROTOCOL",
                    color = SleekPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = """
                Priority Routing Pipeline:
                1. 🌐 Internet (4G/5G) [${if (networkStatus.isInternetAvailable) "ONLINE" else "OFFLINE"}]
                2. 📱 Cellular SMS [${if (networkStatus.isCellularAvailable) "READY" else "OFFLINE"}]
                3. 📡 Local LoRa / Bluetooth Mesh [ONLINE - 4 Nodes]
                4. 🌉 Emergency Gateway (ESP32 Bridge) [ONLINE]
                5. 🛰️ Satellite Backhaul via Gateway [LINKED]
                """.trimIndent(),
                color = SleekTextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Important: Standard smartphones communicate with local LoRa/BLE mesh nodes. The Emergency Gateway bridges local packets to satellite/cellular backhaul.",
                color = SleekTextMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}
