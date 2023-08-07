package com.example.test2.adapter

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.R
import com.example.test2.databinding.ActivityMealdetailBinding
import com.example.test2.databinding.MealcardBinding
import com.example.test2.databinding.SealcardBinding
import com.example.test2.model.mealdata
import com.example.test2.model.restaurantdata
import com.example.test2.ui.meal.mealdetail
import com.example.test2.ui.restaurant.restdetailacrivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class mealAdapter (var activity: Activity, var data: ArrayList<mealdata>) :
    RecyclerView.Adapter<mealAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: MealcardBinding) : RecyclerView.ViewHolder(binding.root)
    fun updateData(newData: ArrayList<mealdata>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MealcardBinding.inflate(activity.layoutInflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        lateinit var db: FirebaseFirestore

        holder.binding.name.text = data[position].name.toString()
        holder.binding.rate.text = data[position].price.toString()
        Picasso.get()
            .load(data[position].imageUrl)
            .into(holder.binding.imageView5)
        holder.binding.mealdetails.setOnClickListener {

            val i = Intent (activity, mealdetail::class.java)
            i.putExtra("Mid",data[position].m_id )
            activity.startActivity(i)


        }
        holder.binding.menuButtonmeal.setOnClickListener {
            db = Firebase.firestore
            val popupMenu = PopupMenu(activity, holder.binding.menuButtonmeal)
            popupMenu.menuInflater.inflate(R.menu.rest, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit -> {


                        db.collection("meals")
                            .document(data[position].m_id)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val mealId = querySnapshot.id
                                db.collection("meals").document(mealId).delete()
                                val updatedData = ArrayList(data)
                                updatedData.removeAt(position)
                                updateData(updatedData)

                            }
                            .addOnFailureListener { exception ->
                            }
                    }                    }





                true
            }
            popupMenu.show()}}}



