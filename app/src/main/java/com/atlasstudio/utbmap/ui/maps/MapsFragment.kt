package com.atlasstudio.utbmap.ui.maps

//import com.atlasstudio.utbmap.net.utils.ErrorResponseType

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.atlasstudio.utbmap.R
import com.atlasstudio.utbmap.data.Office
import com.atlasstudio.utbmap.data.OfficeType
import com.atlasstudio.utbmap.databinding.FragmentMapsBinding
import com.atlasstudio.utbmap.utils.showToast
import com.goodiebag.horizontalpicker.HorizontalPicker
import com.goodiebag.horizontalpicker.HorizontalPicker.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.progressindicator.LinearProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.time.format.DateTimeFormatter
import java.util.*


const val REQUEST_CODE_LOCATION = 42   // just a random unique number

@AndroidEntryPoint
class MapsFragment : Fragment(R.layout.fragment_maps),
                     OnMapReadyCallback,
                     OnMapClickListener,
                     OnMapLongClickListener,
                     OnCameraIdleListener
{
    private lateinit var mMap: GoogleMap
    private lateinit var mBinding: FragmentMapsBinding
    private lateinit var mProgress: LinearProgressIndicator
    private lateinit var mMenu: Menu
    private var mCurrentMarker: Marker? = null
    private var mCurrentPolys: MutableList<Polygon> = mutableListOf<Polygon>()

    private val viewModel: MapsViewModel by viewModels()

    /** Demonstrates customizing the info window and/or its contents.  */
    internal inner class CustomInfoWindowAdapter : InfoWindowAdapter {

        // These are both view groups containing an ImageView with id "badge" and two
        // TextViews with id "title" and "snippet".
        private val contents: View = layoutInflater.inflate(R.layout.custom_info_contents, null)

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View? {
            render(marker, contents)
            return contents
        }

        private fun render(marker: Marker, view: View) {
            val office: Office = marker?.tag as Office
            val officeType = when (office.type) {
                OfficeType.DeansOffice -> R.drawable.ic_dean
                OfficeType.PersonalOffice -> R.drawable.ic_person
                OfficeType.LectureHall -> R.drawable.ic_lecture_hall
                OfficeType.LectureRoom -> R.drawable.ic_lecture_room
                OfficeType.SeminarRoom -> R.drawable.ic_seminar_room
                OfficeType.StudyDepartment -> R.drawable.ic_study_department
                OfficeType.StudyRoom -> R.drawable.ic_study_room
                OfficeType.InfoPoint -> R.drawable.ic_info_point
                OfficeType.RestRoom -> R.drawable.ic_rest_room
                OfficeType.Closet -> R.drawable.ic_closet
                else -> 0 // Passing 0 to setImageResource will clear the image view.
            }

            view.findViewById<ImageView>(R.id.office_type).setImageResource(officeType)

            // Set the id, name and office info for the custom info window
            val preId: String = "Místnost: "
            val id: String = preId + (marker.title ?: "")
            val idUi = view.findViewById<TextView>(R.id.office_id)

                // Spannable string allows us to edit the formatting of the text.
            idUi.text = SpannableString(id).apply {
                setSpan(ForegroundColorSpan(Color.GRAY), 0, preId.length, 0)
                setSpan(ForegroundColorSpan(Color.BLACK), preId.length, id.length, 0)
            }

            val preName: String = "Název: "
            val name: String = preName + (marker.snippet ?: "")
            val nameUi = view.findViewById<TextView>(R.id.office_name)
            nameUi.text = SpannableString(name).apply {
                setSpan(ForegroundColorSpan(Color.GRAY), 0, preName.length, 0)
                setSpan(ForegroundColorSpan(Color.BLACK), preName.length, name.length, 0)
            }

            val preHours: String = "Otevírací hodiny:\n"
            var hours: String = preHours
            val locale: Locale = Locale("cs", "CZ", "CZ")
            val formatterStart = DateTimeFormatter.ofPattern("E HH:mm").withLocale(locale)
            val formatterEnd = DateTimeFormatter.ofPattern("- HH:mm\n").withLocale(locale)
            office.info.officeHours?.forEachIndexed { i, v ->
                if (i%2==0) hours += formatterStart.format(v)
                if (i%2==1) hours += formatterEnd.format(v)
            }
            if (office.info.officeHours.isNullOrEmpty())
            {
                hours += "Neuvedeny"
            }

            val hoursUi = view.findViewById<TextView>(R.id.office_hours)
            hoursUi.text = SpannableString(hours).apply {
                setSpan(ForegroundColorSpan(Color.GRAY), 0, preHours.length, 0)
                setSpan(ForegroundColorSpan(Color.BLACK), preHours.length, hours.length, 0)
            }

            val preNumber: String = " Číslo: "
            val preNote: String = " Poznámky: "
            val phoneNumber: String = preNumber + (office.info.phoneNumber ?: "Žádné")
            val info: String = preNumber + (office.info.phoneNumber ?: "Žádné") + preNote + (office.info.note ?: "Žádné")
            val infoUi = view.findViewById<TextView>(R.id.office_info)
            infoUi.text = SpannableString(info).apply {
                setSpan(ForegroundColorSpan(Color.GRAY), 0, preNumber.length, 0)
                setSpan(ForegroundColorSpan(Color.BLACK), preNumber.length, phoneNumber.length, 0)
                setSpan(ForegroundColorSpan(Color.GRAY), phoneNumber.length, phoneNumber.length + preNote.length, 0)
                setSpan(ForegroundColorSpan(Color.BLACK), phoneNumber.length + preNote.length, info.length, 0)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        mBinding = FragmentMapsBinding.inflate(inflater, container, false)
        mProgress = mBinding.progress
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        viewModel.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setFragmentResultListener("favourites_request") { _, bundle ->
            val result = bundle.getParcelable<LatLng>("favourites_result")
            viewModel.onFavouritesResult(result)
        }

        viewModel.getZIndicesList()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())

        mMap.setMapStyle(
            context?.let {
                MapStyleOptions.loadRawResourceStyle(
                    it, R.raw.mapstyle)
            })
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        enableCurrentLocation()

        val zlinUTB = LatLng(49.2305, 17.6575)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zlinUTB, 18.2f))
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (mMap.isMyLocationEnabled) {
            fusedLocationProviderClient.lastLocation
        }
        mMap.setMinZoomPreference(18.2f)
        mMap.setLatLngBoundsForCameraTarget(LatLngBounds(LatLng(49.22965703037514,17.65676606073976),
                                                         LatLng(49.23134252726122,17.65823349729180)))

        observe()
        viewModel.onReady()

        mProgress.hide()
    }

    override fun onMapClick(pos: LatLng) {
        //mTapTextView.text = "tapped, point=$pos"

        //mMap.addMarker(MarkerOptions().position(pos))
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.5f))

        //viewModel.onPositionSelected(pos)
    }

    override fun onMapLongClick(pos: LatLng) {
        //mTapTextView.text = "long pressed, point=$point"
        //mMap.clear()

        viewModel.onPositionSelected(pos)

        // TBD: this should be handled by the viewModel
        //mMap.addMarker(MarkerOptions().position(pos))
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(pos))
        handleFavourite(false)
    }

    override fun onCameraIdle() {
        if(!::mMap.isInitialized) return
        //mCameraTextView.text = mMap.cameraPosition.toString()
        /*if(viewModel.lastZoom != mMap.cameraPosition.zoom) {
            viewModel.onCameraZoomChanged(mMap.cameraPosition.zoom)
        }*/
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableCurrentLocation() {
        if (hasLocationPermission() == true) {
            mMap.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location_permissions),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean? {
        return context?.let { EasyPermissions.hasPermissions(it, Manifest.permission.ACCESS_FINE_LOCATION) }
    }

    private fun observeState(){
        viewModel.mState
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeDraw(){
        viewModel.mPolys
            .flowWithLifecycle(lifecycle)
            .onEach { polys ->
                handlePolygons(polys)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observe(){
        observeState()
        observeDraw()
        // and more...
    }

    private fun handleState(state: MapsFragmentState){
        when(state){
            is MapsFragmentState.IsLoading -> handleLoading(state.isLoading)
            is MapsFragmentState.ShowToast -> {
                //mBinding.statusText.text = state.message
                requireActivity().showToast(state.message)
            }
            /*is MapsFragmentState.ShowTypeToast -> {
                //mBinding.statusText.text = state.message
                requireActivity().showToast(when(state.error) {
                    ErrorResponseType.LocationForOfficeError ->
                        getString(R.string.location_for_office_error)
                    ErrorResponseType.AddressForOfficeError ->
                        getString(R.string.address_for_office_error)
                    ErrorResponseType.OfficesNotFoundError ->
                        getString(R.string.office_not_found_error)
                    ErrorResponseType.AddressForLocationError ->
                        getString(R.string.address_for_location_error)
                    ErrorResponseType.LocationTranslationError ->
                        getString(R.string.location_translation_error)
                })
            }*/
            is MapsFragmentState.SetMarker -> handleMarker(state.location)
            //is MapsFragmentState.DrawPolygon -> handlePolygon(state.polygonOpts)
            is MapsFragmentState.SetZIndices -> handleZIndices(state.zIndices)
            is MapsFragmentState.IsFavourite -> handleFavourite(state.isFavourite)
            is MapsFragmentState.NavigateToFavourites -> handleShowFavourites()
            is MapsFragmentState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            //mBinding.statusText.text = "loading..."
            mBinding.progress.show()
        } else {
            //mBinding.statusText.text = "Loaded"
            mBinding.progress.hide()
        }
    }

    private fun handleMarker(office: Office) {
        mCurrentMarker?.remove()
        //mMap.clear()

        var polyBounds = LatLngBounds(LatLng(office.bounds.locationLatMin,office.bounds.locationLngMin),
                                      LatLng(office.bounds.locationLatMax,office.bounds.locationLngMax))
        // add selected location marker
        mCurrentMarker = mMap.addMarker(MarkerOptions()
                             .position(polyBounds.center)
                             .title(office.id)
                             .snippet(office.name))
        mCurrentMarker?.tag = office
        mCurrentMarker?.showInfoWindow()

        var boundsList = listOf(
            LatLng(office.bounds.locationLatMin, office.bounds.locationLngMin),
            LatLng(office.bounds.locationLatMin, office.bounds.locationLngMax),
            LatLng(office.bounds.locationLatMax, office.bounds.locationLngMin),
            LatLng(office.bounds.locationLatMax, office.bounds.locationLngMax)
        )

        if(boundsList.any {
            !mMap.projection.visibleRegion.latLngBounds.contains(it)
        })
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(polyBounds, 50))
        }
    }

    private fun handlePolygons(polygonOpts: List<PolygonOptions>) {
        mMap.clear()
        for(poly in mCurrentPolys) {
            poly.remove()
        }
        for(office in polygonOpts) {
            if (office.zIndex == viewModel.currentZIndex) {
                mCurrentPolys.add(mMap.addPolygon(office))
                /*mMap.setOnPolylineClickListener (com.google.android.gms.maps.GoogleMap.OnPolylineClickListener {
                })*/
            }
        }
    }

    private fun handleFavourite(favourite: Boolean) {
        val favouriteMark = mMenu.findItem(R.id.action_mark_favourite)
        favouriteMark.isChecked = favourite
        favouriteMark.icon = AppCompatResources.getDrawable(requireContext(), if (favouriteMark.isChecked) R.drawable.ic_star else R.drawable.ic_star_border)
    }

    private fun handleShowFavourites() {
        val action = MapsFragmentDirections.actionMapsFragmentToFavouritesFragment()
        findNavController().navigate(action)
    }

    private fun handleZIndices(zIndices: List<Float>) {
        val zIdText = getView()?.findViewById(R.id.zindex_picker) as HorizontalPicker
        val textItems: MutableList<PickerItem> = ArrayList()

        for (zText in zIndices) {
            textItems.add(TextItem(zText.toInt().toString()))
        }

        zIdText.setItems(
                textItems,
                0
        )

        zIdText.setChangeListener(HorizontalPicker.OnSelectionChangeListener { horizontalPicker, i ->
            viewModel.setZIndex(horizontalPicker.selectedItem.text.toFloat())
        });

    }

    private fun generateSmallIcon(context: Context, resource: Int): BitmapDescriptor {
        val drawable = AppCompatResources.getDrawable(context, resource)

        if (drawable is BitmapDrawable) {
            return BitmapDescriptorFactory.fromBitmap(drawable.bitmap)
        }

        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 50,
            drawable?.intrinsicHeight ?: 50,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_maps, menu)
        mMenu = menu

        /*val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {

        }*/
        viewModel.checkCurrentLocationFavourite()

        //val autoComplete = searchItem.actionView as SearchView.SearchAutoComplete

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /*R.id.action_search -> {
                true
            }*/
            R.id.action_mark_favourite -> {
                viewModel.lastPosition?.let {
                    if (!mBinding.progress.isShown) {
                        item.isChecked = !item.isChecked
                        if (item.isChecked) {
                            item.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_star)
                            viewModel.storeCurrentLocation()
                        } else {
                            item.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_star_border)
                            viewModel.deleteCurrentLocation()
                        }
                    }
                }
                true
            }
            R.id.action_show_favourites -> {
                viewModel.onShowFavourites()
                true
            }
            else -> onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}