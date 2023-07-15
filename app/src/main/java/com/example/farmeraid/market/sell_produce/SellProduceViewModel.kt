package com.example.farmeraid.market.sell_produce

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.farm.model.FarmModel
import com.example.farmeraid.farm.model.FarmModel.FarmViewState
import com.example.farmeraid.farm.model.getMicButton
import com.example.farmeraid.farm.model.getStopButton
import com.example.farmeraid.farm.model.getSubmitButton
import com.example.farmeraid.market.model.MarketPageModel
import com.example.farmeraid.market.sell_produce.model.SellProduceModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.speech_recognition.KontinuousSpeechRecognizer
import com.example.farmeraid.speech_recognition.SpeechRecognizerUtility
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellProduceViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    savedStateHandle: SavedStateHandle,
    private val appNavigator: AppNavigator,
): ViewModel() {

    private val marketId : String? = savedStateHandle["marketId"]

    private val _state = MutableStateFlow(
        SellProduceModel.SellProduceViewState(
            submitButtonUiState = UiComponentModel.ButtonUiState(text = "Submit")
        )
    )

    val state: StateFlow<SellProduceModel.SellProduceViewState>
        get() = _state

    private val marketName : MutableStateFlow<String> = MutableStateFlow("")
    private val produceSellList : MutableStateFlow<List<SellProduceModel.ProduceSell>> = MutableStateFlow(listOf())
    private val submitButtonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)

    init {
        viewModelScope.launch {
            combine(marketName, produceSellList, submitButtonUiState) {
                    marketName: String,
                    produceSellList: List<SellProduceModel.ProduceSell>,
                    submitButtonUiState: UiComponentModel.ButtonUiState ->
                SellProduceModel.SellProduceViewState(
                    marketName = marketName,
                    produceSellList = produceSellList,
                    submitButtonUiState = submitButtonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    init{
        viewModelScope.launch {
            val marketWithQuota: MarketModel.MarketWithQuota? = if (marketId != null) marketRepository.getMarketWithQuota(marketId).data else null
            val inventory: MutableMap<String, Int>? = inventoryRepository.getInventory().single().data

            if (marketWithQuota != null && inventory != null) {
                marketName.value = marketWithQuota.name
                produceSellList.value = marketWithQuota.prices.map {(produceName, price) ->
                    val produceQuota: QuotasRepository.ProduceQuota? = marketWithQuota.quota.produceQuotaList.find { it.produceName == produceName}

                    SellProduceModel.ProduceSell(
                        produceName = produceName,
                        produceCount = 0,
                        produceInventory = inventory.getOrDefault(produceName, 0),
                        producePrice = price,
                        produceTotalPrice = 0,
                        produceQuotaCurrentProgress = produceQuota?.saleAmount ?: 0,
                        produceQuotaTotalGoal = produceQuota?.produceGoalAmount ?: -1
                    )
                }
            }
        }
    }

    fun incrementProduceCount(produceName : String) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = produceSell.produceCount + if (produceName == produceSell.produceName) 1 else 0,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = produceSell.produceTotalPrice + if (produceName == produceSell.produceName) produceSell.producePrice else 0,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    fun decrementProduceCount(produceName : String) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = produceSell.produceCount - if (produceName == produceSell.produceName) 1 else 0,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = produceSell.produceTotalPrice - if (produceName == produceSell.produceName) produceSell.producePrice else 0,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    // TODO: Add method to setProduceCount that will be used with the new functionality of inputting values into the quantity picker component

    fun submitSell() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)
            val produceSellMap: MutableMap<String, Int> = produceSellList.value.associate {
                Pair(it.produceName, it.produceCount)
            }.toMutableMap()

            inventoryRepository.sell(produceSellMap)

            produceSellList.value = produceSellList.value.map {produceSell ->
                SellProduceModel.ProduceSell(
                    produceName = produceSell.produceName,
                    produceCount = 0,
                    produceInventory = produceSell.produceInventory - produceSell.produceCount,
                    producePrice = produceSell.producePrice,
                    produceTotalPrice = 0,
                    produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress + produceSell.produceCount,
                    produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
                )
            }

            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
        }
    }

    fun getTotalEarnings(): Int {
        return produceSellList.value.sumOf { produceSell -> produceSell.produceTotalPrice }
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions()
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}