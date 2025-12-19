package com.example.smartshopapp.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartshopapp.domain.UserViewModel
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.utils.copyImageToInternalStorage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uid: String,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user by userViewModel.user.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { newImageUri = it }

    LaunchedEffect(uid) {
        userViewModel.loadUser(uid)
    }

    LaunchedEffect(user) {
        user?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phone
            imagePath = it.imagePath
        }
    }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OldRose)
            )
        }
    ) { padding ->

        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(OldRose.copy(alpha = 0.15f))
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            newImageUri != null ->
                                Image(
                                    painter = rememberAsyncImagePainter(newImageUri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                            imagePath != null ->
                                Image(
                                    painter = rememberAsyncImagePainter(File(imagePath!!)),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                            else ->
                                Icon(Icons.Default.AddAPhoto, null, tint = OldRose)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(fullName, { fullName = it }, label = { Text("Full name") })
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(email, { email = it }, label = { Text("Email") })
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })

                    Spacer(Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                        onClick = {
                            val finalImage = newImageUri?.let {
                                copyImageToInternalStorage(context, it)
                            } ?: imagePath

                            userViewModel.updateUser(
                                user!!.copy(
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    imagePath = finalImage
                                ),
                                onSuccess = onBack,
                                onError = {}
                            )
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
