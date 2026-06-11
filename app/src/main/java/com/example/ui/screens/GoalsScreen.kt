package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import java.text.NumberFormat
import java.util.*

@Composable
fun GoalsScreen(
    goals: List<GoalEntity>,
    onAddGoal: (String, Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Meta")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Minhas Metas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            if (goals.isEmpty()) {
                item {
                    Text("Você ainda não definiu nenhuma meta.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(goals) { goal ->
                    GoalCard(goal)
                }
            }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nova Meta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Meta (ex: Viagem)") }
                    )
                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("Valor Alvo (ex: 5000)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val targetNum = target.toDoubleOrNull()
                    if (name.isNotBlank() && targetNum != null) {
                        onAddGoal(name, targetNum)
                        showDialog = false
                    }
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun GoalCard(goal: GoalEntity) {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Acumulado: ${format.format(goal.currentAmount)}", fontSize = 12.sp)
                Text("Alvo: ${format.format(goal.targetAmount)}", fontSize = 12.sp)
            }
        }
    }
}
