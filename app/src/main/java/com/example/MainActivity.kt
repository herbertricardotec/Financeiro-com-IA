package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.FinanceRepository
import com.example.ui.MainViewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: FinanceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = FinanceRepository(this)
        
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(repository))
                val transactions by viewModel.transactions.collectAsState()
                val goals by viewModel.goals.collectAsState()
                val messages by viewModel.messages.collectAsState()

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                                label = { Text("Início", fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                selected = currentRoute == "dashboard",
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                onClick = { navController.navigate("dashboard") { launchSingleTop = true } }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
                                label = { Text("Chat", fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                selected = currentRoute == "chat",
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                onClick = { navController.navigate("chat") { launchSingleTop = true } }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.TrackChanges, contentDescription = "Metas") },
                                label = { Text("Metas", fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                selected = currentRoute == "goals",
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                onClick = { navController.navigate("goals") { launchSingleTop = true } }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.PieChart, contentDescription = "Relatos") },
                                label = { Text("Relatos", fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                selected = currentRoute == "reports",
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                onClick = { navController.navigate("reports") { launchSingleTop = true } }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                transactions = transactions,
                                onNavigateToChat = { navController.navigate("chat") { launchSingleTop = true } }
                            )
                        }
                        composable("chat") {
                            ChatScreen(
                                messages = messages,
                                onSendMessage = { viewModel.sendMessage(it) }
                            )
                        }
                        composable("goals") {
                            GoalsScreen(
                                goals = goals,
                                onAddGoal = { name, target -> viewModel.addGoal(name, target) }
                            )
                        }
                        composable("reports") {
                            ReportsScreen(transactions = transactions)
                        }
                    }
                }
            }
        }
    }
}
