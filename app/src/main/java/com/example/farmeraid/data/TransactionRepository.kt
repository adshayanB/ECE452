package com.example.farmeraid.data

import com.example.farmeraid.home.model.HomeModel
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TransactionRepository {
    private val transactionList: MutableList<HomeModel.Transaction> = mutableListOf()
    private val atomicInt: AtomicInteger = AtomicInteger(0)

    fun addNewTransaction(transactionType: HomeModel.TransactionType, produceChanges: MutableMap<String, Int>, locationName: String) {
        val transactionMessage: StringBuilder = StringBuilder()

        when (transactionType) {
            HomeModel.TransactionType.HARVEST -> transactionMessage.append("Harvested ")
            HomeModel.TransactionType.SELL -> transactionMessage.append("Sold ")
            HomeModel.TransactionType.DONATE -> transactionMessage.append("Donated ")
            HomeModel.TransactionType.ALL -> {}
        }

        for ((produceName, produceAmount) in produceChanges.entries) {
            transactionMessage.append("$produceAmount $produceName, ")
        }

        transactionMessage.setLength(transactionMessage.length - 2)

        if (locationName.isNotEmpty()) transactionMessage.append(" to $locationName")

        val transaction: HomeModel.Transaction = HomeModel.Transaction(atomicInt.incrementAndGet(), transactionType, produceChanges, locationName, transactionMessage.toString())

        transactionList.add(transaction)
    }

    fun getRecentTransactions(transactionType: HomeModel.TransactionType, limit: Int): List<HomeModel.Transaction> {
        return if (transactionType == HomeModel.TransactionType.ALL) {
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