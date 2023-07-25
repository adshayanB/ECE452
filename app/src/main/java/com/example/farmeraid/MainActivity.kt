package com.example.farmeraid

import FarmScreenView
import com.example.farmeraid.snackbar.SnackbarDelegate
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.RootNavigationHost
import com.example.farmeraid.speech_recognition.KontinuousSpeechRecognizer
import com.example.farmeraid.speech_recognition.SpeechRecognizerUtility
import com.example.farmeraid.ui.theme.FarmerAidTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var snackbarDelegate: SnackbarDelegate

    @Inject
    lateinit var speechRecognizer: SpeechRecognizerUtility

    @Inject
    lateinit var kontinuousSpeechRecognizer: KontinuousSpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        //kontinuousSpeechRecognizer.setActivtyContext(this)
        //kontinuousSpeechRecognizer.initializeRecognizer()

        speechRecognizer.setActivtyContext(this)
        setContent {
            val navController : NavHostController = rememberNavController()
            appNavigator.setNavController(navController)

            val snackbarHostState = remember { SnackbarHostState() }
            snackbarDelegate.snackbarHostState = snackbarHostState
            snackbarDelegate.coroutineScope = rememberCoroutineScope()

            FarmerAidTheme(darkTheme = false) {
                RootNavigationHost(appNavigator, snackbarHostState)
            }
        }
    }

    override fun onDestroy(){
        kontinuousSpeechRecognizer.onDestroy()
        super.onDestroy()
    }
}