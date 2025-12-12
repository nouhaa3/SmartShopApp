package com.example.smartshopapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartshopapp.auth.AuthViewModel
import com.example.smartshopapp.auth.LoginScreen
import com.example.smartshopapp.auth.RegisterScreen
import com.example.smartshopapp.data.remote.ProductRepository
import com.example.smartshopapp.domain.ProductListViewModel
import com.example.smartshopapp.domain.ProductViewModel
import com.example.smartshopapp.ui.home.HomeScreen
import com.example.smartshopapp.ui.products.AddProductScreen
import com.example.smartshopapp.ui.products.EditProductScreen
import com.example.smartshopapp.ui.products.ProductListScreen
import com.example.smartshopapp.ui.stats.StatisticsScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    // ------------------ AUTH VIEWMODEL ------------------
    val authVM = remember { AuthViewModel() }

    // ------------------ PRODUCT REPO + VMs ------------------
    val repository = remember { ProductRepository() }
    val listVM = remember { ProductListViewModel(repository) }
    val productVM = remember { ProductViewModel(repository) }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // ------------------ LOGIN ------------------
        composable("login") {
            LoginScreen(
                viewModel = authVM,
                onLoginSuccess = { navController.navigate("home") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        // ------------------ REGISTER ------------------
        composable("register") {
            RegisterScreen(
                viewModel = authVM,
                onRegisterSuccess = { navController.navigate("home") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // ------------------ HOME ------------------
        composable("home") {
            HomeScreen(
                onProductsClick = { navController.navigate("product_list") },
                onStatsClick = { navController.navigate("statistics") }
            )
        }

        // ------------------ PRODUCT LIST ------------------
        composable("product_list") {
            ProductListScreen(
                viewModel = listVM,
                onAddProduct = { navController.navigate("add_product") },
                onEditProduct = { product ->
                    navController.navigate("edit_product/${product.id}")
                }
            )
        }

        // ------------------ ADD PRODUCT ------------------
        composable("add_product") {
            AddProductScreen(
                viewModel = productVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ------------------ EDIT PRODUCT ------------------
        composable(
            "edit_product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { entry ->
            val productId = entry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                viewModel = productVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ------------------ STATISTICS ------------------
        composable("statistics") {
            StatisticsScreen(repository)
        }
    }
}
