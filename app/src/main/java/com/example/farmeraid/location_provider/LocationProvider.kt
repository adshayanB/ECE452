package com.example.farmeraid.location_provider

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LocationProvider() {
    private var activityContext: Context?= null
    //private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activityContext as Context)
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    //A callback for receiving notifications from the FusedLocationProviderClient.
    lateinit var locationCallback: LocationCallback

    //The main entry point for interacting with the Fused Location Provider
    lateinit var locationProvider: FusedLocationProviderClient

    @Inject
    lateinit var snackbarDelegate: SnackbarDelegate

    data class LatandLong(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
    )

    @SuppressLint("MissingPermission")
    @Composable
    fun getUserLocation(): LatandLong {

        // The Fused Location Provider provides access to location APIs.
        locationProvider = LocationServices.getFusedLocationProviderClient(activityContext as Context)

        var currentUserLocation by remember { mutableStateOf(LatandLong()) }

        DisposableEffect(key1 = locationProvider) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {

                    /**
                     * Option 1
                     * This option returns the locations computed, ordered from oldest to newest.
                     * */
                    for (location in result.locations) {
                        // Update data class with location data
                        currentUserLocation = LatandLong(location.latitude, location.longitude)
                        Log.d("LOCATION_TAG", "${location.latitude},${location.longitude}")
                    }


                    /**
                     * Option 2
                     * This option returns the most recent historical location currently available.
                     * Will return null if no historical location is available
                     * */

//                    locationProvider.lastLocation
//                        .addOnSuccessListener { location ->
//                            location?.let {
//                                val lat = location.latitude
//                                val long = location.longitude
//                                // Update data class with location data
//                                currentUserLocation = LatandLong(latitude = lat, longitude = long)
//                            }
//                        }
//                        .addOnFailureListener {
//                            Log.e("Location_error", "${it.message}")
//                        }

                }
            }

            if (isPermissionGranted()) {
                Log.d("MESSAGE", "Location Permission Granted")
                locationUpdate()
            } else {
                // Open permission screen
                Log.d("MESSAGE", "Asking for Location Permission")
                val LOCATION_PERMISSION_REQUEST_CODE = 1001
                ActivityCompat.requestPermissions(
                    activityContext as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )

                //Toast message
                Toast.makeText(activityContext, "Allow Permission", Toast.LENGTH_SHORT).show()
            }

            onDispose {
                stopLocationUpdate()
            }
        }
        //
        return currentUserLocation

    }

    fun setActivtyContext(context: Context) {
        activityContext = context
    }

    fun isPermissionGranted(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(activityContext as Context, locationPermission)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    fun stopLocationUpdate() {
        try {
            //Removes all location updates for the given callback.
            val removeTask = locationProvider.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LOCATION_TAG", "Location Callback removed.")
                } else {
                    Log.d("LOCATION_TAG", "Failed to remove Location Callback.")
                }
            }
        } catch (se: SecurityException) {
            Log.e("LOCATION_TAG", "Failed to remove Location Callback.. $se")
        }
    }

    @SuppressLint("MissingPermission")
    fun locationUpdate() {
        locationCallback.let {
            //An encapsulation of various parameters for requesting
            // location through FusedLocationProviderClient.
            val locationRequest: LocationRequest =
                LocationRequest.create().apply {
                    interval = TimeUnit.SECONDS.toMillis(10)
                    fastestInterval = TimeUnit.SECONDS.toMillis(5)
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                }

            //use FusedLocationProviderClient to request location update
            locationProvider.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }

    }

    fun getReadableLocation(latitude: Double, longitude: Double): String {
        var addressText = ""
        val geocoder = Geocoder(activityContext as Context, Locale.getDefault())

        try {

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                addressText = "${address.getAddressLine(0)}, ${address.locality}"
                // Use the addressText in your app
                Log.d("geolocation", addressText)
            }

        } catch (e: IOException) {
            Log.d("geolocation", e.message.toString())

        }

        return addressText

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getCoordinatesFromLocationName(locationName : String): DoubleArray {
        var latitude : Double = 0.0
        var longitude : Double = 0.0
        val geocoder = Geocoder(activityContext as Context, Locale.getDefault())

        try {
            var addresses = geocoder.getFromLocationName(locationName, 1)

            if (addresses?.isNotEmpty() == true) {
                val address = addresses.get(0)
                latitude = address.latitude
                longitude = address.longitude

                Log.d("geolocation", "latitude: ${latitude},  longitude: ${longitude}")
            }

            Log.d("geolocation", "BEFORE getFromLocationName")

//            geocoder.getFromLocationName(locationName, 1) { addressList ->
//                Log.d("Geolocation", "Address List: ${addressList}")
//                if (addressList.isNotEmpty()) {
//                    latitude = addressList[0].latitude
//                    longitude = addressList[0].longitude
//
//                    Log.d("geolocation", "latitude: ${latitude},  longitude: ${longitude}")
//                } else {
//                    snackbarDelegate.showSnackbar(
//                        message = "Location not found: $locationName"
//                    )
//                }
//            }
        } catch (e: Exception) {
            Log.d("geolocation", e.message.toString())

        }

        return doubleArrayOf(latitude, longitude)
    }
}