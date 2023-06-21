package com.example.farmeraid

import HomeScreenView
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.farmeraid.sign_in.views.SignInScreenView
import com.example.farmeraid.ui.theme.FarmerAidTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContent {
            FarmerAidTheme {
//                HomeScreenView()
                SignInScreenView()
            }
        }
//        setContentView(R.layout.activity_main)
//        val logoutButton = findViewById<Button>(R.id.logoutButton)
//        logoutButton.setOnClickListener { logoutUser(it) }
    }
    fun logoutUser(view: View) {
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)


        // Additional cleanup or navigation code after logging out
    }
}