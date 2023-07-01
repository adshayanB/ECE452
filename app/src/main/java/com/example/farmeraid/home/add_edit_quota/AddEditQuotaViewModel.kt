package com.example.farmeraid.home.add_edit_quota

import android.util.Log
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
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditQuotaViewModel @Inject constructor(
    // TODO: Link firebase with these repositories
    inventoryRepository: InventoryRepository,
    marketRepository: MarketRepository,
    private val quotasRepository: QuotasRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {
    private val _state = MutableStateFlow(AddEditQuotaModel.AddEditQuotaViewState(submitButtonUiState =  getSubmitButton()))
    val state: StateFlow<AddEditQuotaModel.AddEditQuotaViewState>
        get() = _state

    private val marketsList : Flow<List<MarketModel.Market>> = marketRepository.getMarkets()
    private val produceList : MutableStateFlow<MutableMap<String, Int>> = MutableStateFlow(mutableMapOf())
    private val produceRows : MutableStateFlow<List<AddEditQuotaModel.ProduceRow>> = MutableStateFlow(_state.value.produceRows)
    private val selectedMarket : MutableStateFlow<MarketModel.Market?> = MutableStateFlow(null)
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
    init{
        viewModelScope.launch {
                inventoryRepository.getInventory().collect{ produce ->
                    produceList.value = produce
                }
        }
    }

    fun addProduceRow() {
        produceRows.value = produceRows.value + initializeProduceRow()
    }

    fun selectMarket(market: MarketModel.Market) {
        viewModelScope.launch {
            selectedMarket.value = market
            produceRows.value =
                (quotasRepository.getQuota(market)?.produceQuotaList?.map { produceQuota ->
                    AddEditQuotaModel.ProduceRow(
                        produce = produceQuota.produceName,
                        quantityPickerUiState = UiComponentModel.QuantityPickerUiState(produceQuota.produceGoalAmount)
                    )
                } ?: emptyList()) + initializeProduceRow()
        }
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
            val produceList : List<AddEditQuotaModel.ProduceRow> = produceRows.value
            if (market == null) {
                snackbarDelegate.showSnackbar("Select a market")
            } else if (produceList.all { it.produce == null }) {
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