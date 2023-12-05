package uk.ac.tees.mad.q2252114

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

class Location(private val context: Context) {

    private var currentLocation: Location? = null

    fun getCurrentLocation(): Location? {
        // Acquire a reference to the system Location Manager
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Define a listener that responds to location updates
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Called when a new location is found by the network location provider.
                currentLocation = location
                locationManager.removeUpdates(this) // Stop listening for updates once obtained
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Handle status changes (optional)
            }

            override fun onProviderEnabled(provider: String) {
                // Called when the provider is enabled by the user.
            }

            override fun onProviderDisabled(provider: String) {
                // Called when the provider is disabled by the user.
            }
        }

        // Register the listener with the Location Manager to receive location updates
        // You may want to check for location permissions before requesting updates
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

        return currentLocation
    }
}
