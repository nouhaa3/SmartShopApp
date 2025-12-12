package com.example.smartshopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smartshopapp.navigation.AppNavGraph
import com.example.smartshopapp.ui.theme.SmartShopAppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase once at app start
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()

        setContent {
            SmartShopAppTheme {
                // Launch the navigation graph (Splash → Login → Register → Home)
                AppNavGraph()
            }
        }
    }
}
