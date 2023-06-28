package com.example.farmeraid.transactions

import com.example.farmeraid.snackbar.SnackbarDelegate
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_in.model.getSignInButton

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.transactions.model.TransactionsModel
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val transactionList: MutableStateFlow<List<TransactionRepository.Transaction>> = MutableStateFlow(transactionRepository.getRecentTransactions(TransactionRepository.TransactionType.ALL, 5))

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

    fun showDeleteConfirmation(id : Int) {
        viewModelScope.launch{
            isPopupVisible = true
            snackbarDelegate.showSnackbar(
                message = "Are you sure?",
                actionLabel = "Yes",
                onAction = {
                    transactionRepository.deleteTransaction(id)
                    transactionList.value = transactionRepository.getRecentTransactions(TransactionRepository.TransactionType.ALL, 5)
                }
            )
        }

    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

}