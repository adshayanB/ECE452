package com.example.farmeraid.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.home.model.HomeModel.HomeViewState
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val inventoryRepository: InventoryRepository,
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState>
        get() = _state

    private val quotasList: MutableStateFlow<List<MarketModel.MarketWithQuota>> = MutableStateFlow(emptyList())
    private val inventoryList : MutableStateFlow<MutableMap<String, Int>> = MutableStateFlow(mutableMapOf())
    private val selectedTab: MutableStateFlow<Tab> = MutableStateFlow(_state.value.selectedTab)
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)

    init {
        viewModelScope.launch {
            combine(quotasList, inventoryList, selectedTab, isLoading) {
                    quotasList: List<MarketModel.MarketWithQuota>, inventoryList: MutableMap<String, Int>, selectedTab: Tab, isLoading : Boolean ->
                HomeViewState(
                    quotasList = quotasList,
                    inventoryList = inventoryList,
                    selectedTab = selectedTab,
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
            combine(marketRepository.getMarketsWithQuota(), inventoryRepository.getInventory()) {quotas, inventory -> Pair(quotas, inventory)}
                .collect { (quotas, inventory) ->
//                    quotasList.value = quotas
                    quotas.data?.let{
                        quotasList.value = it
                    } ?: run {
                        snackbarDelegate.showSnackbar(quotas.error ?: "Unknown error")
                    }
                    inventory.data?.let {
                        inventoryList.value = it
                    } ?: run {
                        snackbarDelegate.showSnackbar(inventory.error ?: "Unknown error")
                    }
                    isLoading.value = false
                }
        }
    }

    fun changeSelectedTab(tab: Tab) {
        selectedTab.value = tab;
    }

    fun userIsAdmin() : Boolean {
        return userRepository.isAdmin()
    }

    fun navigateToAddQuota() {
        appNavigator.navigateToAddQuota()
    }
    fun navigateToAddProduce() {
        appNavigator.navigateToAddProduce()
    }

    fun navigateToViewQuota(id : String) {
        appNavigator.navigateToViewQuota(id)
    }

    fun navigateToEditProduce(produceName : String, produceAmount : Int) {
        if (userIsAdmin()) {
            appNavigator.navigateToEditProduce(produceName, produceAmount)
        }
    }

    fun navigateToFridgeDetails(fridgeId :String){
        appNavigator.navigateToFridgeDetails(fridgeId)
    }
}