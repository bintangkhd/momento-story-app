package com.example.storyappsubmission.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyappsubmission.R
import com.example.storyappsubmission.customview.CustomPopUpAlert
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.databinding.ActivityMapBinding
import com.example.storyappsubmission.ui.GeneralViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

@SuppressLint("MissingPermission")
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MapsActivity"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private val viewModel: MapViewModel by viewModels { factory }
    private lateinit var factory: GeneralViewModelFactory
    private val boundsBuilder = LatLngBounds.Builder()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        factory = GeneralViewModelFactory.getInstance(binding.root.context)

        setupToolbar()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            mMap.isMyLocationEnabled = true


            getMyLocation()
            getLocationStory(mMap)

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.style_map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.day_mode -> {
                try {
                    val success =
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_map_style))
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.")
                    }
                } catch (exception: Resources.NotFoundException) {
                    Log.e(TAG, "Can't find style. Error: ", exception)
                }
            }
            R.id.night_mode -> {
                try {
                    val success =
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_map_style))
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.")
                    }
                } catch (exception: Resources.NotFoundException) {
                    Log.e(TAG, "Can't find style. Error: ", exception)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                mMap.isMyLocationEnabled = true

                getMyLocation()
                getLocationStory(mMap)
            } else {
                Toast.makeText(
                    this,
                    R.string.permission_denied_alert,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getMyLocation() {
        mMap.isMyLocationEnabled = true
    }

    private fun getLocationStory(map: GoogleMap) {
        viewModel.getStoryLocation().observe(this) { result ->
            if (result != null) {
                when(result) {
                    is ResultCondition.LoadingState -> {
                        Toast.makeText(
                            this,
                            R.string.loading_map,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ResultCondition.ErrorState -> {
                        errorHandler()
                    }
                    is ResultCondition.SuccessState -> {
                        locationMarker(result.data.listStory, map)
                    }
                }
            }
        }
    }



    private fun locationMarker(listStory: List<StoryListResponseModel>, googleMap: GoogleMap) {
        listStory.forEach { story ->
            val latLng = LatLng(story.lat!!, story.lon!!)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title( "${story.createdAt.subSequence(0, 10).toString()} ${story.createdAt.subSequence(11, 16).toString()}")
                    .snippet(StringBuilder("Created by : " + story.name)
                        .toString()
                    )
            )
            boundsBuilder.include(latLng)
        }
    }


    private fun errorHandler() {
        CustomPopUpAlert(this, R.string.error_message).show()
    }

    private fun setupToolbar() {
        title = resources.getString(R.string.title_activity_map)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}