package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String,
    val description: String,
    val date: Long = System.currentTimeMillis()
)
