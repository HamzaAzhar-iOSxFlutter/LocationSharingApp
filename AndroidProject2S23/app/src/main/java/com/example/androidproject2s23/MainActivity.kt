package com.example.androidproject2s23

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call

class MainActivity : AppCompatActivity(),  EmailListener {

    private lateinit var emailFragment: EmailFragment
    private lateinit var aboutFragment: AboutFragment
    var mapFragment: MapsFragment? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val placesApiService = retrofit.create(PlacesApiService::class.java)


    private val placesList = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mapFragment = MapsFragment()

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mapframe, mapFragment!!)
        transaction.commit()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MapsFragment.MY_PERMISSIONS_REQUEST_LOCATION) {
            mapFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_map -> showMap()
            R.id.menu_places -> showPlaces()
            R.id.menu_email -> showEmailFragment()
            R.id.menu_about -> showAbout()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showMap() {
        mapFragment = MapsFragment()

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mapframe, mapFragment!!)
        transaction.commit()
    }

    private fun showPlaces() {

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userLatitude = sharedPreferences?.getString("latitude","0.00")?.toDouble()
        val userLongitude = sharedPreferences?.getString("longitude","0.00")?.toDouble()

        val savedLocation = LatLng(userLatitude ?: 0.0, userLongitude ?: 0.0)
        fetchNearbyPlaces(savedLocation)

        val placesFragment = PlacesFragment.newInstance(placesList)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapframe, placesFragment)
            .addToBackStack(null)
            .commit()
    }
    private fun showEmailFragment() {
        emailFragment = EmailFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapframe, emailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun sendEmail(emailAddress: String) {

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userLatitude = sharedPreferences?.getString("latitude","0.00")?.toDouble()
        val userLongitude = sharedPreferences?.getString("longitude","0.00")?.toDouble()
        val userAddress = sharedPreferences?.getString("address","Null")

        val emailSubject = "Current Location"
        val emailBody = "Latitude: ${userLatitude}\nLongitude: ${userLongitude}\nAddress: ${userAddress}"


        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)

        startActivity(Intent.createChooser(intent, "Choose an Email client:"))

    }


    private fun showAbout() {
        aboutFragment = AboutFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapframe, aboutFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchNearbyPlaces(location: LatLng) {
        val apiKey = "AIzaSyB7p7Fx-mIlhE6dHiDQNRJbQLVFRIH_ppA"
        val radius = 1500 // Desired search radius in meters


        val locationString = "${location.latitude},${location.longitude}"
        val call = placesApiService.getNearbyPlaces(locationString, radius, apiKey)

        call.enqueue(object : retrofit2.Callback<PlacesApiResponse> {
            override fun onResponse(call: Call<PlacesApiResponse>, response: retrofit2.Response<PlacesApiResponse>) {
                if (response.isSuccessful) {
                    val places = response.body()?.results
                    if (places != null) {

                        placesList.clear()
                        placesList.addAll(places)

                    }
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<PlacesApiResponse>, t: Throwable) {
                // Handle failure
            }
        })

    }

}