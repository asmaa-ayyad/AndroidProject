package com.example.test2.ui.restaurant

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.test2.R
import com.example.test2.adapter.restAdapter
import com.example.test2.databinding.RestrecyclerBinding
import com.example.test2.model.restaurantdata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class restaurant : Fragment()  {
    private var _binding: RestrecyclerBinding? = null
    lateinit var data : ArrayList<restaurantdata>

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: restAdapter
    private val binding get() = _binding!!
    private var param1: String? = null
    private var param2: String? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        _binding = RestrecyclerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = Firebase.firestore
        data = ArrayList()
        adapter = restAdapter(requireActivity(), data)

     //   getrst()

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        getrst()
    }

    private fun getrst() {
        //data = ArrayList<restaurantdata>()

        db.collection("restaurants")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.e("TAG", exception.message!!)
                    return@addSnapshotListener
                }

                data.clear()

                for (document in querySnapshot!!) {
                    val r_id = document.id
                    val name = document.getString("name") ?: ""
                    val address = document.getString("address") ?: ""
                    val rate = document.get("rate")
                    val locationGeoPoint = document.getGeoPoint("location")
                    //val location = GeoPoint(locationGeoPoint!!.latitude, locationGeoPoint.longitude)

                    val imageUrlString = document.getString("imageUrl") ?: ""
                    val imageUrl = Uri.parse(imageUrlString)

                    data.add(
                        restaurantdata(
                            r_id,
                            name,
                            address,
                            rate.toString().toDouble(),
                            locationGeoPoint!!,
                            imageUrl
                        )
                    )
                }

                binding.recyclerView2.layoutManager =
                    StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
                binding.recyclerView2.adapter = adapter
            }
    }





    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addmenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {

                val navController = findNavController()
                val navOptions= NavOptions.Builder()
                    .setPopUpTo(navController.currentDestination!!.id,true).build()
                navController.navigate(R.id.action_restaurant_to_addrestaurant,null, navOptions)

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


}