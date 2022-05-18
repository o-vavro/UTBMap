package com.atlasstudio.utbmap.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlasstudio.utbmap.data.Office
import com.atlasstudio.utbmap.repository.LocationOfficeRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repo : LocationOfficeRepository
) : ViewModel() {
    private val state = MutableStateFlow<FavouritesFragmentState>(FavouritesFragmentState.Init)
    val mState: StateFlow<FavouritesFragmentState> get() = state

    suspend fun createFavouritesFlow(): Flow<List<Office?>> {
        return repo.getFavouriteLocations()
    }

    fun onStart() {
        initialize()
    }

    fun onFavouriteSelected(location : Office) {
        viewModelScope.launch {
            /*repo.getLocatedOfficesForFavourite(location)
                .collect {
                    navigateBack(LocationWithOffices(it.location, it.offices))
                }*/
            navigateBack(location)
        }
    }

    fun onFavouriteDelete(location: Office) {
        viewModelScope.launch {
            repo.unsetFavourite(location)
        }
        showDeletedSnackBar()
    }

    private fun initialize() {
        state.value = FavouritesFragmentState.Init
    }

    private fun navigateBack(location : Office) {
        state.value = FavouritesFragmentState.NavigateBackWithResult(location)
    }

    private fun showDeletedSnackBar() {
        state.value = FavouritesFragmentState.SnackBarFavouriteDeleted
    }
}

sealed class FavouritesFragmentState {
    object Init : FavouritesFragmentState()
    data class NavigateBackWithResult(val location : Office) : FavouritesFragmentState()
    object SnackBarFavouriteDeleted: FavouritesFragmentState()
}