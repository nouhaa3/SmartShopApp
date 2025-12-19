package com.example.smartshopapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.domain.UserViewModel
import com.example.smartshopapp.ui.theme.OldRose
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uid: String,
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val userProfile by userViewModel.user.collectAsState()

    LaunchedEffect(uid) {
        userViewModel.loadUser(uid)
    }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OldRose)
            )
        }
    ) { padding ->

        if (userProfile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
            return@Scaffold
        }

        val user = userProfile!!

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // IMAGE
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = OldRose,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = OldRose)
                Spacer(Modifier.width(8.dp))
                Text("Logout", color = OldRose)
            }
        }
    }
}
