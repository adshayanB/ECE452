package com.example.farmeraid.speech_recognition

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.flow.MutableStateFlow
import java.security.AccessController.getContext

//need to return result after text to update values
//parser logic can be done in the respective screen viewmodel
class SpeechRecognizerUtility() {
    private var speechRecognizer: SpeechRecognizer? = null
    private val permission = Manifest.permission.RECORD_AUDIO
    private var activityContext: Context ?= null

    private val recognizerIntent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,60000)
            //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,60000)
            //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,1000)

        }
    }

    val speechRecognizerResult :MutableStateFlow<String> = MutableStateFlow("")

    private val recognitionListener = object : RecognitionListener{
        override fun onReadyForSpeech(bundle: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(v: Float) {}
        override fun onBufferReceived(bytes: ByteArray?) {}
        override fun onEndOfSpeech() {
            startSpeechRecognition()
        }
        override fun onError(i: Int) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onResults(results: Bundle?) {
            val res = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            speechRecognizerResult.value = res?.get(0) ?: ""
            startSpeechRecognition()
        }
    }

    fun isPermissionGranted(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(activityContext as Context, permission)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }
    fun startSpeechRecognition() {
        if (isPermissionGranted()) {
            Log.d("MESSAGE", "Permission Granted")
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activityContext).apply {
                setRecognitionListener(recognitionListener)
                startListening(recognizerIntent)
            }
        } else {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
                // Open permission screen
                Log.d("MESSAGE", "Asking for permission")
                val request_audio_permission_code: Int = 1
                ActivityCompat.requestPermissions(activityContext as Activity, arrayOf(RECORD_AUDIO), request_audio_permission_code)
                //ActivityCompat.requestPermissions(context, [RECORD_AUDIO], REQUEST_AUDIO_PERMISSION_CODE )
                //val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                //startActivity(context, intent, null)

                //Toast message
                Toast.makeText(activityContext, "Allow Permission", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun stopSpeechRecognition() {
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
    }

    fun setActivtyContext(context: Context) {
        activityContext = context
    }
}