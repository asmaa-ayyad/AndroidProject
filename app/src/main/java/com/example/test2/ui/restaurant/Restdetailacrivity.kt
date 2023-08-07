package com.example.test2.ui.restaurant

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.test2.MainActivity
import com.example.test2.MapsActivity
import com.example.test2.adapter.mealAdapter
import com.example.test2.databinding.ActivityRestdetailacrivityBinding
import com.example.test2.model.mealdata
import com.example.test2.ui.meal.addmeal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class restdetailacrivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore


    private lateinit var adapter: mealAdapter
    private lateinit var binding: ActivityRestdetailacrivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestdetailacrivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantId = intent.getStringExtra("asmaa")!!

        db = Firebase.firestore

        val data = ArrayList<mealdata>()
        adapter = mealAdapter(this, data)
        binding.remeal.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        binding.remeal.adapter = adapter

        getrestdet(restaurantId)

        binding.remeal.setHasFixedSize(true)
        binding.remeal.isNestedScrollingEnabled = true

        binding.btnEdit.setOnClickListener {
            val i = Intent(this, editrest::class.java)
            i.putExtra("editid", restaurantId)

            startActivity(i)


        }
        binding.btnadd.setOnClickListener {
            val i = Intent(this, addmeal::class.java)
            i.putExtra("asmaa2", restaurantId)
            startActivity(i)

        }
        binding.bttDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Delete Resturant")
            alertDialog.setMessage("Are you sure?")

            alertDialog.setPositiveButton("Yes") { _, _ ->
                deleterestById(restaurantId)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragmentToLoad", "RestaurantFragment")

                startActivity(intent)

            }
            alertDialog.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()

            }
            alertDialog.create().show()
            true
        }
        binding.btnMinus.setOnClickListener {
            val i = Intent(this, MapsActivity::class.java)
            i.putExtra("idtomap",restaurantId )

            startActivity(i)




        }



    }

    override fun onResume() {
        super.onResume()

        val restaurantId = intent.getStringExtra("asmaa")!!
        getestmeal(restaurantId)
    }

    private fun deleterestById(id: String) {
        db.collection("meals")
            .whereEqualTo("restid", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val mealId = document.id
                    db.collection("meals").document(mealId).delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "Meal $mealId deleted successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error deleting meal $mealId: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting meals for restaurant $id: ${exception.message}")
            }

        db.collection("restaurants").document(id).delete()
            .addOnSuccessListener {
                Log.d(TAG, "Restaurant $id deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error deleting restaurant $id: ${exception.message}")
            }
    }
    private fun getrestdet(restaurantId: String) {
        db.collection("restaurants")
            .document(restaurantId)
            .get().addOnSuccessListener { x->

                val id = x?.id
                val name = x?.get("name").toString()
                val rate = x?.get("rate")
                val address = x?.get("address").toString()
                val imageUrlString = x?.getString("imageUrl") ?: ""
                val imageUrl = Uri.parse(imageUrlString)


                Log.d("TAG", "name: $name")

                binding.tvItemName.text = name.toString()
                binding.txtvItemRating.text = rate.toString()
                binding.locationAddress.text = address.toString()

                Picasso.get()
                    .load(imageUrl)
                    .into(binding.imageView7)
            }


    }





    private fun getestmeal(restaurantId: String) {
        val data = ArrayList<mealdata>()

        val mealsQuery = db.collection("meals")
            .whereEqualTo("restid", restaurantId)

        val mealsListener = mealsQuery.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e("TAG", exception.message!!)
                return@addSnapshotListener
            }

            data.clear()

            for (document in querySnapshot!!) {

                val id = document.id
                val name = document.getString("name") ?: ""
                val price = document.getString("price") ?: ""
                val rate = document.get("rate")
                val description = document.getString("description") ?: ""
                val restid = document.getString("restid") ?: ""
                val issold = document.getString("issold") ?: ""
                val numofreq = document.get("numofreq").toString().toInt()
                val imageUrlString = document.getString("imageUrl") ?: ""
                val imageUrl = Uri.parse(imageUrlString)
                data.add(
                    mealdata(
                        id,
                        name,
                        price,
                        rate.toString().toDouble(),
                        description,
                        imageUrl,
                        restid,
                        issold,
                        numofreq
                    )
                )
            }

            adapter.updateData(data)
        }


    }

}