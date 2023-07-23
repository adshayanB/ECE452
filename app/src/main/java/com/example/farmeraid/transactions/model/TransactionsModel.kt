package com.example.farmeraid.transactions.model

import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.TransactionModel
import java.util.UUID

class TransactionsModel {

    data class TransactionViewState(
        val transactionList: List<TransactionModel.Transaction> = emptyList(),
        val filterList: List<Filter> = emptyList()
    )

    data class Filter(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        val itemsList: List<String>,
        val selectedItem: String?
    )
    
}

fun getFilters(
    marketItems: List<MarketModel.Market>,
    produceItems: MutableMap<String, Int>
): List<TransactionsModel.Filter> {
    val transactionsFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Type",
        itemsList = listOf("Harvest", "Sell", "Donate", "All"),
        selectedItem = "All"
    )

    val marketFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Market",
        itemsList = marketItems.map { it.name },
        selectedItem = null
    )

    val produceFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Produce",
        itemsList = produceItems.map { it.key },
        selectedItem = null
    )

    val charityFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = "Charity",
        itemsList = listOf("St. James", "Southvale", "Manchester"),
        selectedItem = null
    )

    return listOf(transactionsFilter, produceFilter, marketFilter, charityFilter)
}

fun List<TransactionsModel.Filter>.exposeFilters(): List<TransactionsModel.Filter> {
    // If SELL or ALL are selected in TransactionType filter -> Market filter pops up
    // If DONATE or ALL are selected in TransactionType filter -> Charity filter pops up

    val type: TransactionsModel.Filter? = this.firstOrNull{ it.name == "Type" }
    return type?.let {
        when( type.selectedItem){
            "Harvest"-> this.filter { it.name != "Market" && it.name != "Charity" }
            "Sell"-> this.filter { it.name != "Charity" }
            "Donate"-> this.filter { it.name != "Market" }
            else-> this
        }
    }?:this
}