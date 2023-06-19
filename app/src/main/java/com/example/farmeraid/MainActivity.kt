package com.example.farmeraid

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var micInput: ImageView
    private lateinit var speakOut: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener { logoutUser(it) }

        micInput = findViewById(R.id.mic_in)
        speakOut = findViewById(R.id.speak_out)

        micInput.setOnClickListener {
            checkPerms()
            speech2Text()
        }
    }
    fun logoutUser(view: View) {
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        // Additional cleanup or navigation code after logging out
    }


    fun speech2Text() {
       val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
       val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
       recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )

       recognizerIntent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE,
        Locale.getDefault()
       )
//Template speech code from: https://www.geeksforgeeks.org/offline-speech-to-text-without-any-popup-dialog-in-android/
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val res = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (res != null) {
                    speakOut.text = res[0]
                }
            }
            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
        })
        speechRecognizer.startListening(recognizerIntent)
    }

    fun checkPerms() {
        //OS must be greater or equal to version 23
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
            if(ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                // Open permission screen
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null))
                startActivity(intent)

                //Toast message
                Toast.makeText(this, "Allow Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}