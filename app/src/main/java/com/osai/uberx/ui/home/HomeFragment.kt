package com.osai.uberx.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.osai.uberx.R
import com.osai.uberx.UberXApp
import com.osai.uberx.ui.BaseFragment
import com.osai.uberx.utils.drawableToBitmap
import com.osai.uberx.utils.startOverlayAnimation

class HomeFragment : BaseFragment() {

    private lateinit var yourAddress: AppCompatTextView
    private lateinit var yourName: TextView

    // Obtain the ViewModel - use the activity as the lifecycle owner
    private val homeViewModel: HomeViewModel by activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        UberXApp.appComponent(requireActivity()).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        yourAddress = root.findViewById(R.id.yourAddressTextView)
        yourName = root.findViewById(R.id.yourNameTextView)

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
            setMapStyle(style)
        }

        homeViewModel.addressName.observe(viewLifecycleOwner, { yourAddress.text = it })
        homeViewModel.state.observe(viewLifecycleOwner, { yourName.text = it })
    }

}
