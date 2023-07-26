package com.example.farmeraid.fridge_details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.CharityRepository
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.farm.model.FarmModel
import com.example.farmeraid.fridge.model.FridgeModel
import com.example.farmeraid.fridge_details.model.FridgeDetailsModel
import com.example.farmeraid.fridge_details.model.getDonateButton
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.object_detection.ObjectDetectionUtility
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.android.gms.common.util.IOUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import javax.inject.Inject

@HiltViewModel
class FridgeDetailsViewModel @Inject constructor (
    savedStateHandle: SavedStateHandle,
    private val objectDetectionUtility: ObjectDetectionUtility,
    private val inventoryRepository: InventoryRepository,
    private val charityRepository: CharityRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
    @ApplicationContext private val  context: Context
    ): ViewModel(){
    private val _state = MutableStateFlow(
        FridgeDetailsModel.FridgeDetailViewState(
            donateButtonUiState = getDonateButton(),
            fridgeDetails = null
        )
    )

    private val fridgeId: String = savedStateHandle["fridgeId"] ?: ""

    val state: StateFlow<FridgeDetailsModel.FridgeDetailViewState>
        get() = _state

    private val fridgeInventory: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    private val farmProduces: MutableStateFlow<List<FarmModel.ProduceHarvest>> = MutableStateFlow(
        listOf())
    private val fridgeDetails: MutableStateFlow<FridgeModel.Fridge?> = MutableStateFlow(_state.value.fridgeDetails)
    private val donateButtonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.donateButtonUiState)

    init{
        if (fridgeId == ""){
            appNavigator.navigateBack()
        }
    }
    init {
        viewModelScope.launch {
            combine(fridgeInventory, farmProduces, fridgeDetails, donateButtonUiState) {
                    fridgeInventory: List<String>,
                    farmProduces: List<FarmModel.ProduceHarvest>,
                    fridgeDetails: FridgeModel.Fridge?,
                    donateButtonUiState: UiComponentModel.ButtonUiState ->
                FridgeDetailsModel.FridgeDetailViewState(
                    fridgeInventory = fridgeInventory,
                    farmProduces = farmProduces,
                    fridgeDetails =  fridgeDetails,
                    donateButtonUiState = donateButtonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    //Getting the results from model
    init {
        viewModelScope.launch {
            objectDetectionUtility.detectionResults.collect{results ->
                fridgeInventory.value = results.filter{
                        res -> res.lowercase() in farmProduces.value.map { it.produceName.lowercase() }
                }
            }
        }
    }

    //Get all our farm's produces
    init {
        viewModelScope.launch{
            inventoryRepository.getInventory().collect{ produce ->
                produce.data?.let {
                    farmProduces.value = it.map{(produceName, _) ->
                        FarmModel.ProduceHarvest(
                            produceName = produceName,
                            produceCount = 0
                        )
                    }
                }
            }
        }
    }

    //Get the fridge details -> this should have the image
    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                charityRepository.getCharity(fridgeId).let { response ->
                    response.data?.let { data ->
                        Log.d("FRIDGE DATA", data.imageUri)

                        objectDetectionUtility.detect(data.imageUri);

                        fridgeDetails.value = data
                    } ?: run {
                        snackbarDelegate.showSnackbar(response.error ?: "Unknown error")
                    }
                }
            }
        }
    }

//    init {
//        val inputStream = context.assets.open("fridge3.jpg")
//        val imageBitmap = BitmapFactory.decodeStream(inputStream)
//        //objectDetectionUtility.detectObjects(imageBitmap)
//        objectDetectionUtility.analyze(imageBitmap, 0);
//    }

    fun setProduceCount(produceName : String, count : Int?) {
        farmProduces.value = farmProduces.value.map { produceHarvest ->
            FarmModel.ProduceHarvest(
                produceName = produceHarvest.produceName,
                produceCount = if (produceName == produceHarvest.produceName) count else produceHarvest.produceCount
            )
        }
    }

    fun incrementProduceCount(produceName : String) {
        farmProduces.value = farmProduces.value.map { produceHarvest ->
            FarmModel.ProduceHarvest(
                produceName = produceHarvest.produceName,
                produceCount = (produceHarvest.produceCount?:0) + if (produceName == produceHarvest.produceName) 1 else 0
            )
        }
    }

    fun decrementProduceCount(produceName : String) {
        farmProduces.value = farmProduces.value.map { produceHarvest ->
            FarmModel.ProduceHarvest(
                produceName = produceHarvest.produceName,
                produceCount = (produceHarvest.produceCount?:1) - if (produceName == produceHarvest.produceName) 1 else 0
            )
        }
    }

    fun navigateToFridge() {
       snackbarDelegate.showSnackbar("Navigate To Edit Fridge Page")
    }

    fun navigateBack(){
        appNavigator.navigateBack()
    }

    fun donateProduce(){
        snackbarDelegate.showSnackbar("Navigate To Edit Fridge Page")
    }
}