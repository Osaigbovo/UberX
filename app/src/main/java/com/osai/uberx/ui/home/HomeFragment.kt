package com.osai.uberx.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.osai.uberx.R
import com.osai.uberx.ui.BaseFragment
import com.osai.uberx.utils.PermissionUtils.isPermissionGranted
import com.osai.uberx.utils.PermissionUtils.requestPermission
import com.osai.uberx.utils.drawableToBitmap
import com.osai.uberx.utils.startOverlayAnimation

class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val style: MapStyleOptions = MapStyleOptions.loadRawResourceStyle(
            requireActivity(), R.raw.maps_style
        )

        with(googleMap) {
            setOnMyLocationButtonClickListener(this@HomeFragment)
            setOnMyLocationClickListener(this@HomeFragment)
            setMaxZoomPreference(20F)
            //setMapStyle(style)
        }

        //addOverlay(LatLng(getUserLocation()?.latitude!!, getUserLocation()?.longitude!!), googleMap)
    }

    private fun addOverlay(place: LatLng?, googleMap: GoogleMap) {
        val groundOverlay: GroundOverlay = googleMap.addGroundOverlay(
            GroundOverlayOptions()
                .position(place, 100f)
                .transparency(0.5f)
                .zIndex(3f)
                .image(
                    BitmapDescriptorFactory
                        .fromBitmap(getDrawable(requireActivity(), R.drawable.map_overlay)!!.drawableToBitmap())
                )
        )
        groundOverlay.startOverlayAnimation()
    }
}