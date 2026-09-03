package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SosDao {
    @Query("SELECT * FROM sos_requests ORDER BY timestamp DESC")
    fun getAllSosRequests(): Flow<List<SosRequestEntity>>

    @Query("SELECT * FROM sos_requests WHERE id = :id")
    fun getSosRequestById(id: Long): Flow<SosRequestEntity?>

    @Query("SELECT * FROM sos_requests WHERE rescueStatus != 'CLOSED' ORDER BY timestamp DESC LIMIT 1")
    fun getActiveSosRequest(): Flow<SosRequestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSosRequest(sos: SosRequestEntity): Long

    @Update
    suspend fun updateSosRequest(sos: SosRequestEntity)

    @Query("UPDATE sos_requests SET rescueStatus = :status WHERE id = :id")
    suspend fun updateRescueStatus(id: Long, status: String)

    @Query("UPDATE sos_requests SET deliveryStatus = :deliveryStatus, commChannel = :commChannel WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Long, deliveryStatus: String, commChannel: String)

    @Query("UPDATE sos_requests SET isLocationSharingActive = :isActive, lastLocationUpdate = :timestamp, latitude = :lat, longitude = :lon, gpsAccuracyMeters = :accuracy WHERE id = :id")
    suspend fun updateLocationSharing(id: Long, isActive: Boolean, lat: Double, lon: Double, accuracy: Float, timestamp: Long)

    @Query("DELETE FROM sos_requests WHERE id = :id")
    suspend fun deleteSosRequest(id: Long)

    @Query("DELETE FROM sos_requests")
    suspend fun clearAll()
}

@Dao
interface FloodReportDao {
    @Query("SELECT * FROM flood_reports ORDER BY timestamp DESC")
    fun getAllFloodReports(): Flow<List<FloodReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFloodReport(report: FloodReportEntity): Long

    @Query("UPDATE flood_reports SET isVerifiedByAuthority = :verified WHERE id = :id")
    suspend fun verifyReport(id: Long, verified: Boolean)

    @Query("DELETE FROM flood_reports")
    suspend fun clearAll()
}

@Dao
interface ShelterDao {
    @Query("SELECT * FROM shelters ORDER BY distanceKm ASC")
    fun getAllShelters(): Flow<List<ShelterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelters(shelters: List<ShelterEntity>)

    @Query("DELETE FROM shelters")
    suspend fun clearAll()
}

@Dao
interface EarlyWarningDao {
    @Query("SELECT * FROM early_warnings WHERE isActive = 1 ORDER BY timestamp DESC")
    fun getActiveWarnings(): Flow<List<EarlyWarningEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarnings(warnings: List<EarlyWarningEntity>)

    @Query("DELETE FROM early_warnings")
    suspend fun clearAll()
}

@Dao
interface MapZoneDao {
    @Query("SELECT * FROM map_zones")
    fun getAllMapZones(): Flow<List<MapZoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapZones(zones: List<MapZoneEntity>)

    @Query("DELETE FROM map_zones")
    suspend fun clearAll()
}
