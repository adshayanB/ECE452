package com.example.farmeraid.transactions.model

import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.TransactionModel
import java.util.UUID

class TransactionsModel {
    enum class FilterName(val uiName : String, val dbFieldName : String) {
        Type("Type", "type"),
        Produce("Produce", "produce"),
        Market("Market", "destination"),
        Charity("Charity", "destination"),
    }
    data class TransactionViewState(
        val transactionList: List<TransactionModel.Transaction> = emptyList(),
        val filterList: List<Filter> = emptyList()
    )

    data class Filter(
        val id: UUID = UUID.randomUUID(),
        val name: FilterName,
        val itemsList: List<String>,
        val selectedItem: String?
    )
    
}

fun getFilters(
    marketItems: List<MarketModel.Market>,
    produceItems: MutableMap<String, Int>
): List<TransactionsModel.Filter> {
    val transactionsFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = TransactionsModel.FilterName.Type,
        itemsList = listOf(
            TransactionModel.TransactionType.HARVEST.stringValue,
            TransactionModel.TransactionType.SELL.stringValue,
            TransactionModel.TransactionType.DONATE.stringValue
        ),
        selectedItem = "All"
    )

    val marketFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = TransactionsModel.FilterName.Market,
        itemsList = marketItems.map { it.name },
        selectedItem = null
    )

    val produceFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = TransactionsModel.FilterName.Produce,
        itemsList = produceItems.map { it.key },
        selectedItem = null
    )

    val charityFilter: TransactionsModel.Filter = TransactionsModel.Filter(
        name = TransactionsModel.FilterName.Charity,
        itemsList = listOf("St. James", "Southvale", "Manchester"),
        selectedItem = null
    )

    return listOf(transactionsFilter, produceFilter, marketFilter, charityFilter)
}

fun List<TransactionsModel.Filter>.exposeFilters(): List<TransactionsModel.Filter> {
    // If SELL or ALL are selected in TransactionType filter -> Market filter pops up
    // If DONATE or ALL are selected in TransactionType filter -> Charity filter pops up

    val type: TransactionsModel.Filter? = this.firstOrNull{ it.name == TransactionsModel.FilterName.Type }
    val market: TransactionsModel.Filter? = this.firstOrNull { it.name == TransactionsModel.FilterName.Market }
    val charity: TransactionsModel.Filter? = this.firstOrNull { it.name == TransactionsModel.FilterName.Charity }
    return type?.let {
        when( type.selectedItem){
            "Harvest"-> this.filter { it.name != TransactionsModel.FilterName.Market && it.name != TransactionsModel.FilterName.Charity }
            "Sell"-> this.filter { it.name != TransactionsModel.FilterName.Charity }
            "Donate"-> this.filter { it.name != TransactionsModel.FilterName.Market }
            else -> {
                if (market?.selectedItem != null) {
                    this.filter { it.name != TransactionsModel.FilterName.Charity }
                } else if (charity?.selectedItem != null) {
                    this.filter { it.name != TransactionsModel.FilterName.Market }
                } else this
            }
        }
    }?:this
}