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
import java.time.LocalDateTime

@Parcelize
enum class OfficeType : Parcelable {
        TaxOffice,
        LabourOffice,
        DistrictCourt,
        RegionalCourt,
        HighCourt,
        CustomsOffice,
        CityGovernmentOffice
}

@Parcelize
data class OfficeInfo (
        var officeHours : Array<LocalDateTime>?,
        var phoneNumber : String?,
        var note : String?
        ) : Parcelable

@Entity(tableName="office_table")
@Parcelize
data class Office (
        @PrimaryKey@NonNull
        val id : String,
        var type : OfficeType,
        var name : String,
        var location : LatLngBounds,
        var address: String,
        var info : OfficeInfo
        ) : Parcelable


class LatLngBoundsConverter {
        @TypeConverter
        fun toLocation(locationString: String?): LatLngBounds? {
                return try {
                        Gson().fromJson(locationString, LatLngBounds::class.java)
                } catch (e: Exception) {
                        null
                }
        }

        @TypeConverter
        fun toLocationString(location: LatLngBounds?): String? {
                return Gson().toJson(location)
        }
}

class OfficeInfoConverter {
        @TypeConverter
        fun toOfficeInfo(officeInfoString: String?): OfficeInfo? {
                return try {
                        Gson().fromJson(officeInfoString, OfficeInfo::class.java)
                } catch (e: Exception) {
                        null
                }
        }

        @TypeConverter
        fun toOfficeInfoString(officeInfo: OfficeInfo?): String? {
                return Gson().toJson(officeInfo)
        }
}