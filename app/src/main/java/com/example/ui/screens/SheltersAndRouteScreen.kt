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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.ShelterEntity
import com.example.data.service.SafeRouteOption
import com.example.ui.theme.RiskExtremeRed
import com.example.ui.theme.RiskLowGreen
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
fun SheltersAndRouteScreen(
    shelters: List<ShelterEntity>,
    routeOptions: List<SafeRouteOption>,
    selectedShelter: ShelterEntity?,
    onSelectShelter: (ShelterEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableIntStateOf(if (selectedShelter != null) 1 else 0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBg)
    ) {
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = SleekSurface,
            contentColor = SleekPrimary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    color = SleekPrimary,
                    height = 3.dp
                )
            },
            divider = {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SleekBorder))
            }
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = {
                    Text(
                        "Safe Shelters (${shelters.size})",
                        fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeSubTab == 0) SleekPrimary else SleekTextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_shelters")
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = {
                    Text(
                        "Safe Routing",
                        fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeSubTab == 1) SleekPrimary else SleekTextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_safe_routing")
            )
        }

        if (activeSubTab == 0) {
            ShelterListContent(
                shelters = shelters,
                onGetDirections = { shelter ->
                    onSelectShelter(shelter)
                    activeSubTab = 1
                }
            )
        } else {
            SafeRouteContent(
                routes = routeOptions,
                destinationShelterName = selectedShelter?.name ?: "St. Jude Elevated Community Center"
            )
        }
    }
}

@Composable
fun ShelterListContent(
    shelters: List<ShelterEntity>,
    onGetDirections: (ShelterEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var callingShelter by remember { mutableStateOf<ShelterEntity?>(null) }

    val filterOptions = listOf("All", "🏥 Medical", "🟢 Safe Ground", "🍲 Food & Water", "🐕 Pet Friendly")

    val filteredShelters = shelters.filter { shelter ->
        val matchesSearch = searchQuery.isBlank() || 
            shelter.name.contains(searchQuery, ignoreCase = true) ||
            shelter.facilities.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "🏥 Medical" -> shelter.facilities.contains("Medical", ignoreCase = true) || shelter.facilities.contains("First Aid", ignoreCase = true)
            "🟢 Safe Ground" -> shelter.riskLevel == "LOW"
            "🍲 Food & Water" -> shelter.facilities.contains("Food", ignoreCase = true) || shelter.facilities.contains("Water", ignoreCase = true) || shelter.facilities.contains("Kitchen", ignoreCase = true)
            "🐕 Pet Friendly" -> shelter.facilities.contains("Pet", ignoreCase = true) || shelter.capacityTotal > 600
            else -> true
        }

        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search shelters by name or facility...", fontSize = 13.sp, color = SleekTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SleekPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SleekTextMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SleekSurface,
                    unfocusedContainerColor = SleekSurface,
                    focusedBorderColor = SleekPrimary,
                    unfocusedBorderColor = SleekBorder,
                    focusedTextColor = SleekTextPrimary,
                    unfocusedTextColor = SleekTextPrimary
                ),
                singleLine = true
            )
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SleekPrimary else SleekSurface,
                        border = BorderStroke(1.dp, if (isSelected) SleekPrimary else SleekBorder),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SleekTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ELEVATED EMERGENCY SHELTERS (${filteredShelters.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMuted,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Sorted by proximity",
                    fontSize = 10.sp,
                    color = SleekTextMuted
                )
            }
        }

        if (filteredShelters.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SleekSurface,
                    border = BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching shelters found", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SleekTextPrimary)
                        Text("Try resetting search filters or keywords", fontSize = 12.sp, color = SleekTextMuted)
                    }
                }
            }
        }

        items(filteredShelters) { shelter ->
            val isSafe = shelter.riskLevel == "LOW"

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SleekBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shelter_item_${shelter.id}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSafe) Color(0xFFDCFCE7) else SleekCriticalRedBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isSafe) "🏠" else "⚠️", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = shelter.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextPrimary
                                )
                                Text(
                                    text = "${shelter.distanceKm} km away • Safe Elevated Ground",
                                    fontSize = 11.sp,
                                    color = SleekTextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSafe) Color(0xFFDCFCE7) else SleekCriticalRedBg,
                            border = BorderStroke(
                                1.dp,
                                if (isSafe) RiskLowGreen.copy(alpha = 0.3f) else SleekCriticalRed.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = if (isSafe) "🟢 SAFE GROUND" else "🔴 WATER RISING",
                                color = if (isSafe) RiskLowGreen else SleekCriticalRedDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Capacity indicator
                    val occupancyRatio = shelter.capacityOccupied.toFloat() / shelter.capacityTotal.toFloat()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Occupancy: ${shelter.capacityOccupied} / ${shelter.capacityTotal} persons",
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )
                        Text(
                            text = "${(occupancyRatio * 100).toInt()}% Full",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (occupancyRatio > 0.85f) SleekCriticalRed else SleekTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { occupancyRatio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (occupancyRatio > 0.85f) SleekCriticalRed else SleekPrimary,
                        trackColor = SleekBorder
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Facilities tag
                    Text(
                        text = "Facilities: ${shelter.facilities}",
                        fontSize = 11.sp,
                        color = SleekTextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onGetDirections(shelter) },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Safe Route", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { callingShelter = shelter },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, SleekBorder),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = SleekTextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                        }
                    }
                }
            }
        }
    }

    if (callingShelter != null) {
        ShelterContactDialog(
            shelter = callingShelter!!,
            onDismiss = { callingShelter = null }
        )
    }
}

@Composable
fun ShelterContactDialog(
    shelter: ShelterEntity,
    onDismiss: () -> Unit
) {
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
                        Text("📞", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SHELTER COORDINATOR",
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
                    text = shelter.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                )

                Text(
                    text = "Contact: ${shelter.contactPhone}",
                    fontSize = 13.sp,
                    color = SleekPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "High Ground Structure • Available Capacity: ${shelter.capacityTotal - shelter.capacityOccupied} slots",
                    fontSize = 11.sp,
                    color = SleekTextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call ${shelter.contactPhone}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SafeRouteContent(
    routes: List<SafeRouteOption>,
    destinationShelterName: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SleekSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FLOOD-AWARE SAFE ROUTING ALGORITHM",
                        color = SleekPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SAFEST ROUTE > SHORTEST ROUTE",
                        color = SleekTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Destination: $destinationShelterName",
                        color = SleekTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        items(routes) { route ->
            val isRecommended = route.isRecommended

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (isRecommended) Color(0xFFF0FDF4) else SleekCriticalRedBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isRecommended) RiskLowGreen.copy(alpha = 0.4f) else SleekCriticalRed.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("route_card_${route.routeId}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = route.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecommended) Color(0xFF14532D) else SleekCriticalRedDark
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isRecommended) RiskLowGreen else SleekCriticalRed
                        ) {
                            Text(
                                text = if (isRecommended) "✓ RECOMMENDED" else "⚠️ AVOID",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text("Distance", fontSize = 10.sp, color = SleekTextMuted)
                            Text("${route.distanceKm} km", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                        }
                        Column {
                            Text("Est. Travel Time", fontSize = 10.sp, color = SleekTextMuted)
                            Text("${route.estimatedMinutes} mins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                        }
                        Column {
                            Text("Elevation Profile", fontSize = 10.sp, color = SleekTextMuted)
                            Text(route.elevationProfile, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = route.rationale,
                        fontSize = 11.sp,
                        color = if (isRecommended) Color(0xFF166534) else SleekCriticalRedDark,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Waypoints list
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        route.waypoints.forEachIndexed { idx, point ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${idx + 1}.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${point.hazardDesc} (${point.elevationM}m elev.)",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
