package com.example.farmeraid.home.view_quota

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.home.view_quota.model.ViewQuotaModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewQuotaViewModel @Inject constructor(
    marketRepository: MarketRepository,
    savedStateHandle: SavedStateHandle,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val marketId : String? = savedStateHandle["marketId"]

    private val _state = MutableStateFlow(ViewQuotaModel.ViewQuotaViewState())
    val state: StateFlow<ViewQuotaModel.ViewQuotaViewState>
        get() = _state

    private val quota : Flow<MarketModel.MarketWithQuota?> = flow {
        marketId?.let{
            emit(marketRepository.getMarketWithQuota(marketId).data)
        } ?: run {
            emit(null)
        }
    }.flowOn(Dispatchers.IO)

    init {
        viewModelScope.launch {
            quota.collect {
                _state.value = ViewQuotaModel.ViewQuotaViewState(
                    quota = it
                )
            }
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

    fun navigateToEditQuota(id : String) {
        appNavigator.navigateToEditQuota(id)
    }
}