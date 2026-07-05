package com.example.javereporta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.javereporta.navigation.AppRoutes
import com.example.javereporta.ui.screen.ConstructionScreen
import com.example.javereporta.ui.screen.ForgotPasswordScreen
import com.example.javereporta.ui.screen.HomeScreen
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in bottomNavigationRoutes) {
                            JaveReportaBottomBar(
                                currentRoute = currentRoute,
                                onRouteSelected = { currentRoute = it }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentRoute) {
                        AppRoutes.LOGIN -> LoginScreen(
                            onRegisterClick = { currentRoute = AppRoutes.REGISTER },
                            onForgotPasswordClick = { currentRoute = AppRoutes.FORGOT_PASSWORD },
                            onLoginSuccess = { currentRoute = AppRoutes.HOME },
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

                        AppRoutes.HOME -> HomeScreen(
                            onOpenCampusMap = { currentRoute = AppRoutes.CAMPUS_MAP },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REPORTS_LIST,
                        AppRoutes.PROFILE,
                        AppRoutes.CAMPUS_MAP -> ConstructionScreen(
                            onHomeClick = { currentRoute = AppRoutes.HOME },
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

private val bottomNavigationRoutes = setOf(
    AppRoutes.HOME,
    AppRoutes.REPORTS_LIST,
    AppRoutes.PROFILE,
    AppRoutes.CAMPUS_MAP
)

@Composable
private fun JaveReportaBottomBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == AppRoutes.HOME,
            onClick = { onRouteSelected(AppRoutes.HOME) },
            icon = { Text(text = "I") },
            label = { Text(text = "Inicio") }
        )
        NavigationBarItem(
            selected = currentRoute == AppRoutes.REPORTS_LIST,
            onClick = { onRouteSelected(AppRoutes.REPORTS_LIST) },
            icon = { Text(text = "R") },
            label = { Text(text = "Mis reportes") }
        )
        NavigationBarItem(
            selected = currentRoute == AppRoutes.PROFILE,
            onClick = { onRouteSelected(AppRoutes.PROFILE) },
            icon = { Text(text = "P") },
            label = { Text(text = "Mi perfil") }
        )
    }
}
