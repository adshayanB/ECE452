package com.example.farmeraid.farm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.farm.model.FarmModel
import com.example.farmeraid.farm.model.FarmModel.FarmViewState
import com.example.farmeraid.farm.model.getMicButton
import com.example.farmeraid.farm.model.getStopButton
import com.example.farmeraid.farm.model.getSubmitButton
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.speech_recognition.KontinuousSpeechRecognizer
import com.example.farmeraid.speech_recognition.SpeechRecognizerUtility
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmViewModel @Inject constructor(
    inventoryRepository: InventoryRepository,
    private val speechRecognizerUtility: SpeechRecognizerUtility,
    private val kontinuousSpeechRecognizer: KontinuousSpeechRecognizer,
    private val appNavigator: AppNavigator,
): ViewModel() {
    private val _state = MutableStateFlow(FarmViewState(
        micFabUiState = getMicButton(),
        micFabUiEvent = getMicButtonEvent(),
        submitButtonUiState = getSubmitButton(),
        speechResult = ""
    ))
    val state: StateFlow<FarmViewState>
        get() = _state

    private val harvestList : MutableStateFlow<List<FarmModel.ProduceHarvest>> = MutableStateFlow(listOf())
    private val submitButtonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)
    private val micFabUiState: MutableStateFlow<UiComponentModel.FabUiState> = MutableStateFlow(_state.value.micFabUiState)
    private val micFabUiEvent: MutableStateFlow<UiComponentModel.FabUiEvent> = MutableStateFlow(_state.value.micFabUiEvent)
    private val speechRes: MutableStateFlow<String> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(harvestList, submitButtonUiState, micFabUiState, micFabUiEvent, speechRes) {
                    harvestList: List<FarmModel.ProduceHarvest>,
                    submitButtonUiState: UiComponentModel.ButtonUiState,
                    micFabUiState: UiComponentModel.FabUiState,
                    micFabUiEvent: UiComponentModel.FabUiEvent,
                    speechRes: String->
                FarmViewState(
                    micFabUiState = micFabUiState,
                    micFabUiEvent = micFabUiEvent,
                    submitButtonUiState = submitButtonUiState,
                    produceHarvestList = harvestList,
                    speechResult = speechRes
                )
            }.collect {
                _state.value = it

            }
        }

    }

    init{
        viewModelScope.launch {
//            speechRecognizerUtility.speechRecognizerResult.collect {speechText ->
//                harvestList.value = parseSpeech(speechText)
//            }
            kontinuousSpeechRecognizer.speechRecognizerResult.collect {speechText ->
                harvestList.value = parseSpeech(speechText)
            }
        }
    }

    init{
        viewModelScope.launch {
            inventoryRepository.getInventory().collect{ produce ->
                harvestList.value = produce.map {
                    FarmModel.ProduceHarvest(
                        produceName = it.produceName,
                        produceCount = 0,
                    )
                }
            }
        }
    }

    fun startListening(){
//        if(speechRecognizerUtility.isPermissionGranted()){
//            micFabUiState.value = getStopButton()
//            micFabUiEvent.value = getStopButtonEvent()
//        }
//        speechRecognizerUtility.startSpeechRecognition()
        if(kontinuousSpeechRecognizer.isPermissionGranted()){
            micFabUiState.value = getStopButton()
            micFabUiEvent.value = getStopButtonEvent()
        }
        kontinuousSpeechRecognizer.startRecognition()
    }

    fun stopListening(){
        micFabUiState.value = getMicButton()
        micFabUiEvent.value = getMicButtonEvent()
        //speechRecognizerUtility.stopSpeechRecognition()
        kontinuousSpeechRecognizer.stopRecognition()
    }

    private fun parseSpeech(speechResult: String): List<FarmModel.ProduceHarvest>{

        speechRes.value = speechResult
        harvestList.value.map { produce ->
            var re = Regex("(add)( )*([0-9]+)( )*${produce.produceName}[s]?")
            var matches = re.findAll(speechResult)
            var count = 0
            matches.forEach { m ->
                val action = m.groupValues[1]
                val quantity = m.groupValues[3].toIntOrNull()
                if(quantity != null && action != "" && action == "add"){
                    count += quantity ?: 0
                }
            }
            FarmModel.ProduceHarvest(produceName = produce.produceName, produceCount = produce.produceCount + count )
        }
        return listOf();
    }

    private fun getMicButtonEvent(): UiComponentModel.FabUiEvent {
        return UiComponentModel.FabUiEvent(onClick = {startListening()})
    }

    private fun getStopButtonEvent(): UiComponentModel.FabUiEvent {
        return UiComponentModel.FabUiEvent(onClick = {stopListening()})
    }
}