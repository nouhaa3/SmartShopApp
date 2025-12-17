package com.example.smartshopapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.ui.theme.OldRose
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductsClick: () -> Unit,
    onStatsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onProfileClick()
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            containerColor = OldRose,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = OldRose
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(36.dp))

                // WELCOME TEXT
                Text(
                    text = "Welcome to Bella Rose",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Where elegance meets beauty",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Manage your store with grace",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(100.dp))

                // ACTION CARDS (CENTERED)
                HomeActionCard(
                    title = "Manage Products",
                    subtitle = "Add, update & organize your jewellery",
                    onClick = onProductsClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                HomeActionCard(
                    title = "Statistics",
                    subtitle = "Track sales & performance",
                    onClick = onStatsClick
                )
            }
        }
    }
}

/* ---------------- DRAWER ---------------- */

@Composable
private fun DrawerContent(
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        // BRAND TITLE
        Text(
            text = "Bella Rose ✨",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = OldRose
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Jewellery Store",
            style = MaterialTheme.typography.bodyMedium,
            color = OldRose.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Divider(
            color = OldRose.copy(alpha = 0.15f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // MENU SECTION
        DrawerItem(
            icon = Icons.Outlined.Person,
            text = "Profile",
            onClick = onProfileClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        DrawerItem(
            icon = Icons.Outlined.Logout,
            text = "Logout",
            onClick = onLogoutClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // FOOTER
        Divider(
            color = OldRose.copy(alpha = 0.1f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crafted with love 💗",
            style = MaterialTheme.typography.bodySmall,
            color = OldRose.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = OldRose
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3B2F3A)
        )
    }
}


/* ---------------- BIG HOME CARD ---------------- */

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OldRose
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
