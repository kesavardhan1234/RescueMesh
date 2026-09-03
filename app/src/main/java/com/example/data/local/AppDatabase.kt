package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SosRequestEntity::class,
        FloodReportEntity::class,
        ShelterEntity::class,
        EarlyWarningEntity::class,
        MapZoneEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sosDao(): SosDao
    abstract fun floodReportDao(): FloodReportDao
    abstract fun shelterDao(): ShelterDao
    abstract fun earlyWarningDao(): EarlyWarningDao
    abstract fun mapZoneDao(): MapZoneDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "floodguard_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(database: AppDatabase) {
    // Seed Shelters
    val shelters = listOf(
        ShelterEntity(
            name = "St. Jude Elevated Community Center",
            latitude = 16.5120,
            longitude = 80.6480,
            distanceKm = 2.4,
            capacityTotal = 450,
            capacityOccupied = 180,
            riskLevel = "LOW",
            facilities = "Medical Station, Potable Water, 50kW Generator, High Ground (El. 32m), Dry Rations",
            isRecommended = true,
            contactPhone = "+91 94401 55678"
        ),
        ShelterEntity(
            name = "Government High Ground College Hall",
            latitude = 16.5250,
            longitude = 80.6350,
            distanceKm = 3.8,
            capacityTotal = 600,
            capacityOccupied = 240,
            riskLevel = "LOW",
            facilities = "Doctor on site, Heli-Pad Access, Food Distribution, Clean Water Filter",
            isRecommended = true,
            contactPhone = "+91 98852 33411"
        ),
        ShelterEntity(
            name = "Riverside Municipal Indoor Stadium",
            latitude = 16.4980,
            longitude = 80.6220,
            distanceKm = 1.2,
            capacityTotal = 300,
            capacityOccupied = 285,
            riskLevel = "HIGH",
            facilities = "Limited Power, Water rising in lower perimeter, Evacuation in progress",
            isRecommended = false,
            contactPhone = "+91 91234 56789"
        ),
        ShelterEntity(
            name = "Apex Hillview Primary School",
            latitude = 16.5380,
            longitude = 80.6620,
            distanceKm = 4.6,
            capacityTotal = 350,
            capacityOccupied = 95,
            riskLevel = "LOW",
            facilities = "Solar Power, Baby Care, Medical Kits, Emergency LoRa Relay Station",
            isRecommended = true,
            contactPhone = "+91 94901 88722"
        )
    )
    database.shelterDao().insertShelters(shelters)

    // Seed Map Zones
    val zones = listOf(
        MapZoneEntity(
            zoneCode = "ZONE 01",
            name = "North Ridge Highland",
            centerLat = 16.5350,
            centerLon = 80.6550,
            riskLevel = "LOW",
            expectedRainfallMm = 24.0,
            inundationProbability = 12,
            forecastPeriod = "Next 6 Hours",
            elevationMeters = 38.5,
            statusSummary = "Well-drained terrain. Normal traffic flow."
        ),
        MapZoneEntity(
            zoneCode = "ZONE 02",
            name = "Central Market Sector",
            centerLat = 16.5150,
            centerLon = 80.6400,
            riskLevel = "MODERATE",
            expectedRainfallMm = 52.0,
            inundationProbability = 45,
            forecastPeriod = "Next 3-4 Hours",
            elevationMeters = 18.2,
            statusSummary = "Stormwater channels near 70% capacity. Minor waterlogging."
        ),
        MapZoneEntity(
            zoneCode = "ZONE 03",
            name = "Eastern Canal Basin",
            centerLat = 16.5050,
            centerLon = 80.6580,
            riskLevel = "HIGH",
            expectedRainfallMm = 88.0,
            inundationProbability = 78,
            forecastPeriod = "Next 2 Hours",
            elevationMeters = 11.4,
            statusSummary = "Canal overtopping expected. Ground floor inundation risk."
        ),
        MapZoneEntity(
            zoneCode = "ZONE 04",
            name = "Lowland Riverbank Settlement",
            centerLat = 16.4950,
            centerLon = 80.6280,
            riskLevel = "EXTREME",
            expectedRainfallMm = 115.0,
            inundationProbability = 94,
            forecastPeriod = "Next 1-2 Hours",
            elevationMeters = 6.8,
            statusSummary = "Flash flooding imminent. Immediate evacuation recommended."
        )
    )
    database.mapZoneDao().insertMapZones(zones)

    // Seed Early Warnings
    val warnings = listOf(
        EarlyWarningEntity(
            title = "🔴 EXTREME FLOOD WARNING - ZONE 04 & BASIN",
            warningLevel = "EXTREME",
            locationName = "Lowland Riverbank & Canal Sub-Districts",
            expectedRainfallMm = 115.0,
            expectedPeriod = "Next 1–2 hours",
            inundationProbability = 94,
            message = "Extremely heavy rainfall upstream and dam release may cause severe urban inundation up to 1.8m depth.",
            recommendedActions = "• Move immediately to higher ground or designated shelter.\n• Avoid all low-lying bridges, underpasses, and basements.\n• Disconnect electrical mains before water enters premises.\n• Keep emergency grab-bag ready with drinking water and medications.",
            timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
            isActive = true
        ),
        EarlyWarningEntity(
            title = "🟠 HIGH FLOOD ALERT - CENTRAL CORRIDOR",
            warningLevel = "ALERT",
            locationName = "Central Market & Transit Circle",
            expectedRainfallMm = 78.0,
            expectedPeriod = "Next 3 hours",
            inundationProbability = 72,
            message = "Rapid stormwater accumulation expected along arterial roads. Significant traffic disruptions.",
            recommendedActions = "• Do not drive through flooded roads (Turn Around, Don't Drown).\n• Park vehicles on elevated multi-level structures.\n• Stay tuned to emergency broadcasts.",
            timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
            isActive = true
        )
    )
    database.earlyWarningDao().insertWarnings(warnings)

    // Seed Sample Flood Reports
    val initialReports = listOf(
        FloodReportEntity(
            userId = "citizen_99",
            userName = "Ravi Shankar",
            latitude = 16.4985,
            longitude = 80.6295,
            reportCategory = "WATER_LEVEL",
            waterLevelMeters = 1.4,
            description = "Water reaching knee height at Old Bridge Junction. Vehicles stalled.",
            timestamp = System.currentTimeMillis() - 25 * 60 * 1000,
            isVerifiedByAuthority = true
        ),
        FloodReportEntity(
            userId = "citizen_42",
            userName = "Priya Sharma",
            latitude = 16.5020,
            longitude = 80.6340,
            reportCategory = "ROAD_BLOCKED",
            waterLevelMeters = 0.8,
            description = "Fallen banyan tree blocking drainage culvert and Main Road.",
            timestamp = System.currentTimeMillis() - 40 * 60 * 1000,
            isVerifiedByAuthority = true
        )
    )
    for (r in initialReports) {
        database.floodReportDao().insertFloodReport(r)
    }

    // Seed an initial SOS for the Rescue Dashboard demonstration
    val sampleSos = SosRequestEntity(
        userId = "citizen_1024",
        userName = "Ananya Patel",
        latitude = 16.4960,
        longitude = 80.6275,
        peopleCount = 3,
        riskLevel = "EXTREME",
        batteryPercent = 38,
        emergencyDescription = "Water entered ground floor, 1 elderly person and child with us. Trapped on 1st floor balcony.",
        timestamp = System.currentTimeMillis() - 10 * 60 * 1000,
        deliveryStatus = "DELIVERED",
        commChannel = "EMERGENCY_GATEWAY",
        rescueStatus = "WAITING",
        assignedTeamName = "",
        assignedTeamEta = "",
        isLocationSharingActive = true,
        gpsAccuracyMeters = 8.5f
    )
    database.sosDao().insertSosRequest(sampleSos)
}
