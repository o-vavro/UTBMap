package com.atlasstudio.utbmap.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OfficeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOffice(office: Office)

    @Query("SELECT DISTINCT * FROM office_table WHERE id LIKE :id")
    fun getOffice(id: String): LiveData<Office?>?

    @Query("SELECT DISTINCT * FROM office_table WHERE locationLngMin <= :lng AND locationLngMax >= :lng AND locationLatMin <= :lat AND locationLatMax >= :lat")
    fun getOffice(lat: Double, lng: Double): LiveData<Office?>?

    @Query("SELECT DISTINCT * FROM office_table WHERE favourite LIKE :favourite")
    fun getFavouriteOffices(favourite: Boolean): Flow<List<Office?>>

    @Query("SELECT DISTINCT * FROM office_table WHERE locationLngMin <= :locationLngMax AND locationLngMax >= :locationLngMin AND locationLatMin <= :locationLatMax AND locationLatMax >= :locationLatMin")
    fun getIntersectingOffices(locationLatMin: Double, locationLatMax: Double, locationLngMin: Double, locationLngMax: Double): LiveData<List<Office?>?>?

    @Query("SELECT DISTINCT * FROM office_table")
    fun getAllOffices(): LiveData<List<Office?>?>?

    /*@Delete
    suspend fun deleteOffice(office: Office)

    @Query("DELETE FROM office_table")
    suspend fun deleteAllOffices()*/
}