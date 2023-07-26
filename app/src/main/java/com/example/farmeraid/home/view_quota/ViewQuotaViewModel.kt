package com.example.farmeraid.home.view_quota

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
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
    private val quotasRepository: QuotasRepository,
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val marketId : String? = savedStateHandle["marketId"]

    private val _state = MutableStateFlow(ViewQuotaModel.ViewQuotaViewState())
    val state: StateFlow<ViewQuotaModel.ViewQuotaViewState>
        get() = _state

    private val quota : Flow<MarketModel.MarketWithQuota?> = flow {
        marketId?.let{ id ->
            marketRepository.getMarketWithQuota(id).let { marketWithQuota ->
                marketWithQuota.data?.let {
                    emit(it)
                } ?: run {
                    snackbarDelegate.showSnackbar(marketWithQuota.error ?: "Unknown error")
                    emit(null)
                }
            }
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

    fun confirmDeleteQuota() {
        snackbarDelegate.showSnackbar(
            message = "Are you sure you want to delete ${_state.value.quota?.name}'s quota?",
            actionLabel = "Yes",
            onAction = { deleteProduce() }
        )
    }

    private fun deleteProduce() {
        viewModelScope.launch {
            _state.value.quota?.id?.let {
                when (val deleteResult = quotasRepository.deleteQuota(it)) {
                    is ResponseModel.FAResponse.Success -> {
                        appNavigator.navigateBack()
                    }
                    is ResponseModel.FAResponse.Error -> {
                        snackbarDelegate.showSnackbar(deleteResult.error ?: "Unknown error")
                    }
                }
            } ?: run {
                snackbarDelegate.showSnackbar("No quota id provided")
            }
        }
    }

    fun userIsAdmin() : Boolean {
        return userRepository.isAdmin()
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

    fun navigateToEditQuota() {
        marketId?.let {
            appNavigator.navigateToEditQuota(it)
        }
    }
}