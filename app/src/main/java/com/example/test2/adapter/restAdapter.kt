package com.example.test2.adapter

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.R
import com.example.test2.databinding.RestcardBinding
import com.example.test2.databinding.SealcardBinding
import com.example.test2.model.mealdata
import com.example.test2.model.restaurantdata
import com.example.test2.ui.restaurant.restdetailacrivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class restAdapter (var activity: Activity, var data: ArrayList<restaurantdata>) :
    RecyclerView.Adapter<restAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: RestcardBinding) : RecyclerView.ViewHolder(binding.root)
    fun updateData(newData: ArrayList<restaurantdata>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RestcardBinding.inflate(activity.layoutInflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
         lateinit var db: FirebaseFirestore



        holder.binding.name.text = data[position].name.toString()
        holder.binding.rate.text = data[position].address.toString()
        Picasso.get()
            .load(data[position].imageUrl)
            .into(holder.binding.imageView5)
        holder.binding.details.setOnClickListener {

            val i = Intent (activity, restdetailacrivity::class.java)
            i.putExtra("asmaa",data[position].r_id )
            activity.startActivity(i)


        }








        holder.binding.menuButton.setOnClickListener {
            db = Firebase.firestore
            val popupMenu = PopupMenu(activity, holder.binding.menuButton)
            popupMenu.menuInflater.inflate(R.menu.rest, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit -> {


                            db.collection("meals")
                                .whereEqualTo("restid", data[position].r_id)
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
                                }

                            db.collection("restaurants").document(data[position].r_id).delete()
                                .addOnSuccessListener {

                                }
                                .addOnFailureListener { exception ->
                                }
                        }                    }





                true
            }
            popupMenu.show()}}





}
