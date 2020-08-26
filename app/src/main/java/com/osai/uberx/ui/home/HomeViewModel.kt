package com.osai.uberx.ui.home

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.osai.uberx.data.Result.*
import com.osai.uberx.domain.NameUseCase
import com.osai.uberx.utils.CoroutinesDispatcherProvider
import com.osai.uberx.utils.getCarBitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val context: Context,
    private val nameUseCase: NameUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    var addressName: MutableLiveData<String> = MutableLiveData()

    private val _state = MutableLiveData<String>()
    val state: LiveData<String> get() = _state

    init {
        getName()
    }

    private fun getName(): Job {
        return viewModelScope.launch(dispatcherProvider.io) {
            when (val result = nameUseCase()) {
                is Success -> withContext(dispatcherProvider.main) {
                    _state.value = result.data
                }
            }
        }
    }

    fun getCompleteAddressString(latitude: Double, longitude: Double) {
        var strAdd = ""
        viewModelScope.launch(dispatcherProvider.io) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null) {
                    val returnedAddress: Address = addresses[0]
                    val strReturnedAddress = StringBuilder("")

                    for (i in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i))
                            .append("\n")
                    }
                    withContext(dispatcherProvider.main) {
                        strAdd = strReturnedAddress.toString().trim()
                        addressName.value = strAdd
                    }
                } else {
                    withContext(dispatcherProvider.main) {
                        addressName.value = "Can't Find Address"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCarMarkerAndGet(latLng: LatLng): MarkerOptions? {
        val bitmapDescriptor = BitmapDescriptorFactory
            .fromBitmap(getCarBitmap(context))
        return MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)

    }
}
