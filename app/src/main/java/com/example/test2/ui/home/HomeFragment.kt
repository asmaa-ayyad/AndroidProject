package com.example.test2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.test2.R
import com.example.test2.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.restbuton.setOnClickListener {

            val navController = findNavController()
            val navOptions= NavOptions.Builder()
                .setPopUpTo(navController.currentDestination!!.id,true).build()
            navController.navigate(R.id.action_navigation_home_to_restaurant,null, navOptions)
        }

        binding.mealbuton.setOnClickListener {

            val navController = findNavController()
            val navOptions= NavOptions.Builder()
                .setPopUpTo(navController.currentDestination!!.id,true).build()
            navController.navigate(R.id.action_navigation_home_to_restaurant,null, navOptions)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}