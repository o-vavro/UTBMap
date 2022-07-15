package com.atlasstudio.utbmap.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OfficeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOffice(office: Office)

    @Query("SELECT DISTINCT * FROM office_table WHERE id LIKE :id")
    fun getOffice(id: String): Flow<Office?>?

    /*@RawQuery(observedEntities = [Office::class])
    fun getOffice(query: SupportSQLiteQuery): PagingSource<Int, Office>*/
    @Query("SELECT DISTINCT * FROM office_table WHERE bds_locationLngMin <= :lng AND bds_locationLngMax >= :lng AND bds_locationLatMin <= :lat AND bds_locationLatMax >= :lat AND zIndex == :zIndex")
    fun getOffice(lat: Double, lng: Double, zIndex: Float): Flow<Office?>?

    @Query("SELECT DISTINCT * FROM office_table WHERE favourite LIKE :favourite")
    fun getFavouriteOffices(favourite: Boolean): Flow<List<Office?>>

    @Query("SELECT DISTINCT * FROM office_table WHERE bds_locationLngMin <= :locationLngMax AND bds_locationLngMax >= :locationLngMin AND bds_locationLatMin <= :locationLatMax AND bds_locationLatMax >= :locationLatMin")
    fun getIntersectingOffices(locationLatMin: Double, locationLatMax: Double, locationLngMin: Double, locationLngMax: Double): LiveData<List<Office?>?>?

    @Query("SELECT DISTINCT * FROM office_table")
    fun getAllOffices(): LiveData<List<Office?>>

    @Query("SELECT DISTINCT zIndex FROM office_table")
    fun getZIndices(): Flow<List<Float>>

    /*@Delete
    suspend fun deleteOffice(office: Office)

    @Query("DELETE FROM office_table")
    suspend fun deleteAllOffices()*/
}