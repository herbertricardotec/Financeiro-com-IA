package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.network.*
import kotlinx.coroutines.flow.Flow

class FinanceRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "finconnect_db"
    ).build()

    private val transactionDao = db.transactionDao()
    private val goalDao = db.goalDao()
    private val messageDao = db.messageDao()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    val allMessages: Flow<List<MessageEntity>> = messageDao.getAllMessages()

    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(id: Int) = transactionDao.deleteTransaction(id)

    suspend fun insertGoal(goal: GoalEntity) = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(id: Int) = goalDao.deleteGoal(id)

    suspend fun insertMessage(message: MessageEntity) = messageDao.insertMessage(message)

    suspend fun sendChatMessage(userInput: String, history: List<MessageEntity>): MessageEntity {
        // Build the contents list
        val contents = history.map { 
            Content(role = if (it.role == "user") "user" else "model", parts = listOf(Part(text = it.content)))
        }.toMutableList()
        contents.add(Content(role = "user", parts = listOf(Part(text = userInput))))

        // Define function schema for recording a transaction
        val recordTransactionTool = Tool(
            functionDeclarations = listOf(
                FunctionDeclaration(
                    name = "recordTransaction",
                    description = "Records a new financial transaction (income or expense).",
                    parameters = Schema(
                        type = "OBJECT",
                        properties = mapOf(
                            "type" to Schema(type = "STRING", description = "Must be exactly 'income' or 'expense'."),
                            "amount" to Schema(type = "NUMBER", description = "The numerical amount of the transaction."),
                            "category" to Schema(type = "STRING", description = "Categorization of the transaction, e.g. Alimentação, Transporte, Salário, Lazer, etc.")
                        ),
                        required = listOf("type", "amount", "category")
                    )
                )
            )
        )

        val request = GenerateContentRequest(
            systemInstruction = Content(parts = listOf(Part(text = "You are a friendly personal finance assistant named FinConnect Agent. Your goal is to help users track expenses, incomes, and understand their financial habits. Use the 'recordTransaction' function ONLY when the user explicitly states they spent or received money. Otherwise, offer financial advice, answer questions based on the chat context, or motivate them."))),
            contents = contents,
            tools = listOf(recordTransactionTool),
            generationConfig = GenerationConfig(temperature = 0.2f)
        )

        try {
            val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val candidate = response.candidates?.firstOrNull() ?: return MessageEntity(role = "agent", content = "Desculpe, não consegui entender.")
            
            // Check for function call
            val part = candidate.content?.parts?.firstOrNull()
            if (part?.functionCall != null) {
                val call = part.functionCall
                if (call.name == "recordTransaction" && call.args != null) {
                    val type = call.args["type"]?.toString() ?: "expense"
                    val amountRaw = call.args["amount"] as? Double ?: call.args["amount"].toString().toDoubleOrNull() ?: 0.0
                    val category = call.args["category"]?.toString() ?: "Outros"
                    
                    val transaction = TransactionEntity(
                        type = type.replace("\"", ""), // handle string wrapping if present
                        amount = amountRaw,
                        category = category.replace("\"", ""),
                        description = userInput
                    )
                    insertTransaction(transaction)

                    return MessageEntity(
                        role = "agent", 
                        content = "✅ Registrado: R$ $amountRaw em $category.",
                        type = "transaction_confirm"
                    )
                }
            } else {
                return MessageEntity(role = "agent", content = part?.text ?: "Desculpe, algo deu errado.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MessageEntity(role = "agent", content = "Erro de conexão: ${e.message}")
        }
        return MessageEntity(role = "agent", content = "Erro desconhecido.")
    }
}
