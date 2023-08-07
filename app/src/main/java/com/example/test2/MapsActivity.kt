package com.example.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.test2.databinding.ActivityMapsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var db: FirebaseFirestore
private lateinit var restaurantId: String
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
         restaurantId = intent.getStringExtra("idtomap")!!

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getRestaurant(restaurantId)

    }



    private fun getRestaurant(restaurantId: String) {
        db.collection("restaurants")
            .document(restaurantId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val locationGeoPoint = documentSnapshot.getGeoPoint("location")
                    val latitude = locationGeoPoint?.latitude ?: 0.0
                    val longitude = locationGeoPoint?.longitude ?: 0.0

                    val restaurantLatLng = LatLng(latitude, longitude)
                    mMap.addMarker(MarkerOptions().position(restaurantLatLng).title("Restaurant"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLatLng, 12f))
                } else {
                    Log.d("TAG", "Restaurant document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error getting restaurant document: ${exception.message}")
            }
    }

}