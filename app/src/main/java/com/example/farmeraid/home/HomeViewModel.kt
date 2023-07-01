package com.example.farmeraid.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.UserRepository
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
    private val userRepository: UserRepository,
    ) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState>
        get() = _state

    private val quotasList: Flow<List<QuotasRepository.Quota>> = quotasRepository.getCategorizedQuotas()
    private val inventoryList : MutableStateFlow<MutableMap<String, Int>> = MutableStateFlow(mutableMapOf())
    private val selectedTab: MutableStateFlow<Tab> = MutableStateFlow(_state.value.selectedTab)

    init {
        viewModelScope.launch {
            combine(quotasList, inventoryList, selectedTab) {
                quotasList: List<QuotasRepository.Quota>, inventoryList: MutableMap<String, Int>, selectedTab: Tab ->
                HomeViewState(
                    quotasList = quotasList,
                    inventoryList = inventoryList,
                    selectedTab = selectedTab,
                )
            }.collect {
                _state.value = it

            }
        }
    }

    init{
        viewModelScope.launch {
            userRepository.getUserId()?.let {
                inventoryRepository.getInventory(it).collect{ produce ->
                    inventoryList.value = produce
                }
            }
        }
    }

    fun changeSelectedTab(tab: Tab) {
        selectedTab.value = tab;
    }

    fun navigateToAddQuota() {
        appNavigator.navigateToAddQuota()
    }
    fun navigateToAddProduce() {
        snackbarDelegate.showSnackbar(
            message = "Navigates to Add Produce"
        )
    }
}