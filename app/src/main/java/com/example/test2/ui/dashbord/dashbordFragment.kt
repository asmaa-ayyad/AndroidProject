package com.example.test2.ui.dashbord
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.test2.MainActivity

import com.example.test2.R
import com.example.test2.adapter.mealAdapter
import com.example.test2.adapter.restAdapter
import com.example.test2.adapter.saleAdapter
import com.example.test2.databinding.FragmentDashbordBinding
import com.example.test2.loginn
import com.example.test2.model.mealdata
import com.example.test2.model.restaurantdata
import com.example.test2.saledata.saledata
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlin.collections.ArrayList
class dashbordFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var pieChart2: PieChart
    private lateinit var pieEntryList: MutableList<PieEntry>
    private lateinit var pieDataSet: PieDataSet

    lateinit var db: FirebaseFirestore
    lateinit var binding: FragmentDashbordBinding
    private lateinit var adapter: mealAdapter
    lateinit var sharedPref: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        setHasOptionsMenu(true)
        binding = FragmentDashbordBinding.inflate(inflater, container, false)
        sharedPref = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore



        pieEntryList = mutableListOf()


        setUpChart()
        setUpChart2()

        val data = ArrayList<mealdata>()

        binding.RecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        adapter = mealAdapter(requireActivity(), data)

        binding.RecyclerView.adapter = adapter

        binding.RecyclerView.setHasFixedSize(true)
        binding.RecyclerView.isNestedScrollingEnabled = true
        getSoldMeals()
    }


    private fun setUpChart2() {
        val barChart = binding.chart2
        val barEntries = mutableListOf<BarEntry>()

        val barDataSet = BarDataSet(barEntries, "Top 3 Meals")
        barDataSet.setColors(
            Color.parseColor("#DEEBFB"),
            Color.parseColor("#DCD0FC"),
            Color.parseColor("#FFC107")
        )
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barChart.data = barData

        val legend = barChart.legend
        legend.isEnabled = true
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.form = Legend.LegendForm.SQUARE
        legend.formSize = 10f
        legend.textSize = 12f
        legend.xEntrySpace = 60f

        val setCustomLegendEntries = {
            barDataSet.setColors(
                Color.parseColor("#DEEBFB"),
                Color.parseColor("#DCD0FC"),
                Color.parseColor("#FFC107")
            )
            barDataSet.valueTextColor = Color.WHITE
            barDataSet.valueTextSize = 12f

            val barData = BarData(barDataSet)

            val barChart = binding.chart2
            barChart.data = barData
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = true
            barChart.xAxis.isEnabled = false
            barChart.axisLeft.axisMinimum = 0f
            barChart.axisRight.isEnabled = false
            barChart.animateY(1000, Easing.EaseInOutQuad)

            val legend = barChart.legend
            legend.isEnabled = true
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.form = Legend.LegendForm.SQUARE
            legend.formSize = 10f
            legend.textSize = 12f
            legend.xEntrySpace = 60f

            val legendEntries: MutableList<LegendEntry> = ArrayList()
            for (i in barEntries.indices) {
                val entry = LegendEntry()
                entry.formColor = barDataSet.getColor(i)
                entry.label = barEntries[i].data.toString()
                legendEntries.add(entry)
            }
            legend.setCustom(legendEntries)

        }

        db.collection("meals")
            .whereGreaterThan("numofreq", 0)
            .orderBy("numofreq", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.e("TAG", exception.message!!)
                    return@addSnapshotListener
                }

                barEntries.clear()
                var index = 1f

                for (document in querySnapshot!!) {
                    val data = document.data
                    val name = data["name"].toString()
                    val amount = data["numofreq"].toString().toFloat()
                    barEntries.add(BarEntry(index, amount, name))
                    index += 1f
                }

                barDataSet.notifyDataSetChanged()
                barChart.notifyDataSetChanged()
                barChart.invalidate()

                setCustomLegendEntries()
            }
    }

    private fun setUpChart() {
        pieChart = binding.chart
        gettopratedrest()





    }

    private fun gettopratedrest() {
        val pieChart = binding.chart

         pieEntryList = mutableListOf<PieEntry>()

        val setValues = {
            val pieDataSet = PieDataSet(pieEntryList, "Top Rated Restaurants")
            val colors = intArrayOf(
                Color.parseColor("#DEEBFB"),
                Color.parseColor("#DCD0FC"),
                Color.parseColor("#FCD7FC")
            )

            val colorsList: MutableList<Int> = colors.toMutableList()

            pieDataSet.colors = colors.toList()
            pieDataSet.valueTextColor = Color.WHITE
            pieDataSet.valueTextSize = 12f
            pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            pieDataSet.sliceSpace = 4f
            pieDataSet.selectionShift = 8f

            val pieData = PieData(pieDataSet)
            pieChart.data = pieData

            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = false
            pieChart.isRotationEnabled = false
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setEntryLabelTextSize(10f)
            pieChart.animateY(1000, Easing.EaseInOutQuad)
            pieChart.invalidate()

            pieDataSet.colors = colorsList
            pieDataSet.valueTextColor = resources.getColor(R.color.white)

            pieData.setValueTextSize(12f)
            pieChart.data = pieData
            pieChart.invalidate()




        }

        db.collection("restaurants")
            .orderBy("rate", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.e("TAG", exception.message!!)
                    return@addSnapshotListener
                }

                pieEntryList.clear()

                for (document in querySnapshot!!) {
                    val data = document.data
                    val name = data["name"].toString()
                    val amount = data["rate"].toString().toDouble()

                    pieEntryList.add(PieEntry(amount.toFloat(), name))
                }

                setValues()
            }
    }

    private fun getSoldMeals() {
        val data = ArrayList<mealdata>()

        db.collection("meals")
            .whereEqualTo("issold", "1")
            .addSnapshotListener { querySnapshot, exception ->
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

                adapter.updateData(data)  }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {

                val editor = sharedPref.edit()
                editor.clear()
                editor.commit()

                val i = Intent(requireActivity(), loginn::class.java)
                startActivity(i)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


}












