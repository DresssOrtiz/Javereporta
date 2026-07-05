package com.example.javereporta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.javereporta.navigation.AppRoutes
import com.example.javereporta.ui.screen.ForgotPasswordScreen
import com.example.javereporta.ui.screen.InitialScreen
import com.example.javereporta.ui.screen.LoginScreen
import com.example.javereporta.ui.screen.RegisterScreen
import com.example.javereporta.ui.theme.JaveReportaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentRoute by rememberSaveable { mutableStateOf(AppRoutes.SPLASH) }

            JaveReportaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentRoute) {
                        AppRoutes.LOGIN -> LoginScreen(
                            onRegisterClick = { currentRoute = AppRoutes.REGISTER },
                            onForgotPasswordClick = { currentRoute = AppRoutes.FORGOT_PASSWORD },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REGISTER -> RegisterScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.FORGOT_PASSWORD -> ForgotPasswordScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            modifier = Modifier.padding(innerPadding)
                        )

                        else -> InitialScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            onRegisterClick = { currentRoute = AppRoutes.REGISTER },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
