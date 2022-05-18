package com.atlasstudio.utbmap.repository
import com.atlasstudio.utbmap.data.*
/*import com.atlasstudio.utbmap.net.model.CoordinateTranslationResponse
import com.atlasstudio.utbmap.net.model.OfficeIdResponse
import com.atlasstudio.utbmap.net.model.OfficeResponse
import com.atlasstudio.utbmap.net.model.RuianAddressResponse
import com.atlasstudio.utbmap.net.service.*
import com.atlasstudio.utbmap.net.utils.ErrorResponseType*/
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationOfficeRepository @Inject constructor(private val officeDao: OfficeDao/*,
                                                   private val locationDao: TouchedLocationDao,
                                                   private val locationWithOfficesDao: LocationWithOfficesDao*/) {

    /*suspend fun getOfficeForLocation(loc: LatLng) : Flow<BaseResult<List<Office?>, WrappedListResponse<OfficeResponse>>> {
        return flow {
            val coordinateTranslationResponse = coordinateTranslationService.convertLocationGPSToJTSK(
                "${loc.longitude}+${loc.latitude}+0",
                SimpleDateFormat("yyyy-MM-dd").format(Date())
            )

            if (coordinateTranslationResponse.isSuccessful) {
                val body = coordinateTranslationResponse.body()!!
                val bodyParts = body.coords!!.split(" ")

                val ruianResponse = ruianService.locationToAddress(JTSKLocation(bodyParts[0]!!, bodyParts[1]!!))
                if(ruianResponse.isSuccessful) {
                    val ruianAddress = ruianResponse.body()!!.address!!.address

                    val addressParts = ruianAddress!!.split(",")
                    val psc = addressParts.last().trim().split(' ')[0]
                    val obec = addressParts.last().trim().split(' ').drop(1).joinToString(" ")
                    //val cast_obce = addressParts[1].trim()
                    val ulice = addressParts[0].trim().split(' ').dropLast(1).joinToString(" ")
                    val cisla = addressParts[0].trim().split(' ').last().split('/')
                    val popisne= cisla[0]
                    var orientacni: String = ""
                    if (cisla.size > 1) {
                        orientacni = cisla[1]
                    }

                    var filter = ""
                    if (ulice == "č.p.") {
                        filter = "{\"limit\":1,\"where\":{\"NAZEV_OBCE\":\"%s\",\"PSC\":\"%s\"}}".format(obec, psc) //,\"CISLO_DOMOVNI\":\"%s\".format(popisne)
                    }
                    else {
                        filter = "{\"limit\":1,\"where\":{\"NAZEV_OBCE\":\"%s\",\"PSC\":\"%s\",\"NAZEV_ULICE\":\"%s\"}}".format(obec, psc, ulice) //,\"CISLO_DOMOVNI\":\"%s\" , popisne)
                    }
                    if (cisla.size > 1) {
                        filter = "{\"limit\":1,\"where\":{\"NAZEV_OBCE\":\"%s\",\"PSC\":\"%s\",\"NAZEV_ULICE\":\"%s\",\"CISLO_DOMOVNI\":\"%s\",\"CISLO_ORIENTACNI\":\"%s\"}}".format(obec, psc, ulice, popisne, orientacni)
                    }

                    val officeIdResponse = apiTalksService.addressToId(filter)
                    if(officeIdResponse.isSuccessful) {
                        val officeId = officeIdResponse.body()!!.data[0].id

                        val officeResponse = apiTalksService.idToOffice(officeId)
                        if(officeResponse.isSuccessful) {
                            emit(BaseResult.Success(officeResponse.body()!!.toOffices()))
                        }
                        else {
                            val type = object : TypeToken<WrappedListResponse<OfficeResponse>>() {}.type
                            val err = Gson().fromJson<WrappedListResponse<OfficeResponse>>(
                                officeResponse.errorBody()!!.charStream(), type
                            )!!
                            err.code = officeResponse.code()
                            emit(BaseResult.Error(err))
                        }
                    }
                    else {
                        val type = object : TypeToken<WrappedListResponse<OfficeIdResponse>>() {}.type
                        val err = Gson().fromJson<WrappedListResponse<OfficeResponse>>(
                            officeIdResponse.errorBody()!!.charStream(), type
                        )!!
                        err.code = officeIdResponse.code()
                        emit(BaseResult.Error(err))
                    }
                } else {
                    val type = object : TypeToken<WrappedListResponse<RuianAddressResponse>>() {}.type
                    val err = Gson().fromJson<WrappedListResponse<OfficeResponse>>(
                        ruianResponse.errorBody()!!.charStream(), type
                    )!!
                    err.code = ruianResponse.code()
                    emit(BaseResult.Error(err))
                }
            } else {
                val type = object : TypeToken<WrappedListResponse<CoordinateTranslationResponse>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<OfficeResponse>>(
                    coordinateTranslationResponse.errorBody()!!.charStream(), type
                )!!
                err.code = coordinateTranslationResponse.code()
                emit(BaseResult.Error(err))
            }
        }
    }*/

    suspend fun getOffice(location: LatLng): Office?
    {
        location.let {
            return officeDao.getOffice(location.latitude, location.longitude)?.value
        }
    }

    suspend fun setFavourite(location: Office?) {
        location?.let {
            it?.favourite = true
        }
    }

    suspend fun unsetFavourite(location: Office?) {
        location?.let {
            // delete all offices with only one ref equal to this touched location and the ref
            it?.favourite = false
        }
    }

    /*suspend fun deleteLocation(location: LatLng) {
        location?.let {
            officeDao. getFavouriteOffices(location)
            // delete all offices with only one ref equal to this touched location and the ref
            for (office in location.offices) {
                office?.let {
                    locationWithOfficesDao.getCrossRefsForOffice(office.id)
                        .collect { crossRefList ->
                            if (crossRefList.size == 1) {
                                officeDao.deleteOffice(office)
                            }
                        }
                    locationWithOfficesDao.deleteLocationOfficeCrossRef(it.location, office.id)
                }
            }
        }
    }*/

/*    suspend fun deleteLocation(location: TouchedLocation) {
        location.location?.let {
            locationDao.deleteTouchedLocation(it, location.officeId)
            // delete all offices with only one ref equal to this touched location and the ref
            locationWithOfficesDao.getCrossRefsForLocation(it)
                .collect { crossRefListLocation ->
                    for (item in crossRefListLocation) {
                        locationWithOfficesDao.getCrossRefsForOffice(item.officeId)
                            .collect { crossRefListOffice ->
                                if (crossRefListOffice.size == 1) {
                                    officeDao.getOffice(item.officeId)?.value?.let { office ->
                                        officeDao.deleteOffice(office)
                                    }

                                }
                            }
                    }
                }
        }
    }*/

    suspend fun isLocationFavourite(location: LatLng?): Flow<Boolean> = flow {
        location?.let {
            officeDao.getOffice(it.latitude, it.longitude)
                ?.value?.let {
                    it.favourite == true
                }
        }
        emit(false)
    }

    suspend fun getFavouriteLocations(): Flow<List<Office?>> = flow {
        officeDao.getFavouriteOffices(true)
            .collect {
                emit(it)
            }
    }

    /*suspend fun getLocatedOfficesForFavourite(location: TouchedLocation) = flow {
        locationWithOfficesDao.getTouchedLocationWithOffices(location.location)
            .collect {
                emit(it)
            }
    }*/
}