package com.example.test2.ui.restaurant

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
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
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.test2.R

import com.example.test2.databinding.ActivityLoginnBinding.inflate
import com.example.test2.databinding.FragmentAddrestaurantBinding
import com.example.test2.databinding.FragmentEditrestBinding
import com.example.test2.model.restaurantdata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
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
 * Use the [addrestaurant.newInstance] factory method to
 * create an instance of this fragment.
 */
var fileURI: Uri? = null

class addrestaurant : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    lateinit var  binding :FragmentAddrestaurantBinding
    lateinit var  updates :HashMap<String,Any>

    private var progressDialog: ProgressDialog?=null

    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    val TAG = "hzm"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentAddrestaurantBinding.inflate(inflater, container, false)

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        db = Firebase.firestore
        updates = hashMapOf(
            "salary" to 1
        )
        binding.imageView12.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.addrest.setOnClickListener{
            apdat()
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
                    val restaurant =restaurantdata("",binding.restname.text.toString(),binding.restaddres.text.toString() ,binding.restreat.text.toString().toDouble(),
                        GeoPoint(binding.reatlocation.text.toString().toDouble(),binding.reatlocation2.text.toString().toDouble()), fileURI!!)
                    db.collection("restaurants")
                        .add(restaurant)
                        .addOnSuccessListener { documentReference ->


                            val newRestaurantId = documentReference.id
                            db.collection("restaurants").document(newRestaurantId)
                                .update("r_id", newRestaurantId)
                                .addOnSuccessListener {
                                    Log.d(TAG, "r_id updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to update r_id: ${e.message}")
                                }

                        }
                        .addOnFailureListener { e ->
                        }
                }
                hideDialog()
            }
            val navController = findNavController()
            val navOptions= NavOptions.Builder()
                .setPopUpTo(navController.currentDestination!!.id,true).build()
            navController.navigate(R.id.action_addrestaurant_self,null, navOptions)


        }


        return binding.root
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(requireActivity())
        progressDialog!!.setMessage("Uploading ...")
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

    fun apdat() {
        db.collection("employ")
            .whereGreaterThan("age", 18)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val salary = document.get("salary") .toString().toInt()

                        val newSalary = salary + 100
                        val updates = hashMapOf<String, Any>(
                            "salary" to salary + 100
                        )
                        document.reference.update(updates)
                            .addOnSuccessListener {
                                // Update successful
                            }
                            .addOnFailureListener { e ->
                                // Handle any errors
                            }
                    }

            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }

         }


