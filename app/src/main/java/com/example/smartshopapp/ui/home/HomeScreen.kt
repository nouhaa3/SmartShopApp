package com.example.smartshopapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            OldRose,
                            OldRose.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            Surface(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                },
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .size(44.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(top = 8.dp)
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

                    Spacer(modifier = Modifier.height(40.dp))

                    // WELCOME TEXT
                    Text(
                        text = "Bella Rose",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Where elegance meets beauty",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Manage your store with grace",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // ACTION CARDS
                    HomeActionCard(
                        icon = Icons.Outlined.Inventory2,
                        title = "Manage Products",
                        subtitle = "Add, update & organize your jewellery",
                        onClick = onProductsClick
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HomeActionCard(
                        icon = Icons.Outlined.BarChart,
                        title = "Statistics",
                        subtitle = "Track sales & performance",
                        onClick = onStatsClick
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/* ---------------- ELEGANT DRAWER ---------------- */

@Composable
private fun DrawerContent(
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
            .padding(horizontal = 28.dp)
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        // BRAND SECTION
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Bella Rose",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = OldRose,
                fontSize = 28.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Jewellery Store",
                style = MaterialTheme.typography.bodyLarge,
                color = SpaceIndigo.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                letterSpacing = 0.3.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Divider(
            color = OldRose.copy(alpha = 0.12f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // MENU SECTION
        Text(
            text = "MENU",
            style = MaterialTheme.typography.labelSmall,
            color = SpaceIndigo.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        DrawerItem(
            icon = Icons.Outlined.Person,
            text = "Profile",
            onClick = onProfileClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerItem(
            icon = Icons.Outlined.Logout,
            text = "Logout",
            onClick = onLogoutClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // FOOTER
        Divider(
            color = OldRose.copy(alpha = 0.08f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Crafted with",
                style = MaterialTheme.typography.bodySmall,
                color = SpaceIndigo.copy(alpha = 0.4f),
                fontSize = 13.sp
            )
            Text(
                text = " ♥ ",
                style = MaterialTheme.typography.bodySmall,
                color = OldRose,
                fontSize = 13.sp
            )
            Text(
                text = "for you",
                style = MaterialTheme.typography.bodySmall,
                color = SpaceIndigo.copy(alpha = 0.4f),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = OldRose.copy(alpha = 0.1f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = OldRose,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = SpaceIndigo,
                fontSize = 16.sp,
                letterSpacing = 0.2.sp
            )
        }
    }
}


/* ---------------- PREMIUM HOME CARD ---------------- */

@Composable
private fun HomeActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Surface(
                shape = CircleShape,
                color = OldRose.copy(alpha = 0.12f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = OldRose,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SpaceIndigo,
                    fontSize = 20.sp,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpaceIndigo.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}