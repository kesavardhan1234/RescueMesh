package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.EarlyWarningEntity
import com.example.model.RiskAnalysis
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.RiskHighOrange
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

@Composable
fun AlertsScreen(
    risk: RiskAnalysis,
    warnings: List<EarlyWarningEntity>,
    modifier: Modifier = Modifier
) {
    var selectedLevelFilter by remember { mutableStateOf("All") }
    val levelFilters = listOf("All", "EXTREME", "HIGH", "MODERATE")

    val filteredWarnings = warnings.filter { warn ->
        when (selectedLevelFilter) {
            "EXTREME" -> warn.warningLevel.contains("EXTREME", ignoreCase = true)
            "HIGH" -> warn.warningLevel.contains("HIGH", ignoreCase = true)
            "MODERATE" -> warn.warningLevel.contains("MODERATE", ignoreCase = true)
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "EARLY WARNING SYSTEM & FORECAST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 0.8.sp
            )
        }

        // Hydrological Forecast Chart & Inundation Timeline
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SleekSurface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "6-HOUR INUNDATION FORECAST",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekPrimary,
                            letterSpacing = 0.4.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SleekSurfaceVariant
                        ) {
                            Text(
                                text = "NWP + Radar Hybrid",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextMuted,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timeline simulation bars: +1h, +2h, +3h, +4h, +6h
                    val timeline = listOf(
                        Triple("+1h", 65, RiskModerateYellow),
                        Triple("+2h", 88, SleekCriticalRed),
                        Triple("+3h", 94, SleekCriticalRed),
                        Triple("+4h", 78, RiskHighOrange),
                        Triple("+6h", 45, SleekPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        timeline.forEach { (hour, prob, color) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$prob%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height((prob * 0.85).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = hour,
                                    fontSize = 11.sp,
                                    color = SleekTextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Peak inundation risk expected between +2h and +3h due to canal overflow and upstream dam discharge.",
                            fontSize = 11.sp,
                            color = SleekTextSecondary,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Active Warning Bulletins Header with filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE EMERGENCY BULLETINS (${filteredWarnings.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMuted,
                    letterSpacing = 0.8.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(levelFilters) { filter ->
                        val isSelected = selectedLevelFilter == filter
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SleekPrimary else SleekSurface,
                            border = BorderStroke(1.dp, if (isSelected) SleekPrimary else SleekBorder),
                            modifier = Modifier.clickable { selectedLevelFilter = filter }
                        ) {
                            Text(
                                text = filter,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else SleekTextPrimary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        items(filteredWarnings) { warn ->
            val isExtreme = warn.warningLevel == "EXTREME"

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (isExtreme) SleekCriticalRedBg else Color(0xFFFFFBEB),
                border = BorderStroke(
                    1.dp,
                    if (isExtreme) SleekCriticalRed.copy(alpha = 0.4f) else RiskHighOrange.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bulletin_item_${warn.id}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = warn.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExtreme) SleekCriticalRedDark else Color(0xFF9A3412)
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isExtreme) SleekCriticalRed else RiskHighOrange
                        ) {
                            Text(
                                text = warn.warningLevel,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Target Zone: ${warn.locationName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )
                    Text(
                        text = "Expected Rain: ${warn.expectedRainfallMm} mm • Timeline: ${warn.expectedPeriod}",
                        fontSize = 11.sp,
                        color = SleekTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = warn.message,
                        fontSize = 12.sp,
                        color = SleekTextPrimary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "RECOMMENDED CITIZEN ACTIONS:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExtreme) SleekCriticalRedDark else Color(0xFF78350F)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = warn.recommendedActions,
                                fontSize = 11.sp,
                                color = SleekTextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Citizen Flood Survival Guide
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurface,
                border = BorderStroke(1.dp, SleekBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CITIZEN FLOOD SURVIVAL GUIDE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // DO's
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("✅ DO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF15803D))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• Move to upper floors immediately\n• Keep phone battery charged\n• Turn off main electrical breaker\n• Follow verified NDMA radio alerts", fontSize = 10.sp, color = Color(0xFF166534), lineHeight = 14.sp)
                            }
                        }

                        // DON'Ts
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("❌ DON'T", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFB91C1C))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• Don't walk in moving flood waters\n• Don't drive through submerged roads\n• Don't touch exposed electrical wires\n• Don't consume tap/ground water", fontSize = 10.sp, color = Color(0xFF991B1B), lineHeight = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

