package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.service.NetworkStatus
import com.example.ui.theme.LoRaGreen
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCriticalRed
import com.example.ui.theme.SleekCriticalRedBg
import com.example.ui.theme.SleekCriticalRedDark
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmergencyHeader(
    currentRole: String,
    onRoleChange: (String) -> Unit,
    networkStatus: NetworkStatus,
    onToggleOfflineMode: (Boolean) -> Unit,
    onOpenDemoPanel: () -> Unit,
    currentLanguage: com.example.ui.localization.AppLanguage = com.example.ui.localization.AppLanguage.ENGLISH,
    onLanguageChange: (com.example.ui.localization.AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_spin")
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    val isOffline = networkStatus.isSimulatedOfflineMode

    Surface(
        color = SleekSurface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sleek Brand Logo & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SleekPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(spinRotation)
                                .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FLOODGUARD",
                                color = SleekTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI",
                                color = SleekPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Life-Critical Flood Rescue • SIH26071",
                            color = SleekTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Language Switcher Pills
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            listOf(
                                com.example.ui.localization.AppLanguage.ENGLISH to "EN",
                                com.example.ui.localization.AppLanguage.TELUGU to "తె",
                                com.example.ui.localization.AppLanguage.HINDI to "हि"
                            ).forEach { (lang, label) ->
                                val isSelected = currentLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SleekPrimary else Color.Transparent)
                                        .clickable { onLanguageChange(lang) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
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
                        }
                    }

                    // Demo Controller Shortcut Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SleekSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier
                            .clickable { onOpenDemoPanel() }
                            .testTag("demo_control_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Demo Mode",
                                tint = SleekPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "DEMO",
                                color = SleekPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Role Switcher & Network Status Indicator Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Selector Segmented Container
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekSurfaceVariant)
                        .padding(2.dp)
                ) {
                    RolePill(
                        label = "👤 Citizen",
                        isSelected = currentRole == "CITIZEN",
                        onClick = { onRoleChange("CITIZEN") },
                        testTag = "role_citizen"
                    )
                    RolePill(
                        label = "🚑 Rescue Command",
                        isSelected = currentRole == "RESCUE_TEAM",
                        onClick = { onRoleChange("RESCUE_TEAM") },
                        testTag = "role_rescue"
                    )
                }

                // Highly Visible Network Status Badge (Connected / LoRa Mesh / No Network)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isOffline) SleekCriticalRedBg else SleekPrimaryContainer,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isOffline) SleekCriticalRed.copy(alpha = 0.4f) else SleekPrimary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .clickable { onToggleOfflineMode(!isOffline) }
                        .testTag("network_status_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) SleekCriticalRed else SleekPrimary)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isOffline) "🔴 NO MOBILE NET (LoRa Mesh)" else "📡 CONNECTED (4G)",
                            color = if (isOffline) SleekCriticalRedDark else SleekPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RolePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SleekPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else SleekTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun HoldForSosButton(
    onTriggerSos: () -> Unit,
    networkStatus: NetworkStatus? = null,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var holdJob by remember { mutableStateOf<Job?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_pulse"
    )

    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_ring"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer Glowing Container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            // Soft red blur shadow behind button
            Box(
                modifier = Modifier
                    .size(126.dp)
                    .scale(if (isHolding) 1.15f else pulseScale)
                    .clip(CircleShape)
                    .background(SleekCriticalRed.copy(alpha = if (isHolding) 0.35f else 0.15f))
            )

            // Main White Sleek Button with spinning accent ring
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(8.dp, Color(0xFFF1F5F9)),
                modifier = Modifier
                    .size(116.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHolding = true
                                progress = 0f
                                holdJob = scope.launch {
                                    val steps = 30
                                    for (i in 1..steps) {
                                        delay(40) // ~1.2 seconds hold time
                                        progress = i / steps.toFloat()
                                    }
                                    progress = 1f
                                    onTriggerSos()
                                }
                                tryAwaitRelease()
                                isHolding = false
                                holdJob?.cancel()
                                progress = 0f
                            }
                        )
                    }
                    .testTag("hold_for_sos_button")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Continuous subtle rotating red ring or progress indicator
                    if (isHolding) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(96.dp),
                            color = SleekCriticalRed,
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round,
                            trackColor = Color(0xFFF1F5F9)
                        )
                    } else {
                        // Ambient ring
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .rotate(spinAngle)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            SleekCriticalRed.copy(alpha = 0.8f),
                                            Color.Transparent,
                                            SleekCriticalRed.copy(alpha = 0.8f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🆘",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHolding) "HOLDING..." else "HOLD SOS",
                            color = SleekCriticalRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Monospace Network Telemetry Status
        val isOffline = networkStatus?.isSimulatedOfflineMode ?: false
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isOffline) SleekCriticalRed else SleekPrimary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isOffline) "NETWORK: LORA MESH (OFFLINE FALLBACK)" else "NETWORK: 4G LTE CONNECTED",
                color = SleekTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

