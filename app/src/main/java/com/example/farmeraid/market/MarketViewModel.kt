package com.example.farmeraid.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.TransactionModel
import com.example.farmeraid.market.model.MarketPageModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(MarketPageModel.MarketViewState())
    val state: StateFlow<MarketPageModel.MarketViewState>
        get() = _state

    private val marketList: MutableStateFlow<List<MarketModel.Market>> = MutableStateFlow(listOf())
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)

    init {
        viewModelScope.launch {
            combine(marketList, isLoading) {
                    marketList: List<MarketModel.Market>,
                    isLoading: Boolean ->
                MarketPageModel.MarketViewState(
                    marketList = marketList,
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

            marketRepository.getMarkets().collect { markets ->
                markets.data?.let {
                    marketList.value = it
                } ?: run {
                    snackbarDelegate.showSnackbar(markets.error ?: "Unknown error")
                }
            }

            isLoading.value = false
        }
    }

    init {
        fetchData()
    }

    fun userIsAdmin() : Boolean {
        return userRepository.isAdmin()
    }

    fun navigateToSellProduce(marketId: String) {
        appNavigator.navigateToSellProduce(marketId)
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions(TransactionModel.TransactionType.SELL.stringValue)
    }

    fun navigateToAddMarket() {
        appNavigator.navigateToAddMarket()
    }
}