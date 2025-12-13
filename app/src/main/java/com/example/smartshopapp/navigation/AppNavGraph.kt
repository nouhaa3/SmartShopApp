package com.example.smartshopapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.example.smartshopapp.ui.onboarding.OnboardingScreen
import com.example.smartshopapp.ui.products.AddProductScreen
import com.example.smartshopapp.ui.products.EditProductScreen
import com.example.smartshopapp.ui.products.ProductListScreen
import com.example.smartshopapp.ui.stats.StatisticsScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ------------------ AUTH ------------------
    val authVM = remember { AuthViewModel() }
    val firebaseAuth = FirebaseAuth.getInstance()

    // ------------------ DATA ------------------
    val repository = remember { ProductRepository(context) }

    // ------------------ VIEWMODELS ------------------
    val listVM = remember { ProductListViewModel(repository) }
    val productVM = remember { ProductViewModel(repository) }

    // ------------------ START DESTINATION ------------------
    val startDestination =
        if (firebaseAuth.currentUser != null) "home" else "onboarding"

    // ------------------ NAV HOST ------------------
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ================== ONBOARDING ==================
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate("register") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // ================== LOGIN ==================
        composable("login") {
            LoginScreen(
                viewModel = authVM,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // ================== REGISTER ==================
        composable("register") {
            RegisterScreen(
                viewModel = authVM,
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // ================== HOME ==================
        composable("home") {
            HomeScreen(
                onProductsClick = { navController.navigate("product_list") },
                onStatsClick = { navController.navigate("statistics") },
                onLogout = {
                    authVM.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // ================== PRODUCT LIST ==================
        composable("product_list") {
            ProductListScreen(
                viewModel = listVM,
                onAddProduct = { navController.navigate("add_product") },
                onEditProduct = { product ->
                    navController.navigate("edit_product/${product.id}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ================== ADD PRODUCT ==================
        composable("add_product") {
            AddProductScreen(
                viewModel = productVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ================== EDIT PRODUCT ==================
        composable(
            route = "edit_product/{productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.StringType
            })
        ) { entry ->
            val productId = entry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                viewModel = productVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ================== STATISTICS ==================
        composable("statistics") {
            StatisticsScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
