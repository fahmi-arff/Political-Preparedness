package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.utils.isValid
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        const val LOCATION_REQUEST_PERMISSION = 2
    }

    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var adapter: RepresentativeListAdapter
    private lateinit var conteks: Context

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.address = Address("", "", "", "Alabama", "")

        conteks = binding.buttonLocation.context

        adapter = RepresentativeListAdapter()
        binding.fragmentRepresentativesRecyclerView.adapter = adapter

        val spinner = binding.state
        ArrayAdapter.createFromResource(
                conteks, R.array.states, android.R.layout.simple_spinner_dropdown_item).also { adapter ->
            spinner.adapter = adapter
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            if (areLocationServicesEnabled(conteks)) {
                if (checkLocationPermissions()) {
                    getLocation()
                }
            } else {
                requiredToEnableLocation()
            }
        }

        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            if (!representatives.isNullOrEmpty()) {
                hideKeyboard()
                adapter.submitList(representatives)
            }
        }

        viewModel.noRepresentativeFound.observe(viewLifecycleOwner) { found ->
            if(found){
                Toast.makeText(conteks, getString(R.string.no_representatives_found), Toast.LENGTH_LONG).show()
                viewModel.noRepresentativeFoundDone()
            }
        }

        viewModel.buttonRepresentative.observe(viewLifecycleOwner) { address ->
            hideKeyboard()
            if (address.isValid()) {
                viewModel.setAddress(address)
            } else {
                showSnackbar(conteks.getString(R.string.all_fields_must_be_valid))
            }
        }
    }

    private fun areLocationServicesEnabled(conteks: Context): Boolean {
        val locationManager =
                conteks.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                if ((areLocationServicesEnabled(conteks))) {
                    requiredToGrantPermission()
                } else {
                    requiredToEnableLocation()
                }
            }
        }
    }

    private fun requiredToGrantPermission() {
        showSnackbar(conteks.getString(R.string.location_permission_not_granted))
    }

    private fun requiredToEnableLocation() {
        showSnackbar(conteks.getString(R.string.location_services_disabled_snackbar))
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_PERMISSION)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                conteks,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (isPermissionGranted()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val address = location?.let { geoCodeLocation(it) }
                if (address != null && address.isValid()) {
                    viewModel.getAddressFromGeolocation(address)
                    binding.address = address
                } else {
                    showSnackbar(conteks.getString(R.string.not_in_the_us))
                }
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        return addresses
                .map { address ->
                    if (address.countryCode == "US") {
                        Address(address.thoroughfare ?: "Unknown Road",
                                address.subThoroughfare, address.locality,
                                address.adminArea, address.postalCode ?: "12345")
                    } else {
                        null
                    }
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}