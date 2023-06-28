package com.example.farmeraid.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.home.model.HomeModel.HomeViewState
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    quotasRepository: QuotasRepository,
    inventoryRepository: InventoryRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState>
        get() = _state

    private val quotasList: Flow<List<QuotasRepository.Quota>> = quotasRepository.getCategorizedQuotas()
    private val inventory: Flow<MutableMap<String, Int>> = inventoryRepository.getInventory()
    private val selectedTab: MutableStateFlow<Tab> = MutableStateFlow(_state.value.selectedTab)

    init {
        viewModelScope.launch {
            combine(quotasList, inventory, selectedTab) {
                    quotasList: List<QuotasRepository.Quota>, inventory: MutableMap<String, Int>, selectedTab: Tab ->
                HomeViewState(
                    quotasList = quotasList,
                    inventory = inventory,
                    selectedTab = selectedTab,
                )
            }.collect {
                _state.value = it

            }
        }
    }

    fun changeSelectedTab(tab: Tab) {
        selectedTab.value = tab;
    }

    fun navigateToAddQuota() {
        snackbarDelegate.showSnackbar(
            message = "Navigates to Add Quota"
        )
    }
}