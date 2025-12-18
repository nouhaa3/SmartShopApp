package com.example.smartshopapp.ui.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.domain.ProductViewModel
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.utils.copyImageToInternalStorage
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var product by remember { mutableStateOf<Product?>(null) }

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    var imagePath by remember { mutableStateOf<String?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    val categories = listOf("Rings", "Necklaces", "Bracelets", "Earrings", "Watches")
    var expanded by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> newImageUri = uri }

    LaunchedEffect(productId) {
        val loaded = viewModel.getProductById(productId)
        product = loaded
        loaded?.let {
            name = it.name
            category = it.category
            quantityText = it.quantity.toString()
            priceText = it.price.toString()
            imagePath = it.imagePath
        }
    }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("Edit Product", color = Color.White) },
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

        if (product == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
            return@Scaffold
        }

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

                    /* IMAGE */
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(OldRose.copy(alpha = 0.15f), CircleShape)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            newImageUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(newImageUri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            imagePath != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(File(imagePath!!)),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint = OldRose,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

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
                    OutlinedTextField(priceText, { priceText = it.replace(',', '.') }, label = { Text("Price") })

                    Spacer(Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                        onClick = {
                            val finalImagePath = newImageUri?.let {
                                copyImageToInternalStorage(context, it)
                            } ?: imagePath

                            val updated = product!!.copy(
                                name = name,
                                category = category,
                                quantity = quantityText.toInt(),
                                price = priceText.toDouble(),
                                imagePath = finalImagePath
                            )

                            viewModel.updateProduct(
                                updated,
                                onSuccess = onBack,
                                onError = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                    ) {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
