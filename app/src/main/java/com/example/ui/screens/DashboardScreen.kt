package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    transactions: List<TransactionEntity>,
    onNavigateToChat: () -> Unit
) {
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToChat,
                icon = { Icon(Icons.Filled.Chat, contentDescription = null) },
                text = { Text("Falar com Agente") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Resumo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Saldo disponível", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = currencyFormat.format(balance),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(
                        title = "Receitas",
                        amount = totalIncome,
                        icon = Icons.Filled.ArrowUpward,
                        iconColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Despesas",
                        amount = totalExpense,
                        icon = Icons.Filled.ArrowDownward,
                        iconColor = Color(0xFFFF5252),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Últimas Transações",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Nenhuma transação ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Mande uma mensagem no chat!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(transactions.take(5)) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color, modifier: Modifier = Modifier) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title.uppercase(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(currencyFormat.format(amount), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val isIncome = transaction.type == "income"
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFormat = SimpleDateFormat("dd MMM", Locale("pt", "BR"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isIncome) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                contentDescription = null,
                tint = if (isIncome) Color(0xFF4CAF50) else Color(0xFFFF5252)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.category, fontWeight = FontWeight.SemiBold)
            Text(dateFormat.format(Date(transaction.date)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = "${if(isIncome) "+" else "-"} ${currencyFormat.format(transaction.amount)}",
            fontWeight = FontWeight.Bold,
            color = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
        )
    }
}
