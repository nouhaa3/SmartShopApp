package com.example.smartshopapp.ui.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.domain.ProductViewModel
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo
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

    val product by viewModel.product.collectAsState()

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
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(product) {
        product?.let {
            name = it.name
            category = it.category
            quantityText = it.quantity.toString()
            priceText = it.price.toString()
            imagePath = it.imagePath
        }
    }

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
                    title = {
                        Text(
                            "Edit Product",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    },
                    navigationIcon = {
                        Surface(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(44.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(top = 8.dp)
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
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {

                Spacer(Modifier.height(16.dp))

                /* ---------- IMAGE PICKER SECTION ---------- */
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Color.Black.copy(alpha = 0.15f)
                            )
                            .background(Color.White, CircleShape)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            newImageUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(newImageUri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            imagePath != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(File(imagePath!!)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = OldRose.copy(alpha = 0.1f),
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                Icons.Default.AddAPhoto,
                                                contentDescription = "Add Image",
                                                tint = OldRose,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Add Photo",
                                        color = SpaceIndigo.copy(alpha = 0.6f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.3.sp
                                    )
                                }
                            }
                        }

                        // Overlay gradient when image exists
                        if (newImageUri != null || imagePath != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            )
                                        ),
                                        CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Tap to change photo",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.2.sp
                    )
                }

                Spacer(Modifier.height(32.dp))

                /* ---------- FORM FIELDS ---------- */
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "Product Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpaceIndigo,
                            letterSpacing = 0.3.sp
                        )

                        Spacer(Modifier.height(20.dp))

                        // Name Field
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Product Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OldRose,
                                focusedLabelColor = OldRose,
                                cursorColor = OldRose,
                                focusedTextColor = SpaceIndigo,
                                unfocusedTextColor = SpaceIndigo
                            ),
                            singleLine = true
                        )

                        Spacer(Modifier.height(16.dp))

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OldRose,
                                    focusedLabelColor = OldRose,
                                    cursorColor = OldRose,
                                    focusedTextColor = SpaceIndigo,
                                    unfocusedTextColor = SpaceIndigo
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                cat,
                                                color = SpaceIndigo,
                                                fontWeight = if (cat == category) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            category = cat
                                            expanded = false
                                        },
                                        modifier = Modifier.background(
                                            if (cat == category)
                                                OldRose.copy(alpha = 0.08f)
                                            else Color.Transparent
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Quantity Field
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it.filter(Char::isDigit) },
                            label = { Text("Quantity") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OldRose,
                                focusedLabelColor = OldRose,
                                cursorColor = OldRose,
                                focusedTextColor = SpaceIndigo,
                                unfocusedTextColor = SpaceIndigo
                            ),
                            singleLine = true
                        )

                        Spacer(Modifier.height(16.dp))

                        // Price Field
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it.replace(',', '.') },
                            label = { Text("Price (DT)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OldRose,
                                focusedLabelColor = OldRose,
                                cursorColor = OldRose,
                                focusedTextColor = SpaceIndigo,
                                unfocusedTextColor = SpaceIndigo
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                /* ---------- SAVE BUTTON ---------- */
                Button(
                    onClick = {
                        val finalImagePath = newImageUri?.let {
                            copyImageToInternalStorage(context, it)
                        } ?: imagePath

                        val updated = product!!.copy(
                            name = name,
                            category = category,
                            quantity = quantityText.toIntOrNull() ?: 0,
                            price = priceText.toDoubleOrNull() ?: 0.0,
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = OldRose
                    )
                ) {
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}