package com.example.farmeraid.transactions

import com.example.farmeraid.snackbar.SnackbarDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.TransactionModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.transactions.model.TransactionsModel
import com.example.farmeraid.transactions.model.exposeFilters
import com.example.farmeraid.transactions.model.getFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val transactionType : String = savedStateHandle["transactionType"] ?: "All"

    private val _state = MutableStateFlow(
        TransactionsModel.TransactionViewState()
    )
    val state: StateFlow<TransactionsModel.TransactionViewState>
        get() = _state

    private val transactionList: MutableStateFlow<List<TransactionModel.Transaction>> = MutableStateFlow(
        mutableListOf()
    )

    private val produceItemsList: MutableStateFlow<MutableMap<String,Int>> = MutableStateFlow(
        mutableMapOf()
    )

    private val marketItemsList: MutableStateFlow<List<MarketModel.Market>> = MutableStateFlow(
        mutableListOf()
    )

    private val filterList: MutableStateFlow<List<TransactionsModel.Filter>> = MutableStateFlow(
        mutableListOf()
    )

    init{
        viewModelScope.launch {
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
                filterList.value = getFilters(marketItemsList.value, produceItemsList.value, transactionType)
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(transactionList, filterList) {
                    transactions: List<TransactionModel.Transaction>,
                    filterList: List<TransactionsModel.Filter>->
                TransactionsModel.TransactionViewState(
                    transactionList = transactions,
                    filterList = filterList.exposeFilters(),
                )
            }.collect {
                _state.value = it
            }
        }
    }

    init {
        viewModelScope.launch {
            filterList.collect { filterList ->
                transactionRepository.getRecentTransactions(filterList.exposeFilters(), 25).let { transactions ->
                    transactions.data?.let {
                        transactionList.value = it
                    } ?: run {
                        snackbarDelegate.showSnackbar(transactions.error ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun showDeleteConfirmation(id: String) {
        viewModelScope.launch{
            snackbarDelegate.showSnackbar(
                message = "Are you sure?",
                actionLabel = "Yes",
                onAction = {
                    viewModelScope.launch {
                        when(val delResult = transactionRepository.deleteTransaction(id)) {
                            is ResponseModel.FAResponse.Error -> snackbarDelegate.showSnackbar(delResult.error ?: "Unknown error")
                            is ResponseModel.FAResponse.Success -> {
                                transactionList.value = transactionList.value.filter { it.transactionId != id }
                            }
                        }
                    }
                }
            )
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

    fun updateSelectedFilterItem(id: UUID, selectedItem: String) {

        filterList.value = filterList.value.map{
            if (it.id == id){
                TransactionsModel.Filter(
                    id = it.id,
                    name = it.name,
                    itemsList = it.itemsList,
                    selectedItem = selectedItem
                )
            } else it
        }

    }
}