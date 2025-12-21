package com.example.smartshopapp.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import java.io.File
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                                    Icons.Default.ArrowBack,
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
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                /* ---------- PRODUCT IMAGE ---------- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    if (product.imagePath != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = if (product.imagePath.startsWith("https://"))
                                    product.imagePath
                                else
                                    File(product.imagePath)
                            ),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.1f),
                                            Color.White.copy(alpha = 0.15f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No Image Available",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        OldRose.copy(alpha = 0.3f)
                                    ),
                                    startY = 500f
                                )
                            )
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(20.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = product.category,
                            color = OldRose,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                /* ---------- PRODUCT INFO CARD ---------- */
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Text(
                            text = product.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SpaceIndigo,
                            letterSpacing = 0.5.sp,
                            lineHeight = 32.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        Divider(
                            color = OldRose.copy(alpha = 0.15f),
                            thickness = 1.dp
                        )

                        Spacer(Modifier.height(24.dp))

                        InfoRow(
                            label = "Quantity",
                            value = "${product.quantity} items"
                        )

                        Spacer(Modifier.height(20.dp))

                        InfoRow(
                            label = "Price",
                            value = "${product.price} DT",
                            highlight = true
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                /* ---------- ACTION BUTTONS ---------- */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color.Black.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Edit",
                                color = OldRose,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color.Black.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Delete",
                                color = Color(0xFFE57373),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                productName = product.name,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = SpaceIndigo.copy(alpha = 0.6f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp
        )

        Text(
            text = value,
            color = if (highlight) OldRose else SpaceIndigo,
            fontSize = if (highlight) 22.sp else 16.sp,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.Bold,
            letterSpacing = if (highlight) 0.5.sp else 0.3.sp
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    productName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = RoundedCornerShape(50),
                color = OldRose.copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = OldRose,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Delete Product?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SpaceIndigo
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Are you sure you want to delete",
                    fontSize = 14.sp,
                    color = SpaceIndigo.copy(alpha = 0.7f)
                )
                Text(
                    text = "\"$productName\"?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SpaceIndigo
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This action cannot be undone.",
                    fontSize = 12.sp,
                    color = OldRose,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = "Delete",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SpaceIndigo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    )
}