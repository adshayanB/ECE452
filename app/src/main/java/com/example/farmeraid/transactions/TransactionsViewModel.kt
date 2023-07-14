package com.example.farmeraid.transactions

import com.example.farmeraid.snackbar.SnackbarDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.transactions.model.TransactionsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {

    private val _state = MutableStateFlow(
        TransactionsModel.TransactionViewState())
    val state: StateFlow<TransactionsModel.TransactionViewState>
        get() = _state

    private val transactionList: MutableStateFlow<List<TransactionRepository.Transaction>> = MutableStateFlow(
        mutableListOf()
    )

    init{
        viewModelScope.launch {
            transactionRepository.getRecentTransactions(TransactionRepository.TransactionType.ALL, 5).collect{ produce ->
                transactionList.value = produce
            }
        }
    }
    init {
        viewModelScope.launch {
            transactionList.collect{
                _state.value =  TransactionsModel.TransactionViewState(
                    transactionList = it
                )
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