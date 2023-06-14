package com.example.farmeraid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener { logoutUser(it) }
    }
    fun logoutUser(view: View) {
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)


        // Additional cleanup or navigation code after logging out
    }
}