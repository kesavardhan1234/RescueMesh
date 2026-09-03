package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCriticalRed
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.viewmodel.DemoScenarioState

@Composable
fun DemoPanelDialog(
    demoState: DemoScenarioState,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onGoToStep: (Int) -> Unit,
    onToggleAutoPlay: () -> Unit,
    onDismiss: () -> Unit
) {
    val stepTitles = listOf(
        "1. Baseline Weather",
        "2. Rain Increases",
        "3. AI Inundation Alert",
        "4. Warning Broadcast",
        "5. GIS Flood Map",
        "6. Safe Routing",
        "7. SOS Generated",
        "8. GPS Live Beacon",
        "9. 4G/Cellular Blackout",
        "10. LoRa/Gateway Failover",
        "11. Rescue Command Triage",
        "12. Team Alpha Dispatched",
        "13. Safe Responders Route",
        "14. Victims Rescued & Closed"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SleekSurface,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, SleekBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("demo_panel_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎮", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HACKATHON DEMO ENGINE",
                                color = SleekTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "SIH26071 Simulation Controller",
                                color = SleekTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Active Step Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SleekSurfaceVariant,
                    border = BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STEP ${demoState.currentStep} OF 14",
                                color = SleekPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (demoState.isAutoPlaying) SleekPrimary else SleekBorder
                            ) {
                                Text(
                                    text = if (demoState.isAutoPlaying) "● AUTO-RUNNING" else "MANUAL",
                                    color = if (demoState.isAutoPlaying) Color.White else SleekTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = demoState.stepTitle,
                            color = SleekTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = demoState.stepDescription,
                            color = SleekTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Playback Control Buttons: Prev, AutoPlay/Pause, Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrevStep,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = null, tint = SleekTextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prev", color = SleekTextPrimary, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onToggleAutoPlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (demoState.isAutoPlaying) SleekCriticalRed else SleekPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(
                            imageVector = if (demoState.isAutoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (demoState.isAutoPlaying) "Pause" else "Auto-Play", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = onNextStep,
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.FastForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "JUMP DIRECTLY TO ANY STEP (1 TO 14)",
                    color = SleekTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Direct Step Selection List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(stepTitles.size) { index ->
                        val stepNum = index + 1
                        val isCurrent = demoState.currentStep == stepNum

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) SleekPrimary else SleekSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) SleekPrimary else SleekBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGoToStep(stepNum) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrent) Color.White else SleekBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$stepNum",
                                        color = if (isCurrent) SleekPrimary else SleekTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stepTitles[index],
                                    color = if (isCurrent) Color.White else SleekTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
