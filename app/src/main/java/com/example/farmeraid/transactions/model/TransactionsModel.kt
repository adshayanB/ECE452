package com.example.farmeraid.transactions.model

import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.model.MarketModel

class TransactionsModel {

//    data class Transaction(
//        val mode: String = "",
//        val description: String = "",
//        val ID: Int = 0
//    )

    data class TransactionViewState(
        val transactionList: List<TransactionRepository.Transaction> = emptyList(),
        val filterList: List<Filter> = emptyList()
    )

    data class Filter(
        val name: String,
        val itemsList: List<String>,
        val selected: Boolean = false
    )
    
    
}

fun getFilters(
    marketItems: List<MarketModel.Market>,
    produceItems: MutableMap<String, Int>
): List<TransactionsModel.Filter> {
    val transactionsFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Type",
        itemsList = listOf("Harvest", "Sell", "Donate"),
    )

    val marketFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Markets",
        itemsList = marketItems.map { it.name },
    )

    val produceFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Produce",
        itemsList = produceItems.map { it.key },
    )

    return listOf(transactionsFilter, marketFilter, produceFilter)
}