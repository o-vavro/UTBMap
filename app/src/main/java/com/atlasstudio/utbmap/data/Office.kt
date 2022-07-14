package com.atlasstudio.utbmap.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.time.LocalDateTime


@Parcelize
enum class OfficeType : Parcelable {
        DeansOffice,
        PersonalOffice,
        LectureHall,
        LectureRoom,
        SeminarRoom,
        StudyDepartment,
        StudyRoom,
        InfoPoint,
        RestRoom,
        Closet
}

@Parcelize
data class OfficeInfo (
        var note : String?,
        var phoneNumber : String?,
        var officeHours : Array<LocalDateTime>?
        ) : Parcelable

@Parcelize
data class Bounds (
        var locationLatMin : Double,
        var locationLngMin : Double,
        var locationLatMax : Double,
        var locationLngMax : Double
        ) : Parcelable

@Entity(tableName="office_table")
@Parcelize
data class Office(
        @PrimaryKey@NonNull
        val id : String,
        var type : OfficeType,
        var name : String,
        @Embedded(prefix = "bds_") val bounds : Bounds,
        var polygonPoints : ArrayList<LatLng>,
        val zIndex : Float,
        var info : OfficeInfo,
        var favourite : Boolean
        ) : Parcelable

class LatLngListConverter {
        @TypeConverter
        fun toLocation(locationsString: String?): ArrayList<LatLng> {
                val listType: Type = object : TypeToken<ArrayList<LatLng?>?>() {}.type
                return Gson().fromJson(locationsString, listType)
        }

        @TypeConverter
        fun toLocationString(list: ArrayList<LatLng?>?): String {
                val gson = Gson()
                return gson.toJson(list)
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