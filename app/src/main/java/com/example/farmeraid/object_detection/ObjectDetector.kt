package com.example.farmeraid.object_detection

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import java.io.File
import java.io.IOException
import java.net.URL

class ObjectDetectionUtility(private val context:Context){
    private val model = LocalModel.Builder()
        .setAssetFilePath("mnasnet_1.3_224_1_metadata_1.tflite")
        .build()
    private var activityContext: Context ?= null
    val labelToProduce: Map<String,String> = mapOf(
        "Granny Smith" to "apple",
    )

    private val mh = ONNXModelHub(context)
   // private val m = mh.loadPretrainedModel(ONNXModels.ObjectDetection.SSDMobileNetV1)


    private val detectorOptions = CustomObjectDetectorOptions.Builder(model)
        .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .setClassificationConfidenceThreshold(0.2f)
        .setMaxPerObjectLabelCount(10)
        .build()


    val objectDetector = ObjectDetection.getClient(detectorOptions)
    val detectionResults : MutableStateFlow<List<String>> = MutableStateFlow(emptyList())


    fun analyze(imageBitmap: Bitmap){
        val res: MutableList<String> = mutableListOf()

        //val imageUri:Uri = uriString.toUri()
        //val image = InputImage.fromFilePath(context,imageUri)
        //val source = ImageDecoder.createSource(activityContext!!.contentResolver, imageUri)
        //val bitmap = ImageDecoder.decodeBitmap(source)
        val image = InputImage.fromBitmap(imageBitmap,0)

        try {
            objectDetector
                .process(image)
                .addOnFailureListener { e-> Log.e("DETECTED", e.message?:"") }
                .addOnSuccessListener { results ->
                    results.forEach{detectedObject ->
                        val label = detectedObject.labels[0].text
                        if (label in labelToProduce){
                            res.add(labelToProduce.get(label)?: "label")
                        }else{
                            res.add(label)
                        }
                        Log.d("DETECTED", label)
                    }
                    detectionResults.value = res
                }
        }catch(e: Exception){
            Log.e("TAG", e.message.toString())
        }

    }


    fun detect(uriString:String){
        try {
            val url = URL(uriString)
                val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val image: Bitmap = BitmapFactory.decodeStream(inputStream)
            // Use the 'image' bitmap as needed
            analyze(image)
        } catch (e: IOException) {
            Log.e("ERROR", e.message?: "UNKNOWN ERROR")
        }
//        val storageReference = FirebaseFirestore.g
//
//        val localFile = File.createTempFile("image", "jpg")
//        storageReference.getFile(localFile).addOnSuccessListener {
//            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//            analyze(bitmap)
//        }.addOnFailureListener{
//            Log.e("ERROR", it.message?: "Unknown error")
//        }
    }
    fun setActivtyContext(aContext: Context) {
        activityContext = aContext
    }



//    fun detectObjects(bitmap: Bitmap){
//        m.use {onnxModel ->
//            val objectsDetected = onnxModel.detectObjects(bitmap,20)
//            objectsDetected.forEach{
//                Log.d("DETECTED", it.label.toString())
//            }
//
//        }
//
//    }

}