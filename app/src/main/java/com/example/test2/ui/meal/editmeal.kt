package com.example.test2.ui.meal

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.test2.R
import com.example.test2.databinding.ActivityEditmealBinding
import com.example.test2.databinding.FragmentAddmealBinding
import com.example.test2.model.mealdata
import com.example.test2.model.restaurantdata
import com.example.test2.ui.restaurant.fileURI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

private lateinit var db: FirebaseFirestore

private var progressDialog: ProgressDialog?=null

private val PICK_IMAGE_REQUEST = 111
lateinit var  binding : ActivityEditmealBinding
var imageURI: Uri? = null
val TAG = "hzm"


class editmeal : AppCompatActivity() {
    var issold =  ""
    var numofreq = 0

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding= ActivityEditmealBinding.inflate(layoutInflater)
    setContentView(binding.root)
 val  resttId2 = intent.getStringExtra("restidforedit")!!
    val mealId = intent.getStringExtra("Midforedit")!!





    val storage = Firebase.storage
    val storageRef = storage.reference
    val imageRef = storageRef.child("images")
    db = Firebase.firestore
    getmealdet(mealId)




    binding.imageView12.setOnClickListener {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    binding.updatemealrest.setOnClickListener{
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
                val meal = mealdata(mealId,binding.mealname.text.toString(),binding.mealprice.text.toString() ,binding.mealreat.text.toString().toDouble(),binding.mealdescription.text.toString(), fileURI!!,
                    resttId2,issold,numofreq)
                updatemealById(meal)
            }
            hideDialog()
        }



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
    private fun getmealdet(mealId: String) {
        db.collection("meals")
            .document(mealId)
            .get()
            .addOnSuccessListener { querySnapshot ->

                val id = querySnapshot.id
                val Mname = querySnapshot.getString("name") ?: ""
                val Mprice = querySnapshot.getString("price") ?: ""
                val Mrate = querySnapshot.get("rate")
                val Mdescription = querySnapshot.getString("description") ?: ""
                val restid = querySnapshot.getString("restid") ?: ""
                val MimageUrlString = querySnapshot.getString("imageUrl") ?: ""
                val MimageUrl = Uri.parse(MimageUrlString)
                 issold = querySnapshot.getString("issold") ?: ""
                 numofreq = querySnapshot.get("numofreq").toString().toInt()

                binding.mealname.setText(Mname.toString())
                binding.mealreat.setText(Mrate.toString())
                binding.mealdescription.setText(Mdescription.toString())
                binding.mealprice.setText(Mprice.toString())
                Picasso.get()
                    .load(MimageUrl)
                    .into(binding.imageView12)
                return@addOnSuccessListener
            }

            .addOnFailureListener { exception ->
                Log.e("TAG", exception.message!!)
            }
    }}


private fun updatemealById(meal: mealdata){
    db.collection("meals")
        .document(meal.m_id)

        .set(meal)
        .addOnSuccessListener {
            Log.e("hzm","Student updated Successfully")
        }
        .addOnFailureListener {
            Log.e("hzm",it.message!!)

        }
}



