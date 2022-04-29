package com.atlasstudio.utbmap.data

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

@Dao
interface TouchedLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTouchedLocation(location: TouchedLocation)

    @Query("SELECT DISTINCT * FROM touched_location_table WHERE location LIKE :location")
    fun getTouchedLocation(location: LatLng): Flow<TouchedLocation>

    @Query("SELECT DISTINCT * FROM touched_location_table")
    fun getTouchedLocations(): Flow<List<TouchedLocation>>

    @Delete
    suspend fun deleteTouchedLocation(location: TouchedLocation?)

    @Query("DELETE FROM touched_location_table WHERE location LIKE :location AND officeId LIKE :officeId")
    suspend fun deleteTouchedLocation(location: LatLng, officeId: String)

    @Query("DELETE FROM touched_location_table")
    suspend fun deleteAllTouchedLocations()
}