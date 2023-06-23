package com.example.farmeraid

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.RootNavigationHost
import com.example.farmeraid.ui.theme.FarmerAidTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var appNavigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContent {
            val navController : NavHostController = rememberNavController()
            appNavigator.setNavController(navController)
            FarmerAidTheme(darkTheme = false) {
                RootNavigationHost(appNavigator)
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