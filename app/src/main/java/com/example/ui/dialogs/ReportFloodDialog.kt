package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.UserGpsLocation
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary

@Composable
fun ReportFloodDialog(
    userLocation: UserGpsLocation,
    onDismiss: () -> Unit,
    onSubmitReport: (String, Double, String) -> Unit
) {
    val categories = listOf(
        "🌊 Flooded Area",
        "🚧 Road Blocked",
        "🌉 Bridge Damage",
        "🏢 Building Inundation",
        "🌳 Fallen Tree",
        "⚠️ Live Wire / Hazard"
    )

    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var waterLevelM by remember { mutableFloatStateOf(0.8f) }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SleekSurface,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, SleekBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("report_flood_dialog")
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📢", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "REPORT FLOOD HAZARD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SleekTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // GPS Location Stamp
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekSurfaceVariant,
                    border = BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GPS: ${"%.4f".format(userLocation.latitude)}, ${"%.4f".format(userLocation.longitude)} • ${userLocation.locationName}",
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hazard Category
                Text("Select Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SleekPrimary else SleekSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) SleekPrimary else SleekBorder
                            ),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else SleekTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Water Level Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Water Depth", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted, letterSpacing = 0.5.sp)
                    Text("${"%.1f".format(waterLevelM)} meters", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RiskHighOrange)
                }

                Slider(
                    value = waterLevelM,
                    onValueChange = { waterLevelM = it },
                    valueRange = 0.1f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = SleekPrimary, activeTrackColor = SleekPrimary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                Text("Situation Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("e.g. Water reached 1m near bridge, road impassable...", fontSize = 12.sp, color = SleekTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SleekPrimary,
                        unfocusedBorderColor = SleekBorder,
                        focusedTextColor = SleekTextPrimary,
                        unfocusedTextColor = SleekTextPrimary
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        val cleanCat = selectedCategory.replace(Regex("[^A-Za-z0-9 ]"), "").trim()
                        onSubmitReport(cleanCat, (waterLevelM * 10).toInt() / 10.0, description.ifBlank { "Water accumulation observed at location." })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_flood_report_button")
                ) {
                    Text("Submit Flood Report", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}
