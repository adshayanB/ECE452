package com.example.farmeraid.transactions.model

import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.uicomponents.models.UiComponentModel

class TransactionsModel {

//    data class Transaction(
//        val mode: String = "",
//        val description: String = "",
//        val ID: Int = 0
//    )

    data class TransactionViewState(
        val transactionList: List<TransactionRepository.Transaction> = emptyList(),
    )
}