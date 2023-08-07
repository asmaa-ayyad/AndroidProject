

package com.example.test2.ui.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2.MainActivity
import com.example.test2.R
import com.example.test2.databinding.ActivityLoginnBinding
import com.example.test2.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [login.newInstance] factory method to
 * create an instance of this fragment.
*/
class login : Fragment() {
// TODO: Rename and change types of parameters
private var param1: String? = null
private var param2: String? = null



override fun onCreateView(
inflater: LayoutInflater, container: ViewGroup?,
savedInstanceState: Bundle?
): View? {

val binding = ActivityLoginnBinding.inflate(inflater, container, false)
val sharedPref=  this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE);




binding.loginButton.setOnClickListener {
val pass=binding.username.text.toString()
val em= binding.password.text.toString()



val editor = sharedPref!!.edit()
editor.putString("username",em)
editor.putString("pass",pass)
editor.apply()

    val i = Intent(requireActivity(), MainActivity::class.java)
   startActivity(i)

}
















return binding.root
}}