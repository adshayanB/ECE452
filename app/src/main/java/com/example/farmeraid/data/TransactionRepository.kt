package com.example.farmeraid.data

import com.example.farmeraid.home.model.HomeModel
import java.util.concurrent.atomic.AtomicInteger

class TransactionRepository {
    enum class TransactionType(val stringValue: String) {
        HARVEST("Harvest"),
        SELL("Sell"),
        DONATE("Donate"),
        ALL("All")
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
        Transaction(1, TransactionType.SELL, HashMap(), "", "Sold 30 Apples to St. Jacob's"),
        Transaction(1, TransactionType.DONATE, HashMap(), "", "Donated 30 Apples to UW Fridge"),
        Transaction(1, TransactionType.HARVEST, HashMap(), "", "Harvested 10 Bananas"),
        Transaction(1, TransactionType.DONATE, HashMap(), "", "Donated 20 Bananas to TO Fridge"),
        Transaction(1, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples, 10 Bananas"),
        Transaction(1, TransactionType.SELL, HashMap(), "", "Sold 30 Apples, 10 Oranges to St. Jacob's"),
        Transaction(1, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples, 10 Bananas, 50 Oranges"),
        Transaction(1, TransactionType.SELL, HashMap(), "", "Sold 30 Apples, 10 Oranges, 20 Bananas to St. Lawrence"),
        Transaction(1, TransactionType.HARVEST, HashMap(), "", "Harvested 30 Apples, 50 Oranges"),
        Transaction(1, TransactionType.DONATE, HashMap(), "", "Donated 20 Bananas, 10 Oranges to TO Fridge"),
        Transaction(1, TransactionType.SELL, HashMap(), "", "Sold 20 Bananas to St. Lawrence"),
        Transaction(1, TransactionType.DONATE, HashMap(), "", "Donated 20 Bananas, 15 Apples, 5 Oranges to UW Fridge"),
        Transaction(1, TransactionType.SELL, HashMap(), "", "Sold 10 Oranges to St. Lawrence"),
        Transaction(1, TransactionType.DONATE, HashMap(), "", "Donated 10 Oranges to TO Fridge")
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
            transactionList.takeLast(limit)
        } else {
            transactionList.filter { it.transactionType == transactionType }.takeLast(limit)
        }
    }

    fun deleteTransaction(transactionId: Int) {
        val index: Int = transactionList.binarySearchBy(transactionId) { it.transactionId }

        if (index < 0) {
        } else transactionList.removeAt(index)
    }
}