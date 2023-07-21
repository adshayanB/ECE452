package com.example.farmeraid.market.add_edit_market

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.home.add_edit_quota.model.getSubmitButton
import com.example.farmeraid.market.add_edit_market.model.AddEditMarketModel
import com.example.farmeraid.market.add_edit_market.model.initializeProduceRow
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditMarketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val marketRepository: MarketRepository,
    private val inventoryRepository: InventoryRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {

    private val marketId : String? = savedStateHandle["marketId"]

    private val _state = MutableStateFlow(AddEditMarketModel.AddEditMarketViewState(submitButtonUiState = getSubmitButton()))
    val state: StateFlow<AddEditMarketModel.AddEditMarketViewState>
        get() = _state

    private val marketName : MutableStateFlow<String> = MutableStateFlow("")
    private val selectedMarket : MutableStateFlow<MarketModel.Market?> = MutableStateFlow(_state.value.selectedMarket)
    private val marketsList : MutableStateFlow<List<MarketModel.Market>> = MutableStateFlow(emptyList())
    private val produceList : MutableStateFlow<MutableMap<String, Int>> = MutableStateFlow(mutableMapOf())
    private val produceRows : MutableStateFlow<List<AddEditMarketModel.ProduceRow>> = MutableStateFlow(_state.value.produceRows)
    private val submitButtonUiState : MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)

    inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
    ): Flow<R> {
        return kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5, flow6, flow7) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
            )
        }
    }

    init {
        viewModelScope.launch {

            combine(marketName, selectedMarket, marketsList, produceList, produceRows, submitButtonUiState, isLoading) {
                    marketName: String,
                    selectedMarket: MarketModel.Market?,
                    marketsList : List<MarketModel.Market>,
                    produceMap : Map<String, Int>,
                    produceRows : List<AddEditMarketModel.ProduceRow>,
                    submitButtonUiState : UiComponentModel.ButtonUiState,
                    isLoading: Boolean ->
                AddEditMarketModel.AddEditMarketViewState(
                    marketName = marketName,
                    selectedMarket = selectedMarket,
                    markets = marketsList,
                    produce = produceMap,
                    produceRows = produceRows,
                    submitButtonUiState = submitButtonUiState,
                    isLoading = isLoading,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    init { fetchData() }

    fun fetchData() {
        viewModelScope.launch {
            isLoading.value = true
            combine(marketRepository.getMarkets(), inventoryRepository.getInventory()) {
                    markets, inventory -> Pair(markets, inventory)
            }.collect { (markets, inventory) ->
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
                marketId
                    ?.let { markets.data?.firstOrNull { it.id == marketId } }
                    ?.let { internalSelectMarket(it) }
            }
            isLoading.value = false
        }
    }

    private suspend fun internalSelectMarket(market: MarketModel.Market) {
        marketName.value = market.name
        selectedMarket.value = market
        produceRows.value =
            marketRepository.getMarket(market.id).let { marketResponse ->
                marketResponse.data?.let {
                    it.prices.map { producePrice ->
                        AddEditMarketModel.ProduceRow(
                            produce = producePrice.key,
                            producePrice = producePrice.value,
                        )
                    }
                } ?: run {
                    if (marketResponse.error != "Market does not exist") {
                        snackbarDelegate.showSnackbar(marketResponse.error ?: "Unknown error")
                    }
                    emptyList()
                }
            } + initializeProduceRow()
    }

    fun addProduceRow() {
        produceRows.value = produceRows.value + initializeProduceRow()
    }

    fun selectProduce(id : UUID, newProduce : String) {
        produceRows.value = produceRows.value.map { row ->
            when (row.id) {
                id -> AddEditMarketModel.ProduceRow(
                    id = row.id,
                    produce = newProduce,
                    producePrice = row.producePrice,
                )
                else -> row
            }
        }
    }

    fun setProducePrice(id : UUID, newAmount : Double?) {
        produceRows.value = produceRows.value.map { row ->
            when (row.id) {
                id -> AddEditMarketModel.ProduceRow(
                    id = row.id,
                    produce = row.produce,
                    producePrice = newAmount ?: 0.0,
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
            val produceList: List<AddEditMarketModel.ProduceRow> =
                produceRows.value.filter { it.produce != null }
            if (marketName.value.equals("")) {
                snackbarDelegate.showSnackbar("Enter a market name")
            } else if (produceList.isEmpty()) {
                snackbarDelegate.showSnackbar("Select at least 1 produce")
            } else if (produceList.distinctBy { it.produce }.size != produceList.size) {
                snackbarDelegate.showSnackbar("Cannot have duplicate produce")
            } else if (produceList.any { it.producePrice == 0.0 }) {
                snackbarDelegate.showSnackbar("There are one or more invalid values")
            } else {
                val result = marketRepository.addOrUpdateMarket(marketName.value, produceList.filter{ it.produce != null }.associate { produce -> produce.produce!! to produce.producePrice })

                when (result) {
                    is ResponseModel.FAResponse.Success -> {}
                    is ResponseModel.FAResponse.Error -> {
                        snackbarDelegate.showSnackbar(result.error ?: "Unknown error")
                    }
                }
            }

            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
            navigateBack()
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}