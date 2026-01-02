package com.example.gpsdistancetracker

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder

    private lateinit var tvDistance: TextView
    private lateinit var tvSpeed: TextView
    private lateinit var tvDataPoints: TextView
    private lateinit var tvElapsedTime: TextView
    private lateinit var tvCurrentLocation: TextView
    private lateinit var btnStartStop: Button
    private lateinit var btnReset: Button

    private var isTracking = false
    private var totalDistance = 0.0
    private var lastLocation: Location? = null
    private var dataPointCount = 0
    private var startTime = 0L
    private var elapsedTime = 0L

    private val locationList = mutableListOf<Location>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
        private const val MIN_TIME_INTERVAL = 1000L // 1 second
        private const val MIN_DISTANCE_INTERVAL = 0f // Update on any movement
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupLocationServices()
        setupButtons()

        checkLocationPermissions()
    }

    private fun initializeViews() {
        tvDistance = findViewById(R.id.tvDistance)
        tvSpeed = findViewById(R.id.tvSpeed)
        tvDataPoints = findViewById(R.id.tvDataPoints)
        tvElapsedTime = findViewById(R.id.tvElapsedTime)
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation)
        btnStartStop = findViewById(R.id.btnStartStop)
        btnReset = findViewById(R.id.btnReset)
    }

    private fun setupLocationServices() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        geocoder = Geocoder(this, Locale.getDefault())
    }

    private fun setupButtons() {
        btnStartStop.setOnClickListener {
            if (isTracking) {
                stopTracking()
            } else {
                startTracking()
            }
        }

        btnReset.setOnClickListener {
            resetTracking()
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            isTracking = true
            startTime = SystemClock.elapsedRealtime()
            btnStartStop.text = "Stop Tracking"

            // Request location updates from both GPS and Network providers
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_INTERVAL,
                MIN_DISTANCE_INTERVAL,
                this
            )

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_INTERVAL,
                MIN_DISTANCE_INTERVAL,
                this
            )

            Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopTracking() {
        isTracking = false
        elapsedTime += SystemClock.elapsedRealtime() - startTime
        btnStartStop.text = "Start Tracking"
        locationManager.removeUpdates(this)
        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show()
    }

    private fun resetTracking() {
        totalDistance = 0.0
        dataPointCount = 0
        lastLocation = null
        elapsedTime = 0L
        locationList.clear()
        updateUI()
        Toast.makeText(this, "Tracking reset", Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(location: Location) {
        if (!isTracking) return

        dataPointCount++
        locationList.add(location)

        // Calculate distance from last location
        lastLocation?.let { last ->
            val distance = last.distanceTo(location)
            totalDistance += distance
        }

        lastLocation = location
        updateUI()
        updateLocationAddress(location)
    }

    private fun updateUI() {
        // Update distance (convert meters to km)
        val distanceKm = totalDistance / 1000.0
        tvDistance.text = String.format("Distance: %.2f km", distanceKm)

        // Update speed (convert m/s to km/h)
        lastLocation?.let { loc ->
            val speedKmh = loc.speed * 3.6
            tvSpeed.text = String.format("Speed: %.1f km/h", speedKmh)
        }

        // Update data points
        tvDataPoints.text = "Data Points: $dataPointCount"

        // Update elapsed time
        val currentElapsed = if (isTracking) {
            elapsedTime + (SystemClock.elapsedRealtime() - startTime)
        } else {
            elapsedTime
        }

        val seconds = (currentElapsed / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60

        tvElapsedTime.text = String.format(
            "Time: %02d:%02d:%02d",
            hours,
            minutes % 60,
            seconds % 60
        )
    }

    private fun updateLocationAddress(location: Location) {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressText = buildString {
                    append(address.getAddressLine(0) ?: "")
                }
                tvCurrentLocation.text = "Location: $addressText"
            }
        } catch (e: Exception) {
            tvCurrentLocation.text = String.format(
                "Location: %.6f, %.6f",
                location.latitude,
                location.longitude
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isTracking) {
            locationManager.removeUpdates(this)
        }
    }
}