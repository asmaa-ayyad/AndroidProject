package com.example.test2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.test2.databinding.ActivitySingupBinding
import com.example.test2.model.admin
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class singup : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySingupBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        val sharedPref=  this.getSharedPreferences("login", Context.MODE_PRIVATE);

        binding.sigupButton.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()
            val admin =admin(username,email,pass)
            db.collection("admin")
                .add(admin)
                .addOnSuccessListener { documentReference ->
                    val editor = sharedPref!!.edit()
                    editor.putString("u",username)
                    editor.putString("p",pass)
                    editor.apply()
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)

                }
                .addOnFailureListener { e ->


        }


        }}}