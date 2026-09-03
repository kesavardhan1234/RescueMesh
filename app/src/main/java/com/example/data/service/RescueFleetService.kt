package com.example.data.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RescueTeam(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val status: String, // "AVAILABLE", "ASSIGNED", "EN_ROUTE", "REACHED", "COMPLETED"
    val assignedSosId: Long? = null,
    val contactPhone: String,
    val equipment: String,
    val vehicleType: String // "Inflatable Boat", "High-Clearance 4x4", "Amphibious ATV"
)

class RescueFleetService {
    private val _teams = MutableStateFlow(
        listOf(
            RescueTeam(
                id = "RT_01",
                name = "NDRF Unit Alpha (Water Rescue)",
                latitude = 16.5180,
                longitude = 80.6390,
                distanceKm = 2.1,
                status = "AVAILABLE",
                contactPhone = "+91 98480 11223",
                equipment = "2x Motorized Zodiac Boats, Medical Trauma Kit, Satellite Beacon, Life Vests (x12)",
                vehicleType = "Motorized Zodiac Boat"
            ),
            RescueTeam(
                id = "RT_02",
                name = "SDRF Task Force Bravo",
                latitude = 16.5320,
                longitude = 80.6510,
                distanceKm = 4.8,
                status = "AVAILABLE",
                contactPhone = "+91 98480 44556",
                equipment = "High-Clearance 4x4 Unimog, Winch, LoRa Base Station, Inflatable Rafts",
                vehicleType = "High-Clearance 4x4"
            ),
            RescueTeam(
                id = "RT_03",
                name = "Civil Defense Quick Response Charlie",
                latitude = 16.5450,
                longitude = 80.6650,
                distanceKm = 7.2,
                status = "AVAILABLE",
                contactPhone = "+91 98480 77889",
                equipment = "Amphibious ATV, Drone Surveillance, Thermal Imaging, First Aid Kits",
                vehicleType = "Amphibious ATV"
            )
        )
    )
    val teams: StateFlow<List<RescueTeam>> = _teams.asStateFlow()

    fun assignTeamToSos(teamId: String, sosId: Long): RescueTeam? {
        val list = _teams.value.map { team ->
            if (team.id == teamId) {
                team.copy(status = "EN_ROUTE", assignedSosId = sosId)
            } else team
        }
        _teams.value = list
        return list.firstOrNull { it.id == teamId }
    }

    fun updateTeamStatus(teamId: String, status: String) {
        val list = _teams.value.map { team ->
            if (team.id == teamId) {
                team.copy(status = status)
            } else team
        }
        _teams.value = list
    }
}
