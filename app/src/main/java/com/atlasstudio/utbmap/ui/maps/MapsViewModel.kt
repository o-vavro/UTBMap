package com.atlasstudio.utbmap.ui.maps

import android.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlasstudio.utbmap.data.Office
import com.atlasstudio.utbmap.data.OfficeType
//import com.atlasstudio.utbmap.net.utils.ErrorResponseType
import com.atlasstudio.utbmap.repository.LocationOfficeRepository
import com.atlasstudio.utbmap.utils.BaseResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repo : LocationOfficeRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val state = MutableStateFlow<MapsFragmentState>(MapsFragmentState.Init)
    val mState: StateFlow<MapsFragmentState> get() = state

    private val polys = MutableStateFlow<MutableList<PolygonOptions>>(mutableListOf<PolygonOptions>())
    val mPolys: StateFlow<List<PolygonOptions>> get() = polys

    private var mLastLocation: LatLng = LatLng(0.0, 0.0)
    val lastPosition: LatLng? get() = mLastLocation

    private var mLastZoom: Float = 18.2f
    val lastZoom: Float get() = mLastZoom

    private var mCurrentZIndex: Float = 1.0f
    val currentZIndex: Float get() = mCurrentZIndex

    fun onStart() {
        initialize()
    }

    fun onReady() {
        // called from MapsFragment.onMapReady

        viewModelScope.launch {
            // get all visible offices and drow their polygons
            repo.getAllOffices().collect { result ->
                var officeList: MutableList<PolygonOptions> = mutableListOf<PolygonOptions>()
                for(office in result) {
                    office?.let {
                        var color = Color.GREEN
                        when(it.type)
                        {
                            OfficeType.Closet -> color = Color.CYAN
                            OfficeType.DeansOffice -> color = Color.RED
                            OfficeType.InfoPoint -> color = Color.BLUE
                            OfficeType.LectureHall -> color = Color.YELLOW
                            OfficeType.LectureRoom -> color = Color.MAGENTA
                            OfficeType.RestRoom -> color = Color.WHITE
                            OfficeType.PersonalOffice -> color = Color.DKGRAY
                            OfficeType.SeminarRoom -> color = Color.BLACK
                            OfficeType.StudyDepartment -> color = Color.LTGRAY
                            OfficeType.StudyRoom -> color = Color.GRAY
                        }
                        //drawPolygon(PolygonOptions().addAll(it.polygonPoints)
                        officeList.add(PolygonOptions().addAll(it.polygonPoints)
                                                    .zIndex(it.zIndex)
                                                    .fillColor(color)
                                                    .strokeWidth(1.0f)
                                                    .clickable(true))

                    }
                }
                drawPolygons(officeList)
            }
        }
    }

    fun onPositionSelected(pos: LatLng) {
        mLastLocation = pos
            viewModelScope.launch {
                repo.getOffice(pos, mCurrentZIndex).collect { result ->
                    result?.let {
                        var bounds = LatLngBounds(LatLng(result.bounds.locationLatMin,result.bounds.locationLngMin),
                                     LatLng(result.bounds.locationLatMin, result.bounds.locationLngMax)                        )
                        mLastLocation = bounds.center
                        state.value = MapsFragmentState.SetMarker(result)
                    }
                }
            }
            /*repo.getLocatedOfficesForLocation(pos)
                        .onStart {
                            setLoading()
                        }
                        .catch { exception ->
                            hideLoading()
                            showToast(exception.message.toString())
                        }
                        .collect { result ->
                            hideLoading()
                            when(result) {
                                is BaseResult.Success -> {
                                    setMarkers(result.data)
                                    mLastLocation = result.data
                                    checkCurrentLocationFavourite()
                                }
                                is BaseResult.Error -> {
                                    showTypeToast(result.rawResponse)
                                }
                            }
                        }*/
    }

    fun onShowFavourites() {
        navigateToFavourites()
    }

    fun storeCurrentLocation() {
        viewModelScope.launch {
            repo.getOffice(mLastLocation, mCurrentZIndex).collect { result ->
                result?.let {
                    repo.setFavourite(result)
                }
            }
        }
    }

    fun deleteCurrentLocation() {
        viewModelScope.launch {
            repo.getOffice(mLastLocation, mCurrentZIndex).collect { result ->
                result?.let {
                    repo.unsetFavourite(result)
                }
            }

        }
    }

    fun checkCurrentLocationFavourite() {
        viewModelScope.launch {
            repo.isLocationFavourite(mLastLocation, mCurrentZIndex)
                .collect {
                    setFavourite(it)
                }
        }
    }

    fun onFavouritesResult(location: LatLng?) {
        location?.let {
            viewModelScope.launch {
                //setMarkers(location)
                mLastLocation = location
                checkCurrentLocationFavourite()
            }
        }
    }

    fun getZIndicesList() {
        viewModelScope.launch {
            repo.getZIndicesList()
                .collect {
                    setZIndices(it)
                }
        }
    }

    fun setZIndex(zIndex: Float) {
        mCurrentZIndex = zIndex
        viewModelScope.launch {
            // get all visible offices and drow their polygons
            repo.getAllOffices().collect { result ->
                var officeList: MutableList<PolygonOptions> = mutableListOf<PolygonOptions>()
                for(office in result) {
                    office?.let {
                        var color = Color.GREEN
                        when(it.type)
                        {
                            OfficeType.Closet -> color = Color.CYAN
                            OfficeType.DeansOffice -> color = Color.RED
                            OfficeType.InfoPoint -> color = Color.BLUE
                            OfficeType.LectureHall -> color = Color.YELLOW
                            OfficeType.LectureRoom -> color = Color.MAGENTA
                            OfficeType.RestRoom -> color = Color.WHITE
                            OfficeType.PersonalOffice -> color = Color.DKGRAY
                            OfficeType.SeminarRoom -> color = Color.BLACK
                            OfficeType.StudyDepartment -> color = Color.LTGRAY
                            OfficeType.StudyRoom -> color = Color.GRAY
                        }
                        //drawPolygon(PolygonOptions().addAll(it.polygonPoints)
                        officeList.add(PolygonOptions().addAll(it.polygonPoints)
                            .zIndex(it.zIndex)
                            .fillColor(color)
                            .strokeWidth(1.0f)
                            .clickable(true))

                    }
                }
                drawPolygons(officeList)
            }
        }
    }

    /*fun onCameraZoomChanged(cameraZoom: Float) {
        mLastZoom = cameraZoom
    }*/

    private fun initialize() {
        state.value = MapsFragmentState.Init
    }

    private fun setLoading(){
        state.value = MapsFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = MapsFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = MapsFragmentState.ShowToast(message)
    }

    /*private fun showTypeToast(error: ErrorResponseType){
        state.value = MapsFragmentState.ShowTypeToast(error)
    }*/

    /*private fun setMarkers(location: LocationWithOffices) {
        state.value = MapsFragmentState.SetMarkers(location)
    }*/

    private fun drawPolygons(polygonOptsList: MutableList<PolygonOptions>) {
        polys.value = polygonOptsList
        //state.value = MapsFragmentState.DrawPolygon(polygonOpts)
    }

    private fun setFavourite(favourite: Boolean) {
        state.value = MapsFragmentState.IsFavourite(favourite)
    }

    private fun navigateToFavourites() {
        state.value = MapsFragmentState.NavigateToFavourites
    }

    private fun setZIndices(zIndices: List<Float>) {
        state.value = MapsFragmentState.SetZIndices(zIndices)
    }
}

sealed class MapsFragmentState {
    object Init : MapsFragmentState()
    data class IsLoading(val isLoading : Boolean) : MapsFragmentState()
    data class ShowToast(val message : String) : MapsFragmentState()
    //data class ShowTypeToast(val error : ErrorResponseType) : MapsFragmentState()
    data class SetMarker(val location : Office) : MapsFragmentState()
    //data class DrawPolygon(val polygonOpts : PolygonOptions) : MapsFragmentState()
    data class SetZIndices(val zIndices: List<Float>) : MapsFragmentState()
    data class IsFavourite(val isFavourite : Boolean) : MapsFragmentState()
    object NavigateToFavourites : MapsFragmentState()
}
