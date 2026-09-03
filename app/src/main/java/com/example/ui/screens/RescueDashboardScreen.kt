package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.FloodReportEntity
import com.example.data.local.SosRequestEntity
import com.example.data.service.RescueTeam
import com.example.model.RescueStatus
import com.example.ui.theme.FloodBluePrimary
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskModerateYellow

@Composable
fun RescueDashboardScreen(
    sosRequests: List<SosRequestEntity>,
    rescueTeams: List<RescueTeam>,
    floodReports: List<FloodReportEntity>,
    onAssignTeam: (Long, String) -> Unit,
    onAdvanceStatus: (Long, RescueStatus) -> Unit,
    onVerifyReport: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: SOS Triage, 1: Rescue Fleet, 2: Community Reports

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("rescue_dashboard_screen")
    ) {
        // Authority Header Badge
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NDRF / SDMA DISASTER COMMAND",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "${sosRequests.filter { it.rescueStatus != "CLOSED" }.size} Active SOS",
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = FloodBluePrimary
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("🚨 SOS Triage", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("rescue_tab_sos")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("🚤 Rescue Fleet", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("rescue_tab_fleet")
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("📢 Reports", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("rescue_tab_reports")
            )
        }

        when (activeTab) {
            0 -> SosTriageQueue(
                sosRequests = sosRequests,
                rescueTeams = rescueTeams,
                onAssignTeam = onAssignTeam,
                onAdvanceStatus = onAdvanceStatus
            )
            1 -> RescueFleetList(rescueTeams = rescueTeams)
            2 -> CommunityReportsList(reports = floodReports, onVerifyReport = onVerifyReport)
        }
    }
}

@Composable
fun SosTriageQueue(
    sosRequests: List<SosRequestEntity>,
    rescueTeams: List<RescueTeam>,
    onAssignTeam: (Long, String) -> Unit,
    onAdvanceStatus: (Long, RescueStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "PRIORITIZED EMERGENCY SOS QUEUE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )
        }

        if (sosRequests.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No pending SOS requests at this time.", color = Color(0xFF64748B))
                    }
                }
            }
        }

        items(sosRequests) { sos ->
            val isCritical = sos.riskLevel == "EXTREME" || sos.peopleCount >= 3
            val isClosed = sos.rescueStatus == "CLOSED"

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isClosed) Color(0xFFE2E8F0) else if (isCritical) RiskExtremeRed else RiskHighOrange,
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("sos_triage_item_${sos.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCritical) RiskExtremeRed else RiskHighOrange
                            ) {
                                Text(
                                    text = if (isCritical) "🔴 CRITICAL TRIAGE" else "🟠 HIGH PRIORITY",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SOS #${sos.id}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Via ${sos.commChannel}",
                                color = FloodBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Victim / User: ${sos.userName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "GPS: ${"%.5f".format(sos.latitude)}, ${"%.5f".format(sos.longitude)} (Accuracy: ±${sos.gpsAccuracyMeters.toInt()}m)",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "👥 Group: ${sos.peopleCount} persons • 🔋 Battery: ${sos.batteryPercent}% • ⚠️ Risk: ${sos.riskLevel}",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${sos.emergencyDescription}\"",
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Current Status & Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ${sos.rescueStatus}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = when (sos.rescueStatus) {
                                "RESCUED" -> RiskLowGreen
                                "EN_ROUTE" -> FloodBluePrimary
                                "ASSIGNED" -> RiskModerateYellow
                                "WAITING" -> RiskExtremeRed
                                else -> Color(0xFF64748B)
                            }
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (sos.rescueStatus == "WAITING") {
                                Button(
                                    onClick = { onAssignTeam(sos.id, "RT_01") },
                                    colors = ButtonDefaults.buttonColors(containerColor = FloodBluePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Assign Alpha", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (sos.rescueStatus == "ASSIGNED") {
                                Button(
                                    onClick = { onAdvanceStatus(sos.id, RescueStatus.EN_ROUTE) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Dispatch En Route", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (sos.rescueStatus == "EN_ROUTE") {
                                Button(
                                    onClick = { onAdvanceStatus(sos.id, RescueStatus.RESCUED) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RiskLowGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Mark Rescued", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (sos.rescueStatus == "RESCUED") {
                                OutlinedButton(
                                    onClick = { onAdvanceStatus(sos.id, RescueStatus.CLOSED) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Close Ticket", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RescueFleetList(rescueTeams: List<RescueTeam>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ACTIVE RESCUE FLEET UNITS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )
        }

        items(rescueTeams) { team ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚤", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = team.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Vehicle: ${team.vehicleType}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (team.status) {
                                "AVAILABLE" -> Color(0xFFDCFCE7)
                                "EN_ROUTE" -> Color(0xFFE0F2FE)
                                else -> Color(0xFFFEF3C7)
                            }
                        ) {
                            Text(
                                text = team.status,
                                color = when (team.status) {
                                    "AVAILABLE" -> RiskLowGreen
                                    "EN_ROUTE" -> FloodBluePrimary
                                    else -> Color(0xFFB45309)
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Equipment: ${team.equipment}",
                        fontSize = 11.sp,
                        color = Color(0xFF334155)
                    )
                    Text(
                        text = "Base Location: ${"%.4f".format(team.latitude)}, ${"%.4f".format(team.longitude)} • Contact: ${team.contactPhone}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityReportsList(
    reports: List<FloodReportEntity>,
    onVerifyReport: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "CITIZEN CROWDSOURCED FLOOD REPORTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.8.sp
            )
        }

        items(reports) { report ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Report #${report.id} • ${report.reportCategory}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        if (report.isVerifiedByAuthority) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = RiskLowGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("VERIFIED", color = RiskLowGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = { onVerifyReport(report.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = FloodBluePrimary),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Verify", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Water Level: ${report.waterLevelMeters} meters",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RiskHighOrange
                    )

                    Text(
                        text = report.description,
                        fontSize = 12.sp,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reporter: ${report.userName} • GPS: ${"%.4f".format(report.latitude)}, ${"%.4f".format(report.longitude)}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
