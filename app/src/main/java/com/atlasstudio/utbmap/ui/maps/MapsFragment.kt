package com.atlasstudio.utbmap.ui.maps

//import com.atlasstudio.utbmap.net.utils.ErrorResponseType

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.atlasstudio.utbmap.R
import com.atlasstudio.utbmap.data.Office
import com.atlasstudio.utbmap.databinding.FragmentMapsBinding
import com.atlasstudio.utbmap.utils.showToast
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
    private var mCurrentZIndex: Float = 1.0f

    private val viewModel: MapsViewModel by viewModels()

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
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnCameraIdleListener(this)

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

        //mMap.clear()

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
        viewModel.mPolys
            .flowWithLifecycle(lifecycle)
            .onEach { polys ->
                handlePolygons(polys)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observe(){
        observeState()
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
            is MapsFragmentState.DrawPolygon -> handlePolygon(state.polygonOpts)
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
        mMap.clear()

        var polyBounds = LatLngBounds(LatLng(office.bounds.locationLatMin,office.bounds.locationLngMin),
                                      LatLng(office.bounds.locationLatMax,office.bounds.locationLngMax))
        // add selected location marker
        mCurrentMarker = mMap.addMarker(MarkerOptions()
                             .position(polyBounds.center)
                             .title(office.name))

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

    private fun handlePolygon(polygonOpts: PolygonOptions) {
        if (polygonOpts.zIndex == mCurrentZIndex) {
            mMap.addPolygon(polygonOpts)
            /*mMap.setOnPolylineClickListener (com.google.android.gms.maps.GoogleMap.OnPolylineClickListener {

            })*/
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