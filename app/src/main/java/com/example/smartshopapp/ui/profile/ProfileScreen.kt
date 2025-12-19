package com.example.smartshopapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartshopapp.domain.UserViewModel
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uid: String,
    userViewModel: UserViewModel,
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val user by userViewModel.user.collectAsState()

    LaunchedEffect(uid) {
        userViewModel.loadUser(uid)
    }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OldRose),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->

        if (user == null) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    /* ---------- PROFILE IMAGE ---------- */
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(OldRose.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!user!!.imagePath.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    File(user!!.imagePath!!)
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = OldRose,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    /* ---------- USER DATA ---------- */
                    Text(
                        text = user!!.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        color = SpaceIndigo
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = user!!.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SpaceIndigo
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = user!!.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SpaceIndigo
                    )

                    Spacer(Modifier.height(30.dp))

                    /* ---------- EDIT BUTTON ---------- */
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OldRose)
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profile", color = Color.White)
                    }

                    Spacer(Modifier.height(12.dp))

                    /* ---------- LOGOUT BUTTON ---------- */
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OldRose.copy(alpha = 0.85f)
                        )
                    ) {
                        Text("Logout", color = Color.White)
                    }
                }
            }
        }
    }
}
