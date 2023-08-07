package com.example.test2.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.R
import com.example.test2.databinding.SealcardBinding
import com.example.test2.model.mealdata
import com.example.test2.saledata.saledata

class saleAdapter(var activity: Activity, private var data: ArrayList<saledata>) :
    RecyclerView.Adapter<saleAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: SealcardBinding) : RecyclerView.ViewHolder(binding.root)
    fun updateData(newData: ArrayList<saledata>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SealcardBinding.inflate(activity.layoutInflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.name.text = data[position].name.toString()
        holder.binding.imageView5.setImageResource(R.drawable.burger)


    }


}