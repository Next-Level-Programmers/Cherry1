package com.nextlevelprogrammers.cherry1


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.navigation.NavigationView
import java.io.IOException

class WelcomePage : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var drawerLayout: DrawerLayout // Added
    private lateinit var navigationView: NavigationView
    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var adapter: ArrayAdapter<String>
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val CALL_PERMISSION_REQUEST_CODE = 101
    private val SMS_PERMISSION_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        navigationView = findViewById(R.id.navigationView)

        autoCompleteTextView = findViewById(R.id.searchLocation)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        drawerLayout = findViewById(R.id.drawerLayout) // Initialize DrawerLayout
        drawerLayout.closeDrawer(GravityCompat.START)
        val btnOpenDrawer: ImageButton = findViewById(R.id.btnOpenDrawer) // Initialize NavigationView
        val searchImageButton = findViewById<ImageButton>(R.id.imageButton)
        val suggestionList = mutableListOf<String>()
        Places.createClient(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestionList)
        autoCompleteTextView.setAdapter(adapter)

        btnOpenDrawer.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }


        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupDrawer(btnOpenDrawer)
        setupNavigationView()
        checkLocationPermission()

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Fetch new predictions based on the entered text
                // Make a request for predictions based on newText using the Places API
                // Update suggestionList with the new predictions received
                // Update the adapter with the new suggestionList
                // notifyDataSetChanged() might be needed to refresh the suggestions
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchImageButton.setOnClickListener {
            val location = autoCompleteTextView.text.toString()

            try {
                // Use Geocoding service to convert location to LatLng
                val geocoder = Geocoder(this)
                val addressList = geocoder.getFromLocationName(location, 1)

                if (!addressList.isNullOrEmpty()) {
                    val latitude = addressList[0].latitude
                    val longitude = addressList[0].longitude

                    // Move map camera to the searched location
                    val searchedLatLng = LatLng(latitude, longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLng, 15f))

                    // Optionally, add a marker to the searched location
                    googleMap.addMarker(MarkerOptions().position(searchedLatLng).title(location))
                } else {
                    // Handle case when the location is not found
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IllegalArgumentException) {
                // Handle invalid location name
                e.printStackTrace()
                Toast.makeText(this, "Invalid location", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                // Handle IOException
                e.printStackTrace()
                Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
            }
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Call function to fetch predictions based on the entered text
                fetchAutocompletePredictions(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        autoCompleteTextView.setOnClickListener {
            val popupWindow = PopupWindow(this)
            val view = layoutInflater.inflate(android.R.layout.simple_dropdown_item_1line, null)
            popupWindow.contentView = view

            val height = WindowManager.LayoutParams.WRAP_CONTENT
            val width = autoCompleteTextView.width

            popupWindow.height = height
            popupWindow.width = width
            popupWindow.isFocusable = true

            // Show dropdown below AutoCompleteTextView
            popupWindow.showAsDropDown(autoCompleteTextView)
        }
    }

    private fun fetchAutocompletePredictions(query: String) {
        val request = AutocompleteSessionToken.newInstance()

        val autocompletePredictionsRequest = FindAutocompletePredictionsRequest.builder()
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(request)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(autocompletePredictionsRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions

                val suggestionList = mutableListOf<String>()
                for (prediction in predictions) {
                    suggestionList.add(prediction.getPrimaryText(null).toString())
                }

                adapter.clear()
                adapter.addAll(suggestionList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e("Places", "Autocomplete prediction fetching failed: $exception")
            }
    }

    private fun checkLocationPermission() {
        if (isLocationPermissionGranted()) {
            initMapView()
        } else {
            requestLocationPermission()
        }
    }
    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun initMapView() {
        mapView.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your operations
                // For example, you can enable location on the map here
                enableLocation()
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    private fun saveLocation(latitude: Double, longitude: Double) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("lat", latitude.toFloat())
        editor.putFloat("lng", longitude.toFloat())
        editor.apply()
    }

    private fun enableLocation() {
        if (::googleMap.isInitialized && // check if googleMap is initialized
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Enable location on the map
            googleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                        saveLocation(location.latitude, location.longitude)
                    }
                }
                .addOnFailureListener {
                    // Handle failure to get location
                    // You can log the error or show a message to the user
                }
        } else {
            // Map is not initialized or permissions are not granted
            // Handle this scenario accordingly
        }
    }

    private fun setupDrawer(btnOpenDrawer: ImageButton) {
        btnOpenDrawer.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }


    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_emergency_contacts -> {
                    val intent = Intent(this, EmergencyContactsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }



    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        googleMap.setOnMapClickListener {
            val intent = Intent(this@WelcomePage, FullMapActivity::class.java)
            startActivity(intent)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    fun dashboard(view: View) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    fun onSOSButtonClick(view: View) {
        // Logic to handle SOS button click
        activateSOS()
    }

    private fun activateSOS() {
        val phoneNumber = "8957821509" // Replace with your emergency phone number

        // Check if SMS permission is granted
        val smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        val callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

        if (smsPermission == PackageManager.PERMISSION_GRANTED && callPermission == PackageManager.PERMISSION_GRANTED) {
            sendSMS(phoneNumber)
            makeCall(phoneNumber)
        } else {
            val permissionsToRequest = mutableListOf<String>()
            if (smsPermission != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.SEND_SMS)
            }
            if (callPermission != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.CALL_PHONE)
            }
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                if (permissionsToRequest.contains(Manifest.permission.SEND_SMS)) SMS_PERMISSION_REQUEST_CODE else CALL_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun sendSMS(phoneNumber: String) {
        val smsBody = "Emergency! I need help!(Testing Cherry1 for production)"
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null)
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun makeCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        try {
            startActivity(callIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

}