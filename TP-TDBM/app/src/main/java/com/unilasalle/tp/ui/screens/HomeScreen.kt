package com.unilasalle.tp.ui.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.network.ApiService
import com.unilasalle.tp.services.network.datas.Product
import com.unilasalle.tp.ui.components.CategoryFilter
import com.unilasalle.tp.ui.components.ProductCard
import com.unilasalle.tp.ui.components.ProductDetailModal
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(cartItemController: CartItemController) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(cartItemController))[CartViewModel::class.java]
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()

    var products by remember { mutableStateOf(listOf<Product>()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            products = apiService.getProducts()
        } catch (e: Exception) {
            errorMessage = "Failed to load products"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Category Filter
        CategoryFilter { category ->
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
                try {
                    products = if (category == "All") {
                        apiService.getProducts()
                    } else {
                        apiService.getProductsByCategory(category)
                    }
                } catch (e: Exception) {
                    errorMessage = "Failed to load products for this category"
                } finally {
                    isLoading = false
                }
            }
        }

        // Show Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Show Error Message
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(16.dp))
        }

        // Display Products
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product, onClick = { selectedProduct = product })
            }
        }

        // Show Product Detail Modal
        selectedProduct?.let { product ->
            ProductDetailModal(product = product, onDismiss = { selectedProduct = null }, cartViewModel = cartViewModel)
        }
    }
}
