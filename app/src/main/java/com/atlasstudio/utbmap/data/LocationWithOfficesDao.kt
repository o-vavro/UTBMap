package com.atlasstudio.utbmap.data

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationWithOfficesDao {
    @Transaction
    @Query("SELECT DISTINCT * FROM touched_location_table WHERE location LIKE :loc")
    fun getTouchedLocationWithOffices(loc: LatLng): Flow<LocationWithOffices>

    @Query("SELECT DISTINCT * FROM location_office_cross_ref WHERE officeId LIKE :id")
    fun getCrossRefsForOffice(id: String): Flow<List<LocationOfficeCrossRef>>

    @Query("SELECT DISTINCT * FROM location_office_cross_ref WHERE LocationId LIKE :id")
    fun getCrossRefsForLocation(id: LatLng): Flow<List<LocationOfficeCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocationOfficeCrossRef(locationOfficeCrossRef: LocationOfficeCrossRef)

    @Query("DELETE FROM location_office_cross_ref WHERE locationId LIKE :locationId AND officeId LIKE :officeId")
    suspend fun deleteLocationOfficeCrossRef(locationId: LatLngBounds, officeId: String)
}