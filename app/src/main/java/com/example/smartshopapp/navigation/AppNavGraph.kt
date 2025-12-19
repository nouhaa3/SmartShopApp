package com.example.smartshopapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.smartshopapp.ui.onboarding.WelcomeScreen
import com.example.smartshopapp.ui.products.AddProductScreen
import com.example.smartshopapp.ui.products.EditProductScreen
import com.example.smartshopapp.ui.products.ProductDetailsScreen
import com.example.smartshopapp.ui.products.ProductListScreen
import com.example.smartshopapp.ui.stats.StatisticsScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect
import com.example.smartshopapp.ui.profile.ProfileScreen
import com.example.smartshopapp.domain.UserViewModel
import com.example.smartshopapp.data.remote.UserRepository
import com.example.smartshopapp.ui.profile.EditProfileScreen
import com.example.smartshopapp.domain.UserViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ------------------ AUTH ------------------
    val authVM = remember { AuthViewModel() }
    val firebaseAuth = FirebaseAuth.getInstance()

    // ------------------ DATA ------------------
    val repository = remember { ProductRepository(context) }

    LaunchedEffect(Unit) {
        repository.startRealtimeSync()
    }


    // ------------------ VIEWMODELS ------------------
    val listVM = remember { ProductListViewModel(repository) }
    val productVM = remember { ProductViewModel(repository) }

    // ------------------ START DESTINATION ------------------
    val startDestination =
        if (firebaseAuth.currentUser != null) "home" else "welcome"

    // ------------------ NAV HOST ------------------
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ================== WELCOME ==================
        composable("welcome") {
            WelcomeScreen(
                onStart = {
                    navController.navigate("onboarding") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        // ================== ONBOARDING ==================
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("register") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onSkip = {
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
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // ================== HOME ==================
        composable("home") {
            HomeScreen(
                onProductsClick = {
                    navController.navigate("product_list")
                },
                onStatsClick = {
                    navController.navigate("statistics")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
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
                    navController.navigate("product_details/${product.id}")
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

        // ================== PRODUCT DETAILS ==================
        composable(
            route = "product_details/{productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.StringType
            })
        ) { entry ->

            val productId = entry.arguments?.getString("productId") ?: ""

            val productsState = listVM.products.collectAsState()
            val product = productsState.value.find { it.id == productId }

            product?.let {
                ProductDetailsScreen(
                    product = it,
                    onBack = { navController.popBackStack() },
                    onEdit = {
                        navController.navigate("edit_product/${it.id}")
                    },
                    onDelete = {
                        listVM.deleteProduct(it.id)
                        navController.popBackStack()
                    }
                )
            }
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

        // ================== PROFILE ==================
            // VIEW
            composable("profile") {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val context = LocalContext.current

                if (firebaseUser != null) {
                    val userVM: UserViewModel = viewModel(
                        factory = UserViewModelFactory(context)
                    )

                    ProfileScreen(
                        uid = firebaseUser.uid,
                        userViewModel = userVM,
                        onEdit = {
                            navController.navigate("edit_profile")
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // EDIT
            composable("edit_profile") {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val context = LocalContext.current

                if (firebaseUser != null) {
                    val userVM: UserViewModel = viewModel(
                        factory = UserViewModelFactory(context)
                    )

                    EditProfileScreen(
                        uid = firebaseUser.uid,
                        userViewModel = userVM,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
    }
}
