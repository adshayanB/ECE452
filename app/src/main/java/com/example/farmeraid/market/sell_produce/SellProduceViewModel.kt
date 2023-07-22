package com.example.farmeraid.market.sell_produce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.market.sell_produce.model.SellProduceModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellProduceViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    private val quotasRepository: QuotasRepository,
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
    private val marketIdFlow : MutableStateFlow<String> = MutableStateFlow("")
    private val produceSellList : MutableStateFlow<List<SellProduceModel.ProduceSell>> = MutableStateFlow(listOf())
    private val submitButtonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)

    init {
        viewModelScope.launch {
            combine(marketName, marketIdFlow, produceSellList, submitButtonUiState, isLoading) {
                    marketName: String,
                    marketIdFlow: String,
                    produceSellList: List<SellProduceModel.ProduceSell>,
                    submitButtonUiState: UiComponentModel.ButtonUiState,
                    isLoading: Boolean ->
                SellProduceModel.SellProduceViewState(
                    marketName = marketName,
                    marketId = marketIdFlow,
                    produceSellList = produceSellList,
                    submitButtonUiState = submitButtonUiState,
                    isLoading = isLoading,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun fetchData() {
        viewModelScope.launch {
            isLoading.value = true
            val market: MarketModel.Market = marketRepository.getMarket(marketId!!).data!!
            val inventory: MutableMap<String, Int> = inventoryRepository.getInventory().single().data!!
            val quotaRes: ResponseModel.FAResponseWithData<QuotaModel.Quota?> = quotasRepository.getQuota(marketId!!)

            val quota: QuotaModel.Quota? = quotaRes.data

            marketName.value = market.name
            marketIdFlow.value = market.id
            produceSellList.value = market.prices.map {(produceName, price) ->
                val produceQuota: QuotaModel.ProduceQuota? = quota?.produceQuotaList?.find { it.produceName == produceName}

                SellProduceModel.ProduceSell(
                    produceName = produceName,
                    produceCount = 0,
                    produceInventory = inventory.getOrDefault(produceName, 0),
                    producePrice = price,
                    produceTotalPrice = 0.0,
                    produceQuotaCurrentProgress = produceQuota?.saleAmount ?: 0,
                    produceQuotaTotalGoal = produceQuota?.produceGoalAmount ?: -1
                )
            }
            isLoading.value = false
        }
    }

    init{ fetchData() }

    fun incrementProduceCount(produceName : String) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = produceSell.produceCount + if (produceName == produceSell.produceName) 1 else 0,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = produceSell.produceTotalPrice + if (produceName == produceSell.produceName) produceSell.producePrice else 0.0,
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
                produceTotalPrice = produceSell.produceTotalPrice - if (produceName == produceSell.produceName) produceSell.producePrice else 0.0,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    fun setProduceCount(produceName: String, produceCount: Int) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = if (produceName == produceSell.produceName) produceCount else produceSell.produceCount,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = if (produceName == produceSell.produceName) produceCount * produceSell.producePrice else produceSell.produceTotalPrice,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    fun submitSell() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)

            val produceSellMap: MutableMap<String, Int> = produceSellList.value.associate {
                Pair(it.produceName, it.produceCount)
            }.toMutableMap()

            inventoryRepository.sell(produceSellMap)

            val market: MarketModel.Market = marketRepository.getMarket(marketId!!).data!!

            val quotaRes: ResponseModel.FAResponseWithData<QuotaModel.Quota?> = quotasRepository.getQuota(marketId!!)

            val quota: QuotaModel.Quota? = if (quotaRes.error != null) quotaRes.data
            else null

            if (quota != null) {
                quotasRepository.addQuota(market, quota.produceQuotaList.map {produceQuota ->
                    QuotaModel.ProduceQuota(
                        produceName = produceQuota.produceName,
                        produceGoalAmount = produceQuota.produceGoalAmount,
                        saleAmount = produceQuota.saleAmount + produceSellList.value.find{it.produceName == produceQuota.produceName}!!.produceCount,
                    )
                })
            }

            fetchData()

            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
        }
    }

    fun getTotalEarnings(): Double {
        return produceSellList.value.sumOf { produceSell -> produceSell.produceTotalPrice }
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions()
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

    fun navigateToEditMarket(marketId: String) {
        appNavigator.navigateToEditMarket(marketId)
    }
}