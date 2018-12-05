package edu.uw.acevedoj.sos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.location.*
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.uw.acevedoj.sos.R.menu.navigation
import kotlinx.android.synthetic.main.dashboard_fragment.*
import org.json.JSONObject
import java.lang.StringBuilder
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class DashboardFragment: Fragment(), OnMapReadyCallback {

    private val MY_PERMISSIONS_COARSE_LOCATION_CODE = 1
    private val MY_PERMISSIONS_FINE_LOCATION_CODE = 2
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 3
    private  val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4
    private lateinit var mMap: GoogleMap
    private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private lateinit var type: String
    private var hasLoaded: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGeoDataClient = Places.getGeoDataClient(context!!, null)

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(context!!, null)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapFragment.retainInstance = true

        val callBtn = view.findViewById<Button>(R.id.callSOS)
        val smsBtn = view.findViewById<Button>(R.id.textSOS)
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())


        callBtn.setOnClickListener{
            checkForCallSmsPermission()
            callPhone()
        }

        smsBtn.setOnClickListener {
            checkForSmsPermission()
            sendText()
        }





    }

    // Function that calls when the call button is pressed
    // with the number specified being the number set at runtime
    // in permissions or as their primary contact
    fun callPhone() {

        val callBtn = view!!.findViewById<Button>(R.id.callSOS)

        checkForCallSmsPermission()
        val intent = Intent(Intent.ACTION_CALL)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())


        callBtn?.setOnClickListener {
            val callPreferences = prefs.getString("call_preference", "1")
            if (callPreferences == "1") {
                intent.setData(Uri.parse("tel:911"))
            } else {
                intent.setData(Uri.parse("tel:${prefs.getString("contact_text_1", "911")}"))
            }
            startActivity(intent)

        }

    }

    // Function that sends a text to contacts
    // specified by the user
    fun sendText() {
        val smsBtn = view!!.findViewById<Button>(R.id.textSOS)
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        checkForSmsPermission()
        smsBtn?.setOnClickListener {
            val sms = SmsManager.getDefault()
            val textPreferences = prefs.getString("text_preference", "1")
            val primaryContact = prefs.getString("contact_text_1", "5554")
            sms.sendTextMessage(primaryContact,null, "I need help, I'm at Latitude: ${currentLocation.latitude} and Longitude ${currentLocation.longitude}", null, null)
            if (textPreferences == "2") {
                for (i in 2..3) {
                    val contactNumber = prefs.getString("contact_text_$i", " ")
                    if (contactNumber != " ") {
                        sms.sendTextMessage(contactNumber, null, "I need help, I'm at Latitude: ${currentLocation.latitude} and Longitude ${currentLocation.longitude}", null, null)
                    }
                }
            }

        }
    }

    // Sets the map styling and ui
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setPadding(0, 0, 0, 200)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setMinZoomPreference(14f)

        updateLocationUI()
    }

    // Gets a link with the places
    // we want to get on the map
    // and then starts the connect to the link
    private fun getLocations(placesLink: String): String {
        var data = ""
        var stream: InputStream? = null
        var urlConnection: HttpURLConnection? = null

        try {
            val url: URL = URL(placesLink)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            stream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(stream))
            val sb = StringBuffer()
            var line = ""
            for (line in br.lines()) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch(e: Exception) {
            Log.d("Error downloading", e.toString())
        } finally {
            stream!!.close()
            urlConnection!!.disconnect()
        }
        return data

    }

    // checks permissions and then starts requesting locations
    private fun updateLocationUI() {
        try {
            val granted = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION);
            if (granted == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
                requestLocations()
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_FINE_LOCATION_CODE
                )

            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    // Checks permssion results for every permission we need
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_COARSE_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocations()
                }
            }
            MY_PERMISSIONS_FINE_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocations()
                }
            }
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendText()
                }
            }
            MY_PERMISSIONS_REQUEST_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // Requests the location and then sets the current location
    // of the user and then shows the marker on the map and centers
    // the camera on the user.
    private fun requestLocations() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude
                            )
                        )
                    )
                    currentLocation = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)

                    if (!hasLoaded) {
                        for (i in 0..resources.getStringArray(R.array.place_type).size - 1) {
                            type = resources.getStringArray(R.array.place_type)[i].toString()
                            dataSet(type)
                        }

                    }
                    hasLoaded = true

                }
            }
        }
        val granted = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION);
        if (granted == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_FINE_LOCATION_CODE
            )
        }
    }

    // Builds the url we want in the format that is accepted by google places
    // and then starts t=loading the data
    private fun dataSet(type: String) {
        val sb = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        sb.append("location=${currentLocation.latitude},${currentLocation.longitude}")
        sb.append("&radius=5000")
        sb.append("&types=$type")
        sb.append("&sensor=true")
        sb.append("&key=${resources.getString(R.string.google_places_key)}")
        Log.v("MAIN", sb.toString())

        // Creating a new non-ui thread task to download Google place json data
        val placesTask = PlacesTask()

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString())
    }

    // Does the location downloading in the background to not overload the device
    private inner class PlacesTask: AsyncTask<String, kotlin.Int, String>() {
        var data: String? = null

        override fun doInBackground(vararg params: String): String? {
            try {
                data = getLocations(params[0])
            } catch (e: Exception) {
                Log.d("Background Task Error", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val parserTask = ParserTask()
            parserTask.execute(result)

        }

    }

    private fun checkForSmsPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_SEND_SMS
                )
            }
        }
    }


    private fun checkForCallSmsPermission() {

        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.CALL_PHONE)){


            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.CALL_PHONE, android.Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )
            }

        }

    }


    // Makes it so that we can get each individual place in a list so we can iterate
    // through each place using the parsing library
    private inner class ParserTask: AsyncTask<String, kotlin.Int, List<HashMap<String, String>>>() {
        private lateinit var jsonObj: JSONObject

        override fun doInBackground(vararg params: String?): List<HashMap<String, String>>? {
            var places: List<HashMap<String, String>>? = null
            var jsonParser: PlaceJSONParser = PlaceJSONParser()

            try {
                jsonObj = JSONObject(params[0])
                places = jsonParser.parse(jsonObj)

            } catch(e: Exception) {
                Log.v("MAIN", e.toString())
            }

            return places
        }

        override fun onPostExecute(result: List<HashMap<String, String>>?) {
            super.onPostExecute(result)

            if (result != null) {
                for (i in 0..result.size - 1) {
                    val markerOptions: MarkerOptions = MarkerOptions()
                    val place = result.get(i)
                    val latLng: LatLng = LatLng(place.get("lat")!!.toDouble(), place.get("lng")!!.toDouble())
                    val name = place.get("place_name")
                    val vicinity = place.get("vicinity")
                    val type = place.get("types")

                    markerOptions.position(latLng)
                    markerOptions.title("$name : $vicinity")
                    var marker = mMap.addMarker(markerOptions)
                    marker.showInfoWindow()
                }
            }
        }

    }
}

