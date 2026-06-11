package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun messageDao(): MessageDao
}
