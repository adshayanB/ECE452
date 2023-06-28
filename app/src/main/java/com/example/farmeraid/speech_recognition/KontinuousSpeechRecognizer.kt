package com.example.farmeraid.speech_recognition

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.github.stephenvinouze.core.interfaces.RecognitionCallback
import com.github.stephenvinouze.core.managers.KontinuousRecognitionManager
import com.github.stephenvinouze.core.models.RecognitionStatus
import kotlinx.coroutines.flow.MutableStateFlow

class KontinuousSpeechRecognizer : RecognitionCallback {
    private var activityContext: Context?= null
    private val ACTIVATION_KEYWORD = "OK Google"
    var recognitionManager: KontinuousRecognitionManager ?= null
    val speechRecognizerResult : MutableStateFlow<String> = MutableStateFlow("")


    override fun onPrepared(status: RecognitionStatus){}
    override fun onBeginningOfSpeech(){}
    override fun onKeywordDetected() {
        speechRecognizerResult.value = "Keywordetected"
    }
    override fun onReadyForSpeech(params: Bundle){}
    override fun onBufferReceived(buffer: ByteArray){}
    override fun onRmsChanged(rmsdB: Float){}
    override fun onPartialResults(results: List<String>){
        val text = results.joinToString(separator = "\n")
        speechRecognizerResult.value = text
    }
    override fun onResults(results: List<String>, scores: FloatArray?){
        val text = results.joinToString(separator = "\n")
        speechRecognizerResult.value = text
    }
    override fun onError(errorCode: Int){}
    override fun onEvent(eventType: Int, params: Bundle){}
    override fun onEndOfSpeech(){}



    fun initializeRecognizer(){
        recognitionManager = KontinuousRecognitionManager(activityContext!!, activationKeyword = ACTIVATION_KEYWORD, shouldMute = false, callback = this)
        recognitionManager?.createRecognizer()

        if (ContextCompat.checkSelfPermission(activityContext!!, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activityContext as Activity, arrayOf(RECORD_AUDIO), 101)
        }
    }

    fun onDestroy() {
        recognitionManager?.destroyRecognizer()
    }

    fun onResume() {
        if (ContextCompat.checkSelfPermission(activityContext!!, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecognition()
        }
    }

    fun onPause() {
        stopRecognition()
    }

    fun startRecognition(){
        recognitionManager?.startRecognition()
    }
    fun stopRecognition(){
        recognitionManager?.stopRecognition()
    }

    fun isPermissionGranted(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(activityContext as Context, RECORD_AUDIO)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }


    fun setActivtyContext(context: Context) {
        activityContext = context
    }
}