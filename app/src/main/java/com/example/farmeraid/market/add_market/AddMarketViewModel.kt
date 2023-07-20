package com.example.farmeraid.market.add_market

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.home.add_edit_quota.model.getSubmitButton
import com.example.farmeraid.market.add_market.model.AddMarketModel
import com.example.farmeraid.market.add_market.model.initializeProduceRow
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddMarketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    private val quotasRepository: QuotasRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {

    private val _state = MutableStateFlow(AddMarketModel.AddMarketViewState(submitButtonUiState = getSubmitButton()))
    val state: StateFlow<AddMarketModel.AddMarketViewState>
        get() = _state

    private val marketName : MutableStateFlow<String> = MutableStateFlow("")
    private val marketsList : MutableStateFlow<List<MarketModel.Market>> = MutableStateFlow(emptyList())
    private val produceList : MutableStateFlow<MutableMap<String, Int>> = MutableStateFlow(mutableMapOf())
    private val produceRows : MutableStateFlow<List<AddMarketModel.ProduceRow>> = MutableStateFlow(_state.value.produceRows)
    private val submitButtonUiState : MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)

    init {
        viewModelScope.launch {
            combine(marketName, marketsList, produceList, produceRows, submitButtonUiState) {
                    marketName : String,
                    marketsList : List<MarketModel.Market>,
                    produceMap : Map<String, Int>,
                    produceRows : List<AddMarketModel.ProduceRow>,
                    submitButtonUiState : UiComponentModel.ButtonUiState ->
                AddMarketModel.AddMarketViewState(
                    marketName = marketName,
                    markets = marketsList,
                    produce = produceMap,
                    produceRows = produceRows,
                    submitButtonUiState = submitButtonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    init { fetchData() }

    fun fetchData() {
        viewModelScope.launch {
            combine(marketRepository.getMarkets(), inventoryRepository.getInventory()) {
                    markets, inventory -> Pair(markets, inventory)
            }.collect { (markets, inventory) ->
//                marketsList.value = markets
                markets.data?.let{
                    marketsList.value = it
                }?: run{
                    snackbarDelegate.showSnackbar(markets.error ?: "Unknown Error")
                }
                inventory.data?.let {
                    produceList.value = it
                } ?: run {
                    snackbarDelegate.showSnackbar(inventory.error ?: "Unknown error")
                }
//                marketId
//                    ?.let { markets.data?.firstOrNull { it.id == marketId } }
//                    ?.let { internalSelectMarket(it) }
            }
        }
    }

//    private suspend fun internalSelectMarket(market: MarketModel.Market) {
//        selectedMarket.value = market
//        produceRows.value =
//            quotasRepository.getQuota(market.id).let { quotaResponse ->
//                quotaResponse.data?.let {
//                    it.produceQuotaList.map { produceQuota ->
//                        AddEditQuotaModel.ProduceRow(
//                            produce = produceQuota.produceName,
//                            quantityPickerUiState = UiComponentModel.QuantityPickerUiState(produceQuota.produceGoalAmount)
//                        )
//                    }
//                } ?: run {
//                    if (quotaResponse.error != "Quota does not exist") {
//                        snackbarDelegate.showSnackbar(quotaResponse.error ?: "Unknown error")
//                    }
//                    emptyList()
//                }
//            } + initializeProduceRow()
//    }

//    fun selectMarket(market: MarketModel.Market) {
//        viewModelScope.launch { internalSelectMarket(market) }
//    }

    fun addProduceRow() {
        produceRows.value = produceRows.value + initializeProduceRow()
    }

    fun selectProduce(id : UUID, newProduce : String) {
        produceRows.value = produceRows.value.map { row ->
            when (row.id) {
                id -> AddMarketModel.ProduceRow(
                    id = row.id,
                    produce = newProduce,
                    quantityPickerUiState = UiComponentModel.QuantityPickerUiState(row.quantityPickerUiState.count),
                )
                else -> row
            }
        }
    }

    fun selectProducePrice(id : UUID, newAmount : Int?) {
        produceRows.value = produceRows.value.map { row ->
            when (row.id) {
                id -> AddMarketModel.ProduceRow(
                    id = row.id,
                    produce = row.produce,
                    quantityPickerUiState = UiComponentModel.QuantityPickerUiState(newAmount ?: 0, null,row.produce != null)
                )
                else -> row
            }
        }
    }

    fun removeProduceRow(id : UUID) {
        produceRows.value = produceRows.value.filter { it.id != id }
    }

    fun setMarketName(newMarketName: String) {
        marketName.value = newMarketName
    }

    fun submitMarket() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)
            val produceList: List<AddMarketModel.ProduceRow> =
                produceRows.value.filter { it.produce != null }
            if (marketName.value.equals("")) {
                snackbarDelegate.showSnackbar("Enter a market name")
            } else if (produceList.isEmpty()) {
                snackbarDelegate.showSnackbar("Select at least 1 produce")
            } else if (produceList.distinctBy { it.produce }.size != produceList.size) {
                snackbarDelegate.showSnackbar("Cannot have duplicate produce")
            } else if (produceList.any { it.quantityPickerUiState.count == null }) {
                snackbarDelegate.showSnackbar("There are one or more invalid values")
            } else {
                val addResult = marketRepository.addMarket(marketName.value, produceList.filter{ it.produce != null }.associate { produce -> produce.produce!! to produce.quantityPickerUiState.count.toDouble() })

                when (addResult) {
                    is ResponseModel.FAResponse.Success -> {}
                    is ResponseModel.FAResponse.Error -> {
                        snackbarDelegate.showSnackbar(addResult.error ?: "Unknown error")
                    }
                }
            }

            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}