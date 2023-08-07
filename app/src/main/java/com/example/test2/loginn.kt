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
import com.example.test2.databinding.ActivityLoginnBinding
import com.example.test2.databinding.ActivityRestdetailacrivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class loginn : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityLoginnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityLoginnBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val sharedPref=  this.getSharedPreferences("login", Context.MODE_PRIVATE);

        binding.loginButton.setOnClickListener {
            val pass=binding.username.text.toString()
            val em= binding.password.text.toString()
            if(binding.password.text.trim().isEmpty()&&binding.username.text.trim().isEmpty()){
                binding.password.error="password requird"
                binding.username.error="email requird"
                return@setOnClickListener
            }



         searchUser(pass,em)


        }

binding.signupText.setOnClickListener {

    val i = Intent(this, singup::class.java)
    startActivity(i)


}



}

    val usersCollection = db.collection("admin")

    fun searchUser(username: String, password: String) {
        usersCollection.whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val documentUsername = document.getString("username")
                        val documentPassword = document.getString("password")

                        if (documentUsername == username && documentPassword == password) {
                            val sharedPref = getSharedPreferences("login", MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            editor.putString("u", username)
                            editor.putString("p", password)
                            editor.apply()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            return@addOnSuccessListener
                        }

                    }
                }else { binding.username.error="email or password dont match "

                    binding.password.error="email or password dont match"
                }

                     }
            .addOnFailureListener { e ->
                        }
    }


}