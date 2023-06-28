package com.example.farmeraid.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.home.model.HomeModel.Produce
import com.example.farmeraid.home.model.HomeModel.CategorizedQuotas
import com.example.farmeraid.home.model.HomeModel.HomeViewState
import com.example.farmeraid.home.model.getHomeButton
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
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
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState(
        buttonUiState = getHomeButton()
    ))
    val state: StateFlow<HomeViewState>
        get() = _state

    private val quotasList: Flow<List<CategorizedQuotas>> = quotasRepository.getCategorizedQuotas()
    private val inventory: Flow<MutableMap<String, Int>> = inventoryRepository.getInventory()
    private val selectedTab: MutableStateFlow<Tab> = MutableStateFlow(_state.value.selectedTab)

    init {
        viewModelScope.launch {
            combine(quotasList, inventory, selectedTab) {
                quotasList: List<CategorizedQuotas>, inventory: MutableMap<String, Int>, selectedTab: Tab ->
                HomeViewState(
                    quotasList = quotasList,
                    inventory = inventory,
                    selectedTab = selectedTab,
                    buttonUiState = getHomeButton(),
                )
            }.collect {
                _state.value = it

            }
        }
    }

    fun changeSelectedTabAndNavigate(tab: Tab) {
        selectedTab.value = tab;
        appNavigator.navigateToMode(NavRoute.Farm)
    }
}