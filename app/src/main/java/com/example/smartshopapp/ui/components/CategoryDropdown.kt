package com.example.smartshopapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    value: String,
    categories: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Category"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor(),
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
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            category,
                            color = SpaceIndigo,
                            fontWeight = if (category == value) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onValueChange(category)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (category == value)
                            OldRose.copy(alpha = 0.08f)
                        else Color.Transparent
                    )
                )
            }
        }
    }
}