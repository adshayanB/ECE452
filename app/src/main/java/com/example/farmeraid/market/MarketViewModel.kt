package com.example.farmeraid.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.farm.model.FarmModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.home.model.HomeModel.HomeViewState
import com.example.farmeraid.market.model.MarketPageModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(MarketPageModel.MarketViewState())
    val state: StateFlow<MarketPageModel.MarketViewState>
        get() = _state

    private val marketWithQuotaList: MutableStateFlow<List<MarketModel.MarketWithQuota>> = MutableStateFlow(listOf())
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)

    init {
        viewModelScope.launch {
            combine(marketWithQuotaList, isLoading) {
                    marketWithQuotaList: List<MarketModel.MarketWithQuota>,
                    isLoading: Boolean ->
                MarketPageModel.MarketViewState(
                    marketWithQuotaList = marketWithQuotaList,
                    isLoading = isLoading,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    init {
        viewModelScope.launch {
            isLoading.value = true

            marketRepository.getMarketsWithQuota().collect { marketsWithQuota ->
                marketsWithQuota.data?.let {
                    marketWithQuotaList.value = it
                } ?: run {
                        snackbarDelegate.showSnackbar(marketsWithQuota.error ?: "Unknown error")
                }
            }

            isLoading.value = false
        }
    }

    fun navigateToSellProduce(marketId: String) {
        appNavigator.navigateToSellProduce(marketId)
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions()
    }

    fun navigateToAddMarket() {
        appNavigator.navigateToAddMarket()
    }
}