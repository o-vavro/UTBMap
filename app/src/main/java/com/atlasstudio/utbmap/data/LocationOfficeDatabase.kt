package com.atlasstudio.utbmap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atlasstudio.utbmap.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider


@TypeConverters(LatLngConverter::class, LatLngBoundsConverter::class, OfficeInfoConverter::class)
@Database(entities = [Office::class, TouchedLocation::class, LocationOfficeCrossRef::class], version = 2)
abstract class LocationOfficeDatabase: RoomDatabase() {
    abstract fun officeDao(): OfficeDao
    abstract fun locationDao(): TouchedLocationDao
    abstract fun allDao(): LocationWithOfficesDao

    class Callback @Inject constructor(
        private val databaseLocation: Provider<LocationOfficeDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}