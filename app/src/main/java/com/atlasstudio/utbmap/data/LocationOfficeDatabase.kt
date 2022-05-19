package com.atlasstudio.utbmap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atlasstudio.utbmap.di.ApplicationScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@TypeConverters(/*LatLngConverter::class,*/ LatLngListConverter::class, OfficeInfoConverter::class)
@Database(entities = [Office::class/*, TouchedLocation::class, LocationOfficeCrossRef::class*/], version = 1)
abstract class LocationOfficeDatabase: RoomDatabase() {
    abstract fun officeDao(): OfficeDao
    //abstract fun locationDao(): TouchedLocationDao
    //abstract fun allDao(): LocationWithOfficesDao

    class Callback @Inject constructor(
        private val databaseLocation: Provider<LocationOfficeDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
    {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = databaseLocation.get().officeDao()

            applicationScope.launch {
                dao.addOffice(Office("U51/101", OfficeType.InfoPoint, "Recepce",
                    Bounds(49.23073128682759, 17.65723058141206,
                        49.230789974458226, 17.657286751359397),
                    arrayListOf(
                        LatLng(49.230789974458226, 17.65723058141206),
                        LatLng(49.230789974458226, 17.657286751359397),
                        LatLng(49.23077090098592, 17.657286751359397),
                        LatLng(49.23073128682759, 17.6572508025931),
                        LatLng(49.23077090098592, 17.65723058141206)
                    ),
                    1,
                    OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/102", OfficeType.InfoPoint, "Recepce",
                    Bounds(49.23068580386592, 17.65726877697625,
                        49.23074449155056, 17.6573204533278),
                    arrayListOf(
                        LatLng(49.23068580386592, 17.65726877697625),
                        LatLng(49.23068580386592, 17.6573204533278),
                        LatLng(49.23074449155056, 17.657290188451352),
                        LatLng(49.23074449155056, 17.657284504561503),
                        LatLng(49.23071368052482, 17.65727102377414)
                    ),
                    1,
                    OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/107", OfficeType.LectureHall, "Hlavní přednášková hala",
                    Bounds(49.23074295569057, 17.656803938128455,
                        49.23092854875127, 17.657096410353507),
                    arrayListOf(
                        LatLng(49.23092854875127, 17.657094757968054),
                        LatLng(49.23080661850506, 17.657096410353507),
                        LatLng(49.23075374600388, 17.656998919611823),
                        LatLng(49.23074295569057, 17.656803938128455),
                        LatLng(49.230882150551466, 17.656853509692024)
                    ),
                    1,
                    OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/108", OfficeType.LectureHall, "Přednášková hala",
                    Bounds(49.230641526630194, 17.656803938128455,
                        49.230746192784814, 17.657000571997276),
                    arrayListOf(
                        LatLng(49.23074295569057, 17.656803938128455),
                        LatLng(49.230746192784814, 17.657000571997276),
                        LatLng(49.230697636348914, 17.65699561484092),
                        LatLng(49.230641526630194, 17.656926214651925)
                    ),
                    1,
                    OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/118", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23037208163111, 17.65709885117049,
                        49.23044048966639, 17.657237342954158),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.65709885117049),
                        LatLng(49.23037208163111, 17.657237342954158),
                        LatLng(49.23044048966639, 17.657237342954158),
                        LatLng(49.23044048966639, 17.65709885117049)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/119", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23037208163111, 17.6569781147437,
                        49.23044048966639, 17.65709885117049),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.6569781147437),
                        LatLng(49.23037208163111, 17.65709885117049),
                        LatLng(49.23044048966639, 17.65709885117049),
                        LatLng(49.23044048966639, 17.6569781147437)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/120", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23037208163111, 17.6568893379593,
                        49.23044048966639, 17.6569781147437),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.6568893379593),
                        LatLng(49.23037208163111, 17.6569781147437),
                        LatLng(49.23044048966639, 17.6569781147437),
                        LatLng(49.23044048966639, 17.6568893379593)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U52/101", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.230509715471776, 17.65770328618923,
                        49.2305653238675, 17.65784823276393),
                    arrayListOf(
                        LatLng(49.2305653238675, 17.65770328618923),
                        LatLng(49.2305653238675, 17.65784823276393),
                        LatLng(49.230509715471776, 17.65784823276393),
                        LatLng(49.230509715471776, 17.65770328618923)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U52/102", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.2305653238675, 17.65770328618923,
                        49.2306895551637, 17.657806560623705),
                    arrayListOf(
                        LatLng(49.2306895551637, 17.65770328618923),
                        LatLng(49.2306895551637, 17.657806560623705),
                        LatLng(49.2305653238675, 17.657806560623705),
                        LatLng(49.2305653238675, 17.65770328618923)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U52/107", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.2307238666095, 17.65763262473407,
                        49.230787756824384, 17.657763076651293),
                    arrayListOf(
                        LatLng(49.2307238666095, 17.65763262473407),
                        LatLng(49.2307238666095, 17.657763076651293),
                        LatLng(49.230787756824384, 17.657763076651293),
                        LatLng(49.230787756824384, 17.65763262473407)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U52/108", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.2307238666095, 17.657503984649026,
                        49.230787756824384, 17.65763262473407),
                    arrayListOf(
                        LatLng(49.2307238666095, 17.657503984649026),
                        LatLng(49.2307238666095, 17.65763262473407),
                        LatLng(49.230787756824384, 17.65763262473407),
                        LatLng(49.230787756824384, 17.657503984649026)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U52/109", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.2307238666095, 17.657290188451352,
                        49.230787756824384, 17.657503984649026),
                    arrayListOf(
                        LatLng(49.2307238666095, 17.657290188451352),
                        LatLng(49.2307238666095, 17.657503984649026),
                        LatLng(49.230787756824384, 17.657503984649026),
                        LatLng(49.230787756824384, 17.657290188451352)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U53/107", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.23055230914219, 17.65803303964666,
                        49.23069073831739, 17.6581399377455),
                    arrayListOf(
                        LatLng(49.23069073831739, 17.65803303964666),
                        LatLng(49.23069073831739, 17.6581399377455),
                        LatLng(49.23060555117857, 17.6581399377455),
                        LatLng(49.23060555117857, 17.658116383927112),
                        LatLng(49.23055230914219, 17.658116383927112),
                        LatLng(49.23055230914219, 17.65803303964666)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/102", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.23037208163111, 17.658183703475885,
                        49.23044048966639, 17.65822454079671),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.658183703475885),
                        LatLng(49.23037208163111, 17.65822454079671),
                        LatLng(49.23044048966639, 17.65822454079671),
                        LatLng(49.23044048966639, 17.658183703475885)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/103", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.23037208163111, 17.65811978419112,
                        49.23044048966639, 17.658183703475885),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.65811978419112),
                        LatLng(49.23037208163111, 17.658183703475885),
                        LatLng(49.23044048966639, 17.658183703475885),
                        LatLng(49.23044048966639, 17.65811978419112)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/104", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.23037208163111, 17.65805408937066,
                        49.23044048966639, 17.65811978419112),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.65805408937066),
                        LatLng(49.23037208163111, 17.65811978419112),
                        LatLng(49.23044048966639, 17.65811978419112),
                        LatLng(49.23044048966639, 17.65805408937066)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/105", OfficeType.SeminarRoom, "Laboratoř",
                    Bounds(49.23037208163111, 17.657929801872495,
                        49.23044048966639, 17.65805408937066),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.657929801872495),
                        LatLng(49.23037208163111, 17.65805408937066),
                        LatLng(49.23044048966639, 17.65805408937066),
                        LatLng(49.23044048966639, 17.657929801872495)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/106", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23037208163111, 17.657801963302955,
                        49.23044048966639, 17.657929801872495),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.657801963302955),
                        LatLng(49.23037208163111, 17.657929801872495),
                        LatLng(49.23044048966639, 17.657929801872495),
                        LatLng(49.23044048966639, 17.657801963302955)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/107", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23036628433565, 17.657697206697357,
                        49.23044048966639, 17.657801963302955),
                    arrayListOf(
                        LatLng(49.23036628433565, 17.657697206697357),
                        LatLng(49.23036628433565, 17.657801963302955),
                        LatLng(49.23044048966639, 17.657801963302955),
                        LatLng(49.23044048966639, 17.657697206697357)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/108", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23037208163111, 17.65756936812782,
                        49.23044048966639, 17.657697206697357),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.65756936812782),
                        LatLng(49.23037208163111, 17.657697206697357),
                        LatLng(49.23044048966639, 17.657697206697357),
                        LatLng(49.23044048966639, 17.65756936812782)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/109", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23037208163111, 17.65744330509397,
                        49.23044048966639, 17.65756936812782),
                    arrayListOf(
                        LatLng(49.23037208163111, 17.65744330509397),
                        LatLng(49.23037208163111, 17.65756936812782),
                        LatLng(49.23044048966639, 17.65756936812782),
                        LatLng(49.23044048966639, 17.65744330509397)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U54/110", OfficeType.LectureRoom, "Přednášková místnost",
                    Bounds(49.23036628433565, 17.657345650631125,
                        49.23044048966639, 17.65744330509397),
                    arrayListOf(
                        LatLng(49.23036628433565, 17.657345650631125),
                        LatLng(49.23036628433565, 17.65737761027351),
                        LatLng(49.23037208163111, 17.6573793858092),
                        LatLng(49.23037208163111, 17.65744330509397),
                        LatLng(49.23044048966639, 17.65744330509397),
                        LatLng(49.23044048966639, 17.657345650631125)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("WC11", OfficeType.RestRoom, "WC/Toaleta",
                    Bounds(49.23056707325122, 17.65688820978652,
                        49.230619945952206, 17.656944390891898),
                    arrayListOf(
                        LatLng(49.230618866918036, 17.6569129955683),
                        LatLng(49.230619945952206, 17.656944390891898),
                        LatLng(49.2305746264977, 17.656941086120995),
                        LatLng(49.23056707325122, 17.65688820978652)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("WC12", OfficeType.RestRoom, "WC/Toaleta",
                    Bounds(49.23056815228651, 17.656955957590068,
                        49.23062318305449, 17.657013791080892),
                    arrayListOf(
                        LatLng(49.23062318305449, 17.657013791080892),
                        LatLng(49.23056815228651, 17.65701213869544),
                        LatLng(49.230577863602946, 17.65695926236097),
                        LatLng(49.230618866918036, 17.656955957590068)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("WC13", OfficeType.RestRoom, "WC/Toaleta",
                    Bounds(49.2307238666095, 17.657763076651293,
                        49.230787756824384, 17.657817431616802),
                    arrayListOf(
                        LatLng(49.2307238666095, 17.657763076651293),
                        LatLng(49.2307238666095, 17.657817431616802),
                        LatLng(49.230787756824384, 17.657802936959335),
                        LatLng(49.230787756824384, 17.657763076651293)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("WC14", OfficeType.RestRoom, "WC/Toaleta",
                    Bounds(49.2307238666095, 17.657802936959335,
                        49.230787756824384, 17.65784642093174),
                    arrayListOf(
                        LatLng(49.2307238666095, 17.657817431616802),
                        LatLng(49.2307238666095, 17.65784642093174),
                        LatLng(49.230787756824384, 17.65784642093174),
                        LatLng(49.230787756824384, 17.657802936959335)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("WC15", OfficeType.RestRoom, "WC/Toaleta",
                    Bounds(49.230509715471776, 17.65770328618923,
                        49.23051184025423, 17.657807020726597),
                    arrayListOf(
                        LatLng(49.23051184025423, 17.65770328618923),
                        LatLng(49.23051184025423, 17.657807020726597),
                        LatLng(49.230509715471776, 17.657807020726597),
                        LatLng(49.230509715471776, 17.65770328618923)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("WC16", OfficeType.RestRoom, "WC/Toaleta",
                    Bounds(49.230509715471776, 17.657807020726597,
                        49.23051184025423, 17.65784823276393),
                    arrayListOf(
                        LatLng(49.23051184025423, 17.657807020726597),
                        LatLng(49.23051184025423, 17.65784823276393),
                        LatLng(49.230509715471776, 17.65784823276393),
                        LatLng(49.230509715471776, 17.657807020726597)
                ),
                    1,
                            OfficeInfo(null, null, null),
                    favourite = false))

                dao.addOffice(Office("U51/219", OfficeType.LectureHall, "Přednášková hala",
                    Bounds(49.23081237238585, 17.656843324439897,
                        49.2309225126172, 17.65705357776785),
                    arrayListOf(
                        LatLng(49.230874231998065, 17.656843324439897),
                        LatLng(49.230917986311155, 17.656938053961284),
                        LatLng(49.2309225126172, 17.657051267291717),
                        LatLng(49.23081237238585, 17.65705357776785),
                        LatLng(49.23079124957369, 17.656993505388435)
                ),
                    2,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/220", OfficeType.LectureHall, "Přednášková hala",
                    Bounds(49.23075654779129, 17.65679942539341,
                        49.230874231998065, 17.656991194912305),
                    arrayListOf(
                        LatLng(49.230874231998065, 17.656843324439897),
                        LatLng(49.230789740801065, 17.656991194912305),
                        LatLng(49.23075654779129, 17.656977332055515),
                        LatLng(49.23075654779129, 17.65680173586954),
                        LatLng(49.23081840747339, 17.65679942539341)
                ),
                    2,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/218", OfficeType.StudyRoom, "Studovna",
                    Bounds(49.23063282819466, 17.657173722526675,
                        49.23078370571004, 17.657300798713898),
                    arrayListOf(
                        LatLng(49.23078370571004, 17.657173722526675),
                        LatLng(49.23078370571004, 17.657300798713898),
                        LatLng(49.23063282819466, 17.657300798713898),
                        LatLng(49.23063282819466, 17.657173722526675)
                ),
                    2,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/203", OfficeType.PersonalOffice, "Kancelář",
                    Bounds(49.23058303851347, 17.657173722526675,
                        49.23063282819466, 17.657300798713898),
                    arrayListOf(
                        LatLng(49.23063282819466, 17.657173722526675),
                        LatLng(49.23063282819466, 17.657300798713898),
                        LatLng(49.23058303851347, 17.657300798713898),
                        LatLng(49.23058303851347, 17.657173722526675)
                ),
                    2,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/204", OfficeType.PersonalOffice, "Kancelář",
                    Bounds(49.23052872244039, 17.657213000620906,
                        49.23058303851347, 17.657300798713898),
                    arrayListOf(
                        LatLng(49.23058303851347, 17.657213000620906),
                        LatLng(49.23058303851347, 17.657300798713898),
                        LatLng(49.23052872244039, 17.657300798713898),
                        LatLng(49.23052872244039, 17.657213000620906)
                ),
                    2,
                            OfficeInfo(null, null, null),
                    favourite = false))
                dao.addOffice(Office("U51/205", OfficeType.PersonalOffice, "Kancelář",
                    Bounds(49.23050458194431, 17.657213000620906,
                        49.23052872244039, 17.657300798713898),
                    arrayListOf(
                        LatLng(49.23052872244039, 17.657213000620906),
                        LatLng(49.23052872244039, 17.657300798713898),
                        LatLng(49.23050458194431, 17.657300798713898),
                        LatLng(49.23050458194431, 17.657213000620906)
                ),
                    2,
                            OfficeInfo(null, null, null),
                    favourite = false))
            }
        }
    }
}