package com.example.smartshopapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductsClick: () -> Unit,
    onStatsClick: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartShop") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            Button(
                onClick = onProductsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Products")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onStatsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Statistics")
            }
        }
    }
}
