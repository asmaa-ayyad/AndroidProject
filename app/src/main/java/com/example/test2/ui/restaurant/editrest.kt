package com.example.test2.ui.restaurant

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.test2.R
import com.example.test2.adapter.mealAdapter
import com.example.test2.databinding.ActivityRestdetailacrivityBinding
import com.example.test2.databinding.FragmentEditrestBinding
import com.example.test2.model.restaurantdata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editrest.newInstance] factory method to
 * create an instance of this fragment.
 */
class editrest : AppCompatActivity() {
    // TODO: Rename and change types of parameters

    private lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog?=null

    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    val TAG = "hzm"
    private lateinit var adapter: mealAdapter
    private lateinit var binding:FragmentEditrestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= FragmentEditrestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val restaurantId = intent.getStringExtra("editid")!!
        db = Firebase.firestore

        getrestder(restaurantId)

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        db = Firebase.firestore

        binding.imageView12.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

       binding.updaterest.setOnClickListener{
            showDialog()
            val bitmap = (binding.imageView12.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val childRef = imageRef.child(System.currentTimeMillis().toString() + "_hzmimages.png")
            var uploadTask = childRef.putBytes(data)
            uploadTask.addOnFailureListener { exception ->
                Log.e(TAG, exception.message!!)
                hideDialog()
            }.addOnSuccessListener {

                Log.e(TAG, "Image Uploaded Successfully")
                childRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.e(TAG, uri.toString())
                    fileURI = uri
                    val restaurant = restaurantdata(restaurantId,binding.restname.text.toString(),binding.restaddres.text.toString() ,binding.restreat.text.toString().toDouble(),
                        GeoPoint(binding.reatlocation.text.toString().toDouble(),binding.reatlocation3.text.toString().toDouble()), fileURI!!)
                    updaterestById(restaurant)
                }
                hideDialog()


            }

        }











       }
    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading image ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog(){
        if(progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            Log.e(TAG, imageURI.toString())
            binding.imageView12.setImageURI(imageURI)
        }
    }
    private fun getrestder(restaurantId: String) {
        db.collection("restaurants")
            .document(restaurantId)
            .get()
            .addOnSuccessListener { querySnapshot ->

                val id = querySnapshot.id
                val name = querySnapshot.get("name").toString()
                val rate = querySnapshot.get("rate")
                val locationGeoPoint = querySnapshot.getGeoPoint("location")
                val location = GeoPoint(locationGeoPoint!!.latitude, locationGeoPoint.longitude)
                val address = querySnapshot.get("address").toString()
                val imageUrlString = querySnapshot.getString("imageUrl") ?: ""
                val imageUrl = Uri.parse(imageUrlString)
                Log.d("TAG", "name: $name")

                binding.restname.setText(name.toString())
                binding.restaddres.setText(address.toString())
                binding.restreat.setText(rate.toString())
                binding.reatlocation.setText(locationGeoPoint!!.latitude.toString())
                binding.reatlocation3.setText(locationGeoPoint.longitude.toString())


                Picasso.get()
                    .load(imageUrl)
                    .into(binding.imageView12)
                return@addOnSuccessListener
            }

            .addOnFailureListener { exception ->
                Log.e("TAG", exception.message!!)
            }
    }





    private fun updaterestById(rest:restaurantdata){
        db.collection("restaurants")
            .document(rest.r_id)
            .set(rest)
            .addOnSuccessListener {
                Log.e("hzm","Student updated Successfully")
            }
            .addOnFailureListener {
                Log.e("hzm",it.message!!)
            }
    }



}