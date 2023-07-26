package com.example.farmeraid.data.model

import androidx.compose.ui.graphics.Color
import com.cesarferreira.pluralize.pluralize
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class TransactionModel {
    enum class TransactionType(val stringValue: String, val colour: Color?) {
        HARVEST("Harvest", Color(0xFFE5F0C8)),
        SELL("Sell", Color(0xFFFBEECC)),
        DONATE("Donate", Color(0xFFFFE6DB)),
        ALL("All", null);

        companion object{
            fun from(type : String) : TransactionType {
                return when (type) {
                    HARVEST.stringValue -> HARVEST
                    SELL.stringValue -> SELL
                    DONATE.stringValue -> DONATE
                    else -> ALL
                }
            }
        }
    }
    data class Transaction(
        val transactionId: String = "",
        val transactionType: TransactionType,
        val produce: InventoryModel.Produce,
        val pricePerProduce: Double,
        val location: String, // either market name for "SELL" or community fridge name for "DONATE"
    )
}

fun TransactionModel.Transaction.toMessage() : String{
    return when (transactionType) {
        TransactionModel.TransactionType.HARVEST -> {
            "Harvested ${this.produce.produceAmount} ${this.produce.produceName}"
        }
        TransactionModel.TransactionType.SELL -> {
            "Sold ${this.produce.produceAmount} ${this.produce.produceName}" +
                    " for ${NumberFormat.getCurrencyInstance(Locale("en", "US")).format(this.pricePerProduce*this.produce.produceAmount)}" +
                    " to ${this.location}"
        }
        TransactionModel.TransactionType.DONATE -> {
            "Donated ${this.produce.produceAmount} ${this.produce.produceName}" +
                    " to ${this.location}"
        }
        else -> {
            throw Exception("Cannot build a message from this transaction")
        }
    }
}

fun LocalDateTime.toDate() : Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}
fun Date.toLocalDateTime() : LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
}
fun Timestamp.toLocalDateTime() : LocalDateTime {
    return this.toDate().toLocalDateTime()
}