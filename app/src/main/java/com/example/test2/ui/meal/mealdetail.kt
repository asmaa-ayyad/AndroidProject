package com.example.test2.ui.meal

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.test2.R
import com.example.test2.adapter.mealAdapter
import com.example.test2.databinding.ActivityEditmealBinding
import com.example.test2.databinding.ActivityMealdetailBinding
import com.example.test2.databinding.ActivityRestdetailacrivityBinding
import com.example.test2.model.mealdata
import com.example.test2.ui.restaurant.restdetailacrivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class mealdetail : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

var restid =""
    private lateinit var binding:ActivityMealdetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMealdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore

        val mealId = intent.getStringExtra("Mid")!!


binding.editmeal2.setOnClickListener {

    val i = Intent (this, editmeal::class.java)
    i.putExtra("Midforedit",mealId )
    i.putExtra("restidforedit",restid )
    startActivity(i)

}

    }
    private fun getmealdet(mealId: String) {
        db.collection("meals")
            .document(mealId)
















            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.e("TAG", exception.message!!)
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val id = documentSnapshot.id
                    val Mname = documentSnapshot.getString("name") ?: ""
                    val Mprice = documentSnapshot.getString("price") ?: ""
                    val Mrate = documentSnapshot.get("rate")
                    val Mdescription = documentSnapshot.getString("description") ?: ""
                    restid = documentSnapshot.getString("restid") ?: ""
                    val MimageUrlString = documentSnapshot.getString("imageUrl") ?: ""
                    val MimageUrl = Uri.parse(MimageUrlString)

                    binding.tvItemName.text = Mname.toString()
                    binding.txtvItemRating.text = Mrate.toString()
                    binding.tvItemDetail.text = Mdescription.toString()
                    binding.tvItemPrice.text = Mprice.toString()

                    Picasso.get()
                        .load(MimageUrl)
                        .into(binding.mealImage)
                } else {
                }
            }
    }

    private fun updateStudentById(student:mealdata){
        db.collection("COLLECTION_NAME")
            .document(student.m_id)
            //.update("average",90)
            //.update("average", FieldValue.delete())
            .set(student)//هاد للopject الهاش بدون set
            .addOnSuccessListener {
                Log.e("hzm","Student updated Successfully")
            }
            .addOnFailureListener {
                Log.e("hzm",it.message!!)
            }
    }


    override fun onResume() {
        super.onResume()

        val mealId = intent.getStringExtra("Mid")!!
        getmealdet(mealId)
    }
    }