package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "agent"
    val content: String,
    val type: String = "text", // "text", "transaction_confirm", "insight"
    val metadata: String? = null, // JSON string for transaction confirmation data
    val date: Long = System.currentTimeMillis()
)
