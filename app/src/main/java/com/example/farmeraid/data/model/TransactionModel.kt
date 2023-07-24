package com.example.farmeraid.data.model

import com.cesarferreira.pluralize.pluralize
import java.text.NumberFormat
import java.util.Locale

class TransactionModel {
    enum class TransactionType(val stringValue: String) {
        HARVEST("Harvest"),
        SELL("Sell"),
        DONATE("Donate"),
        ALL("All");
    }
    data class Transaction(
        val transactionId: String,
        val transactionType: String,
        val produce: InventoryModel.Produce,
        val pricePerProduce: Double,
        val location: String, // either market name for "SELL" or community fridge name for "DONATE"
    )
}

fun TransactionModel.Transaction.toMessage() : String{
    return when (transactionType) {
        TransactionModel.TransactionType.HARVEST.stringValue -> {
            "Harvested ${this.produce.produceAmount} ${this.produce.produceName.pluralize(this.produce.produceAmount)}"
        }
        TransactionModel.TransactionType.SELL.stringValue -> {
            "Sold ${this.produce.produceAmount} ${this.produce.produceName.pluralize(this.produce.produceAmount)}" +
                    " for ${NumberFormat.getCurrencyInstance(Locale("en", "US")).format(this.pricePerProduce*this.produce.produceAmount)}" +
                    " to ${this.location}"
        }
        TransactionModel.TransactionType.DONATE.stringValue -> {
            "Donated ${this.produce.produceAmount} ${this.produce.produceName.pluralize(this.produce.produceAmount)}" +
                    " to ${this.location}"
        }
        else -> {
            throw Exception("Cannot build a message from this transaction")
        }
    }
}