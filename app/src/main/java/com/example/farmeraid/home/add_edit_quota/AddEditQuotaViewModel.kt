package com.example.farmeraid.home.add_edit_quota

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.home.add_edit_quota.model.getSubmitButton
import com.example.farmeraid.home.add_edit_quota.model.AddEditQuotaModel
import com.example.farmeraid.home.add_edit_quota.model.initializeProduceRow
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
class AddEditQuotaViewModel @Inject constructor(
    // TODO: Link firebase with these repositories
    savedStateHandle: SavedStateHandle,
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    private val quotasRepository: QuotasRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {
    private val marketId : String? = savedStateHandle["marketId"]

    private val _state = MutableStateFlow(AddEditQuotaModel.AddEditQuotaViewState(submitButtonUiState = getSubmitButton()))
    val state: StateFlow<AddEditQuotaModel.AddEditQuotaViewState>
        get() = _state

    private val marketsList : MutableStateFlow<List<MarketModel.Market>> = MutableStateFlow(emptyList())
    private val produceList : MutableStateFlow<MutableMap<String, Int>> = MutableStateFlow(mutableMapOf())
    private val produceRows : MutableStateFlow<List<AddEditQuotaModel.ProduceRow>> = MutableStateFlow(_state.value.produceRows)
    private val selectedMarket : MutableStateFlow<MarketModel.Market?> = MutableStateFlow(_state.value.selectedMarket)
    private val submitButtonUiState : MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)

    init {
        viewModelScope.launch {
            combine(marketsList, produceList, produceRows, selectedMarket, submitButtonUiState) {
                    marketsList : List<MarketModel.Market>,
                    produceMap : Map<String, Int>,
                    produceRows : List<AddEditQuotaModel.ProduceRow>,
                    selectedMarket : MarketModel.Market?,
                    submitButtonUiState : UiComponentModel.ButtonUiState ->
                AddEditQuotaModel.AddEditQuotaViewState(
                    markets = marketsList,
                    produce = produceMap,
                    produceRows = produceRows,
                    selectedMarket = selectedMarket,
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
                marketsList.value = markets
                produceList.value = inventory
                marketId
                    ?.let { markets.firstOrNull { it.id == marketId }}
                    ?.let { internalSelectMarket(it) }
            }
        }
    }

    private suspend fun internalSelectMarket(market: MarketModel.Market) {
        selectedMarket.value = market
        produceRows.value =
            (quotasRepository.getQuota(market.id)?.produceQuotaList?.map { produceQuota ->
                AddEditQuotaModel.ProduceRow(
                    produce = produceQuota.produceName,
                    quantityPickerUiState = UiComponentModel.QuantityPickerUiState(produceQuota.produceGoalAmount)
                )
            } ?: emptyList()) + initializeProduceRow()
    }

    fun selectMarket(market: MarketModel.Market) {
        viewModelScope.launch { internalSelectMarket(market) }
    }

    fun addProduceRow() {
        produceRows.value = produceRows.value + initializeProduceRow()
    }

    fun selectProduce(id : UUID, newProduce : String) {
        produceRows.value = produceRows.value.map { row ->
            when (row.id) {
                id -> AddEditQuotaModel.ProduceRow(
                    id = row.id,
                    produce = newProduce,
                    quantityPickerUiState = UiComponentModel.QuantityPickerUiState(row.quantityPickerUiState.count),
                )
                else -> row
            }
        }
    }

    fun selectQuotaAmount(id : UUID, newAmount : Int) {
        produceRows.value = produceRows.value.map { row ->
            when (row.id) {
                id -> AddEditQuotaModel.ProduceRow(
                    id = row.id,
                    produce = row.produce,
                    quantityPickerUiState = UiComponentModel.QuantityPickerUiState(newAmount, row.produce != null)
                )
                else -> row
            }
        }
    }

    fun removeProduceRow(id : UUID) {
        produceRows.value = produceRows.value.filter { it.id != id }
    }

    fun submitQuota() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)
            val market : MarketModel.Market? = selectedMarket.value
            val produceList : List<AddEditQuotaModel.ProduceRow> = produceRows.value.filter { it.produce != null }
            if (market == null) {
                snackbarDelegate.showSnackbar("Select a market")
            } else if (produceList.isEmpty()) {
                snackbarDelegate.showSnackbar("Select at least 1 produce")
            } else if (produceList.distinctBy { it.produce }.size != produceList.size) {
                snackbarDelegate.showSnackbar("Cannot have duplicate produce")
            } else {
                quotasRepository.addQuota(market, produceList.mapNotNull { row ->
                    row.produce?.let {
                        QuotasRepository.ProduceQuota(
                            produceName = row.produce,
                            produceGoalAmount = row.quantityPickerUiState.count,
                        )
                    }
                })
            }

            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}