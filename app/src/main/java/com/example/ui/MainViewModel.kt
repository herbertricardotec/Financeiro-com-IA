package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: FinanceRepository) : ViewModel() {

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val messages: StateFlow<List<MessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // First save user message
            val userMsg = MessageEntity(role = "user", content = text)
            repository.insertMessage(userMsg)
            
            // Get history to give context to Gemini
            val history = messages.value.takeLast(10)
            
            // Send to Gemini and wait for response
            val reply = repository.sendChatMessage(text, history)
            repository.insertMessage(reply)
        }
    }

    fun addGoal(name: String, target: Double) {
        viewModelScope.launch {
            repository.insertGoal(GoalEntity(name = name, targetAmount = target))
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }
    
    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
