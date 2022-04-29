package com.atlasstudio.utbmap.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Entity(tableName="touched_location_table")
@Parcelize
data class TouchedLocation(
    @PrimaryKey
    @NonNull
    var location : LatLng,
    var officeId: String
    ) : Parcelable

class LatLngConverter {
    @TypeConverter
    fun toLocation(locationString: String?): LatLng? {
        return try {
            Gson().fromJson(locationString, LatLng::class.java)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun toLocationString(location: LatLng?): String? {
        return Gson().toJson(location)
    }
}