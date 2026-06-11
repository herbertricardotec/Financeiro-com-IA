package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import java.text.NumberFormat
import java.util.*

@Composable
fun ReportsScreen(transactions: List<TransactionEntity>) {
    val expenses = transactions.filter { it.type == "expense" }
    val grouped = expenses.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }.toList()
    val totalExpense = expenses.sumOf { it.amount }

    val colors = listOf(Color(0xFFFF5252), Color(0xFFFF9800), Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFF9C27B0))
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Relatórios", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (expenses.isEmpty()) {
                Text("Sem despesas para gerar relatórios.")
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = 0f
                        grouped.forEachIndexed { index, pair ->
                            val sweepAngle = ((pair.second / totalExpense) * 360f).toFloat()
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true
                            )
                            startAngle += sweepAngle
                        }
                    }
                }
            }
        }
        
        if (grouped.isNotEmpty()) {
            item {
                Text("Gastos por categoria", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(grouped.size) { index ->
                val pair = grouped[index]
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        Surface(color = colors[index % colors.size], modifier = Modifier.size(16.dp)) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pair.first)
                    }
                    Text(format.format(pair.second))
                }
            }
        }
    }
}
