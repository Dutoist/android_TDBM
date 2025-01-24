package com.unilasalle.tp.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.database.entities.CartItem
import com.unilasalle.tp.services.network.ApiService
import com.unilasalle.tp.services.network.datas.Product
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun CartScreen(cartItemController: CartItemController) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(cartItemController))[CartViewModel::class.java]

    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()

    var totalAmount by remember { mutableStateOf(0.0) }

    LaunchedEffect(cartItems) {
        val total = cartItems.sumOf { cartItem ->
            val product = apiService.getProduct(cartItem.productId)
            product.price.times(cartItem.quantity).toDouble()
        }
        totalAmount = total
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
    ) {
        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (cartItems.isEmpty()) {
            Text(text = "Your cart is empty.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(cartItems) { cartItem ->
                    CartItemRow(cartItem = cartItem, cartViewModel = cartViewModel)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Total: \$${String.format("%.2f", totalAmount)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = { /* Handle checkout or next action */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Proceed to Checkout", color = Color.White)
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, cartViewModel: CartViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()
    var product by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            product = apiService.getProduct(cartItem.productId)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        product?.let {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(text = it.title, style = MaterialTheme.typography.bodyMedium)
                Text(text = "\$${it.price}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "Quantity: ${cartItem.quantity}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        cartViewModel.removeFromCart(cartItem)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove from cart",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        } ?: Text(text = "Loading product...", style = MaterialTheme.typography.bodyMedium)
    }
}
