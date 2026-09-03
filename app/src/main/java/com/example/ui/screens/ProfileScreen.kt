package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary

import com.example.ui.localization.AppLanguage

@Composable
fun ProfileScreen(
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageChange: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sirenAudioEnabled by remember { mutableStateOf(true) }
    var highPrecisionGps by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "CITIZEN IDENTITY & RESCUE PREFERENCES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 0.8.sp
            )
        }

        // Profile Identity Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurface,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SleekPrimary, SleekPrimaryDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Aarav Sharma",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                        Text(
                            text = "+91 98480 12345 • Ward 12, Riverbank",
                            fontSize = 12.sp,
                            color = SleekTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SleekPrimary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Citizen Verification Active", fontSize = 11.sp, color = SleekPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Emergency Contacts
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurface,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContactPhone, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMERGENCY HELPLINE DIRECTORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMuted,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ContactRow(title = "National Disaster Helpline", number = "1078 (Toll Free)")
                    ContactRow(title = "State Disaster Management (SDMA)", number = "1070")
                    ContactRow(title = "NDRF Control Room", number = "+91 97110 77372")
                    ContactRow(title = "Local Municipal Flood Control", number = "+91 94401 22334")
                }
            }
        }

        // Language & Accessibility
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurface,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REGIONAL LANGUAGE / भाषा",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMuted,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            AppLanguage.ENGLISH to "English",
                            AppLanguage.TELUGU to "తెలుగు",
                            AppLanguage.HINDI to "हिन्दी"
                        ).forEach { (lang, label) ->
                            val isSelected = currentLanguage == lang
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) SleekPrimary else SleekSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SleekPrimary else SleekBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onLanguageChange(lang) }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else SleekTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Emergency Alert Toggles
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurface,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAFETY PERMISSIONS & ALERTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMuted,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Extreme Siren Sound", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                            Text("Overrides silent mode during Flash Flood Warning", fontSize = 11.sp, color = SleekTextSecondary)
                        }
                        Switch(
                            checked = sirenAudioEnabled,
                            onCheckedChange = { sirenAudioEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SleekPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Continuous GPS Sharing on SOS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                            Text("Sends coordinates to NDRF during active rescue", fontSize = 11.sp, color = SleekTextSecondary)
                        }
                        Switch(
                            checked = highPrecisionGps,
                            onCheckedChange = { highPrecisionGps = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SleekPrimary)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ContactRow(title: String, number: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = SleekTextSecondary)
        Text(text = number, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
    }
}
