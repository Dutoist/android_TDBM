package com.unilasalle.tp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unilasalle.tp.services.network.ApiService
import kotlinx.coroutines.launch

@Composable
fun CategoryFilter(onSelected: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var categories by remember { mutableStateOf(listOf<String>()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    val apiService = ApiService.createService()

    // Fetch categories on composable launch
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                categories = apiService.getCategories()
            } catch (e: Exception) {
                categories = listOf("All") // Default to All if fetching fails
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Button with rounded corners on left and right
        Text(
            text = selectedCategory,
            color = Color.White,  // Set the text color to white
            modifier = Modifier
                .fillMaxWidth()  // Make the button take the full width
                .clickable { expanded = true }  // Toggle the dropdown when clicked
                .padding(horizontal = 24.dp, vertical = 16.dp)  // Padding around text
                .background(
                    color = MaterialTheme.colorScheme.primary, // Background color from theme
                    shape = RoundedCornerShape(50.dp) // Apply rounded corners to left and right
                )
                .wrapContentWidth() // Center the text horizontally inside the button
                .padding(16.dp) // Additional padding for comfort
        )

        // Dropdown menu for selecting category
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }  // Close the menu when clicked outside
        ) {
            // "All" option to show all products
            DropdownMenuItem(
                onClick = {
                    selectedCategory = "All"
                    expanded = false
                    onSelected("All")  // Notify parent composable of the selection
                },
                text = { Text("All") }
            )

            // Loop through categories and display them in the dropdown
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        selectedCategory = category
                        expanded = false
                        onSelected(category)  // Notify parent composable of the selection
                    },
                    text = { Text(category) }
                )
            }
        }
    }
}