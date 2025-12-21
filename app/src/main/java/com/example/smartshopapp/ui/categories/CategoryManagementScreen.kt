package com.example.smartshopapp.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshopapp.data.model.Category
import com.example.smartshopapp.domain.CategoryViewModel
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: CategoryViewModel,
    onBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

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
                            "Manage Categories",
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color.White,
                    contentColor = OldRose,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Category",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) { padding ->

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                ) {

                    Spacer(Modifier.height(16.dp))

                    // Info Card
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${categories.size} categories",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    if (categories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No categories yet",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.3.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Add your first category",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(categories) { category ->
                                CategoryCard(
                                    category = category,
                                    onClick = { selectedCategory = category },
                                    onToggleStatus = { viewModel.toggleCategoryStatus(category) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AddCategoryDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    viewModel.addCategory(
                        name = name,
                        onSuccess = { showAddDialog = false },
                        onError = { /* Handle error */ }
                    )
                }
            )
        }

        // Category Options Dialog
        selectedCategory?.let { category ->
            CategoryOptionsDialog(
                category = category,
                onDismiss = { selectedCategory = null },
                onEdit = {
                    editingCategory = category
                    selectedCategory = null
                },
                onDelete = {
                    deletingCategory = category
                    selectedCategory = null
                }
            )
        }

        // Edit Dialog
        editingCategory?.let { category ->
            EditCategoryDialog(
                category = category,
                onDismiss = { editingCategory = null },
                onConfirm = { updatedName ->
                    viewModel.updateCategory(
                        category.copy(name = updatedName),
                        onSuccess = { editingCategory = null },
                        onError = { /* Handle error */ }
                    )
                }
            )
        }

        // Delete Confirmation
        deletingCategory?.let { category ->
            DeleteCategoryDialog(
                category = category,
                onDismiss = { deletingCategory = null },
                onConfirm = {
                    viewModel.deleteCategory(
                        category.id,
                        onSuccess = { deletingCategory = null },
                        onError = { /* Handle error */ }
                    )
                }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpaceIndigo,
                    letterSpacing = 0.3.sp
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (category.isActive) OldRose else Color.Gray,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (category.isActive) "Active" else "Inactive",
                        color = SpaceIndigo.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )
                }
            }

            // Toggle Status Button
            Surface(
                onClick = { onToggleStatus() },
                shape = RoundedCornerShape(12.dp),
                color = if (category.isActive)
                    OldRose.copy(alpha = 0.1f)
                else
                    Color.Gray.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (category.isActive) "Deactivate" else "Activate",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (category.isActive) OldRose else Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryOptionsDialog(
    category: Category,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = category.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SpaceIndigo,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Choose an action",
                    fontSize = 14.sp,
                    color = SpaceIndigo.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Edit Button
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "Edit Category",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                // Delete Button
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE57373)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "Delete Category",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = SpaceIndigo
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    )
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

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
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Category",
                        tint = OldRose,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Add Category",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SpaceIndigo
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Enter a name for your new category",
                    fontSize = 14.sp,
                    color = SpaceIndigo.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OldRose,
                        focusedLabelColor = OldRose,
                        cursorColor = OldRose
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = name.isNotBlank()
            ) {
                Text(
                    text = "Add Category",
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

@Composable
private fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }

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
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Category",
                        tint = OldRose,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Edit Category",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SpaceIndigo
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Update the name for this category",
                    fontSize = 14.sp,
                    color = SpaceIndigo.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OldRose,
                        focusedLabelColor = OldRose,
                        cursorColor = OldRose
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = name.isNotBlank()
            ) {
                Text(
                    text = "Save Changes",
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

@Composable
private fun DeleteCategoryDialog(
    category: Category,
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
                text = "Delete Category?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SpaceIndigo
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Are you sure you want to delete",
                    fontSize = 14.sp,
                    color = SpaceIndigo.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "\"${category.name}\"?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SpaceIndigo,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This action cannot be undone.",
                    fontSize = 12.sp,
                    color = OldRose,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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