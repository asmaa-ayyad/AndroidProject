package com.example.test2.ui.meal

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
import com.example.test2.databinding.ActivityRestdetailacrivityBinding
import com.example.test2.databinding.FragmentAddmealBinding
import com.example.test2.databinding.FragmentAddrestaurantBinding
import com.example.test2.model.mealdata
import com.example.test2.model.restaurantdata
import com.example.test2.ui.restaurant.fileURI
import com.example.test2.ui.restaurant.restdetailacrivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addmeal.newInstance] factory method to
 * create an instance of this fragment.
 */
class addmeal : AppCompatActivity() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var db: FirebaseFirestore

    private var progressDialog: ProgressDialog?=null

    private val PICK_IMAGE_REQUEST = 111
    lateinit var  binding :FragmentAddmealBinding
    var imageURI: Uri? = null
    val TAG = "hzm"



        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding= FragmentAddmealBinding.inflate(layoutInflater)
        setContentView(binding.root)
          val  restaurantId2 = intent.getStringExtra("asmaa2")!!
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

        binding.addmealrest.setOnClickListener{
            showDialog()
            // Get the data from an ImageView as bytes
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
                    val meal = mealdata("",binding.mealname.text.toString(),binding.mealprice.text.toString() ,binding.mealreat.text.toString().toDouble(),binding.mealdescription.text.toString(), fileURI!!,restaurantId2!!,"",0)
                    db.collection("meals")
                        .add(meal)
                        .addOnSuccessListener { documentReference ->
                            val mealId = documentReference.id

                        }
                        .addOnFailureListener { e ->
                        }
                }
                hideDialog()
            }

        }

        binding.button.setOnClickListener {

        }

    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading  ...")
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



}