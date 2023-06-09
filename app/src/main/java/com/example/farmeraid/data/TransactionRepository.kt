package com.example.farmeraid.data

import com.example.farmeraid.home.model.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.atomic.AtomicInteger

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class TransactionRepository {
    enum class TransactionType(val stringValue: String) {
        HARVEST("Harvest"),
        SELL("Sell"),
        DONATE("Donate"),
        ALL("All");


    }
    data class Transaction(
        val transactionId: Int,
        val transactionType: TransactionType,
        val produceChanges: MutableMap<String, Int>,
        val locationName: String, // either market name for "SELL" or community fridge name for "DONATE"
        val transactionMessage: String
    )

    private val transactionList: MutableList<Transaction> = mutableListOf(
        Transaction(1, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples"),
        Transaction(2, TransactionType.SELL, HashMap(), "", "Sold 30 Apples to St. Jacob's"),
        Transaction(3, TransactionType.DONATE, HashMap(), "", "Donated 30 Apples to UW Fridge"),
        Transaction(4, TransactionType.HARVEST, HashMap(), "", "Harvested 10 Bananas"),
        Transaction(5, TransactionType.DONATE, HashMap(), "", "Donated 20 Bananas to TO Fridge"),
        Transaction(6, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples, 10 Bananas"),
        Transaction(7, TransactionType.SELL, HashMap(), "", "Sold 30 Apples, 10 Oranges to St. Jacob's"),
        Transaction(8, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples, 10 Bananas, 50 Oranges"),
        Transaction(9, TransactionType.SELL, HashMap(), "", "Sold 30 Apples, 10 Oranges, 20 Bananas to St. Lawrence"),
        Transaction(10, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples, 50 Oranges"),
        Transaction(11, TransactionType.DONATE, HashMap(), "", "Donated 20 Bananas, 10 Oranges to TO Fridge"),
        Transaction(12, TransactionType.SELL, HashMap(), "", "Sold 20 Bananas to St. Lawrence"),
        Transaction(13, TransactionType.DONATE, HashMap(), "", "Donated 20 Bananas, 15 Apples, 5 Oranges to UW Fridge"),
        Transaction(14, TransactionType.SELL, HashMap(), "", "Sold 10 Oranges to St. Lawrence"),
        Transaction(15, TransactionType.DONATE, HashMap(), "", "Donated 10 Oranges to TO Fridge")
    )

    private val atomicInt: AtomicInteger = AtomicInteger(0)

    fun addNewTransaction(transactionType: TransactionType, produceChanges: MutableMap<String, Int>, locationName: String) {
        val transactionMessage: StringBuilder = StringBuilder()

        when (transactionType) {
            TransactionType.HARVEST -> transactionMessage.append("Harvested ")
            TransactionType.SELL -> transactionMessage.append("Sold ")
            TransactionType.DONATE -> transactionMessage.append("Donated ")
            TransactionType.ALL -> {}
        }

        for ((produceName, produceAmount) in produceChanges.entries) {
            transactionMessage.append("$produceAmount $produceName, ")
        }

        transactionMessage.setLength(transactionMessage.length - 2)

        if (locationName.isNotEmpty()) transactionMessage.append(" to $locationName")

        val transaction: Transaction = Transaction(atomicInt.incrementAndGet(), transactionType, produceChanges, locationName, transactionMessage.toString())

        transactionList.add(transaction)
    }

    fun getRecentTransactions(transactionType: TransactionType, limit: Int): List<Transaction> {
        return if (transactionType == TransactionType.ALL) {
            transactionList.takeLast(limit).reversed()
        } else {
            transactionList.filter { it.transactionType == transactionType }.takeLast(limit).reversed()
        }
    }

    fun deleteTransaction(transactionId: Int) {
        val index: Int = transactionList.binarySearchBy(transactionId) { it.transactionId }

        if (index < 0) {
        } else transactionList.removeAt(index)
    }
}