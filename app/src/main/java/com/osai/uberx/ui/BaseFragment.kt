package com.osai.uberx.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.osai.uberx.utils.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.osai.uberx.utils.PermissionUtils.isPermissionGranted
import com.osai.uberx.utils.PermissionUtils.requestPermission

open class BaseFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        //Request code for location permission request.
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_LOCATION = 199
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var googleMap: GoogleMap
    private var userLocation: Location? = null
    private var permissionDenied = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    userLocation = location
                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(userLocation!!.latitude, userLocation!!.longitude))
                        .zoom(17f)
                        .build()
                    addMarker(LatLng(userLocation!!.latitude, userLocation!!.longitude))
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        enableMyLocation()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        } else startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun addMarker(destination: LatLng) {
        val options = MarkerOptions()
        options.position(destination)
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        googleMap.addMarker(options)
    }

    private fun createLocationRequest() {

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }
        builder?.setAlwaysShow(true)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener(requireActivity()) { // All location settings are satisfied.
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        userLocation = location
                        val cameraPosition = CameraPosition.Builder()
                            .target(LatLng(userLocation!!.latitude, userLocation!!.longitude))
                            .zoom(17f)
                            .build()

                        addMarker(LatLng(userLocation!!.latitude, userLocation!!.longitude))
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    }
                }
            }
        }
        task.addOnFailureListener(requireActivity()) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    val resolvable = e as ResolvableApiException
                    resolvable.startResolutionForResult(
                        activity,
                        REQUEST_LOCATION
                    )
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    open fun getUserLocation() : Location?{
        return userLocation
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Success Perform Task Here
                    enableMyLocation()
                }
                Activity.RESULT_CANCELED -> {

                }
            }
        }
    }

    // Enables the My Location layer if the fine location permission has been granted.
    private fun enableMyLocation() {
        if (!::googleMap.isInitialized) return

        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            createLocationRequest()
        } else {
            googleMap.isMyLocationEnabled = false;
            googleMap.uiSettings.isMyLocationButtonEnabled = false;
            userLocation = null
            requestPermission(
                requireActivity() as AppCompatActivity,
                LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                true
            )
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(
            requireActivity(),
            "Current location:\n${location.latitude}, ${location.longitude}",
            Toast.LENGTH_LONG
        )
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            enableMyLocation()
        } else {
            permissionDenied = true
        }
    }

    //Displays a dialog with error message explaining that the location permission is missing.
    private fun showMissingPermissionError() {
        newInstance(true).show(childFragmentManager, "dialog")
    }
}