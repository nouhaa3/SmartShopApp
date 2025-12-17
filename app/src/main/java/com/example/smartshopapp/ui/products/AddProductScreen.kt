package com.example.smartshopapp.ui.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.domain.ProductViewModel
import com.example.smartshopapp.ui.theme.OldRose
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val categories = listOf("Rings", "Necklaces", "Bracelets", "Earrings", "Watches")
    var expanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("Add Jewellery", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OldRose)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.padding(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val context = LocalContext.current

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(OldRose.copy(alpha = 0.15f), CircleShape)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            val bitmap = remember(imageUri) {
                                context.contentResolver.openInputStream(imageUri!!)?.use {
                                    BitmapFactory.decodeStream(it)
                                }
                            }

                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "Add Image",
                                tint = OldRose,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    Spacer(Modifier.height(12.dp))

                    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded, { expanded = false }) {
                            categories.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        category = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(quantityText, { quantityText = it.filter(Char::isDigit) }, label = { Text("Quantity") })
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(priceText, { priceText = it }, label = { Text("Price") })

                    Spacer(Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                        onClick = {
                            viewModel.addProduct(
                                name = name,
                                category = category,
                                quantity = quantityText.toInt(),
                                price = priceText.toDouble(),
                                imageUri = imageUri?.toString(),
                                onSuccess = onBack,
                                onError = {
                                    scope.launch { snackbarHostState.showSnackbar(it) }
                                }
                            )
                        }
                    ) {
                        Text("Save Product", color = Color.White)
                    }
                }
            }
        }
    }
}

