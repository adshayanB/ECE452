package com.example.farmeraid.transactions

import com.example.farmeraid.snackbar.SnackbarDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.transactions.model.TransactionsModel
import com.example.farmeraid.transactions.model.getFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {

    private val _state = MutableStateFlow(
        TransactionsModel.TransactionViewState()
    )
    val state: StateFlow<TransactionsModel.TransactionViewState>
        get() = _state

    private val transactionList: MutableStateFlow<List<TransactionRepository.Transaction>> = MutableStateFlow(
        mutableListOf()
    )

    private val produceItemsList: MutableStateFlow<MutableMap<String,Int>> = MutableStateFlow(
        mutableMapOf()
    )

    private val marketItemsList: MutableStateFlow<List<MarketModel.Market>> = MutableStateFlow(
        mutableListOf()
    )

    init{
        viewModelScope.launch {
            transactionRepository.getRecentTransactions(TransactionRepository.TransactionType.ALL, 5).collect{ produce ->
                transactionList.value = produce
            }

            combine(marketRepository.getMarkets(), inventoryRepository.getInventory()) {
                    markets, inventory -> Pair(markets, inventory)
            }.collect { (markets, inventory) ->
//                marketsList.value = markets
                markets.data?.let{
                    marketItemsList.value = it
                }?: run{
                    snackbarDelegate.showSnackbar(markets.error ?: "Unknown Error")
                }
                inventory.data?.let {
                    produceItemsList.value = it
                } ?: run {
                    snackbarDelegate.showSnackbar(inventory.error ?: "Unknown error")
                }
            }
        }
    }


    init {
        viewModelScope.launch {
            combine(transactionList, marketItemsList, produceItemsList) {
                    transactions: List<TransactionRepository.Transaction>,
                    marketItems: List<MarketModel.Market>,
                    produceItems: MutableMap<String, Int> ->
                TransactionsModel.TransactionViewState(
                    transactionList = transactions,
                    filterList = getFilters(marketItems, produceItems),
                )
            }.collect {
                _state.value = it
            }
        }
    }

    var isPopupVisible by mutableStateOf(false)
        private set

    fun showDeleteConfirmation(id: String) {
        viewModelScope.launch{
            isPopupVisible = true
            snackbarDelegate.showSnackbar(
                message = "Are you sure?",
                actionLabel = "Yes",
                onAction = {
                    viewModelScope.launch {
                        transactionRepository.deleteTransaction(id)
                        transactionRepository.getRecentTransactions(TransactionRepository.TransactionType.ALL, 5).collect{ produce ->
                            transactionList.value = produce
                        }
                    }
                }
            )
        }

    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }



}