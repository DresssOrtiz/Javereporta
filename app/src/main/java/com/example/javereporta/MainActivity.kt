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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.javereporta.domain.model.Report
import com.example.javereporta.domain.model.ReportStatus
import com.example.javereporta.domain.model.User
import com.example.javereporta.domain.model.UserRole
import com.example.javereporta.navigation.AppRoutes
import com.example.javereporta.ui.screen.CampusMapScreen
import com.example.javereporta.ui.screen.CreateReportScreen
import com.example.javereporta.ui.screen.EditReportScreen
import com.example.javereporta.ui.screen.ForgotPasswordScreen
import com.example.javereporta.ui.screen.HomeScreen
import com.example.javereporta.ui.screen.InitialScreen
import com.example.javereporta.ui.screen.LoginAttemptResult
import com.example.javereporta.ui.screen.LoginScreen
import com.example.javereporta.ui.screen.ProfileScreen
import com.example.javereporta.ui.screen.RegisterScreen
import com.example.javereporta.ui.screen.ReportDetailScreen
import com.example.javereporta.ui.screen.ReportSuccessScreen
import com.example.javereporta.ui.screen.ReportsListScreen
import com.example.javereporta.ui.theme.JaveReportaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentRoute by rememberSaveable { mutableStateOf(AppRoutes.SPLASH) }
            var selectedReportId by rememberSaveable { mutableStateOf<String?>(null) }
            var pendingReportBuildingId by rememberSaveable { mutableStateOf<Int?>(null) }
            var nextReportNumber by remember { mutableStateOf(1) }
            var nextUserNumber by remember { mutableStateOf(1) }
            var currentUser by remember { mutableStateOf<User?>(null) }
            val reports = remember { mutableStateListOf<Report>() }
            val registeredUsers = remember { mutableStateListOf<User>() }

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
                            onLoginAttempt = { email, password ->
                                val user = registeredUsers.firstOrNull {
                                    it.email.equals(email, ignoreCase = true)
                                }
                                when {
                                    user == null -> LoginAttemptResult(
                                        emailError = "No existe una cuenta registrada con este correo."
                                    )
                                    user.password != password -> LoginAttemptResult(
                                        passwordError = "La contraseña no coincide."
                                    )
                                    else -> {
                                        currentUser = user
                                        currentRoute = AppRoutes.HOME
                                        LoginAttemptResult()
                                    }
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REGISTER -> RegisterScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            onRegisterSuccess = { name, email, password ->
                                val user = User(
                                    id = "local-user-${nextUserNumber++}",
                                    name = name,
                                    email = email,
                                    password = password,
                                    role = UserRole.USER
                                )
                                registeredUsers.add(user)
                                currentUser = user
                                currentRoute = AppRoutes.HOME
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.FORGOT_PASSWORD -> ForgotPasswordScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.HOME -> HomeScreen(
                            userName = currentUser?.name.orEmpty(),
                            onOpenCampusMap = { currentRoute = AppRoutes.CAMPUS_MAP },
                            onCreateReportClick = {
                                pendingReportBuildingId = null
                                currentRoute = AppRoutes.CREATE_REPORT
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.CREATE_REPORT -> CreateReportScreen(
                            onBackHomeClick = {
                                pendingReportBuildingId = null
                                currentRoute = AppRoutes.HOME
                            },
                            onReportPrepared = { draft ->
                                reports.add(
                                    index = 0,
                                    element = Report(
                                        id = "local-${nextReportNumber++}",
                                        buildingId = draft.buildingId,
                                        buildingName = draft.buildingName,
                                        floorName = draft.floorName,
                                        zoneName = draft.zoneName,
                                        category = draft.category,
                                        description = draft.description,
                                        status = ReportStatus.ABIERTO,
                                        createdAtMillis = System.currentTimeMillis()
                                    )
                                )
                                pendingReportBuildingId = null
                                currentRoute = AppRoutes.REPORT_SUCCESS
                            },
                            initialBuildingId = pendingReportBuildingId,
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REPORT_SUCCESS -> ReportSuccessScreen(
                            onHomeClick = { currentRoute = AppRoutes.HOME },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REPORTS_LIST -> ReportsListScreen(
                            reports = reports,
                            onReportClick = {
                                selectedReportId = it
                                currentRoute = AppRoutes.REPORT_DETAIL
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REPORT_DETAIL -> ReportDetailScreen(
                            report = reports.firstOrNull { it.id == selectedReportId },
                            onBackClick = { currentRoute = AppRoutes.REPORTS_LIST },
                            onEditReport = {
                                selectedReportId = it
                                currentRoute = AppRoutes.EDIT_REPORT
                            },
                            onCancelReport = { reportId ->
                                val reportIndex = reports.indexOfFirst { it.id == reportId }
                                if (reportIndex >= 0) {
                                    reports[reportIndex] = reports[reportIndex].copy(
                                        status = ReportStatus.CANCELADO
                                    )
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.EDIT_REPORT -> EditReportScreen(
                            report = reports.firstOrNull { it.id == selectedReportId },
                            onBackClick = { currentRoute = AppRoutes.REPORT_DETAIL },
                            onSaveReport = {
                                reportId,
                                buildingId,
                                buildingName,
                                floorName,
                                zoneName,
                                description ->
                                val reportIndex = reports.indexOfFirst { it.id == reportId }
                                if (reportIndex >= 0) {
                                    reports[reportIndex] = reports[reportIndex].copy(
                                        buildingId = buildingId,
                                        buildingName = buildingName,
                                        floorName = floorName,
                                        zoneName = zoneName,
                                        description = description,
                                        hasBeenEdited = true
                                    )
                                }
                                selectedReportId = reportId
                                currentRoute = AppRoutes.REPORT_DETAIL
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.PROFILE -> ProfileScreen(
                            name = currentUser?.name.orEmpty(),
                            email = currentUser?.email.orEmpty(),
                            role = currentUser?.role ?: UserRole.USER,
                            sessionStatus = if (currentUser == null) "Sin sesion" else "Activa",
                            onNameChange = { newName ->
                                val user = currentUser
                                if (user != null) {
                                    val updatedUser = user.copy(name = newName)
                                    currentUser = updatedUser
                                    val userIndex = registeredUsers.indexOfFirst { it.id == user.id }
                                    if (userIndex >= 0) {
                                        registeredUsers[userIndex] = updatedUser
                                    }
                                }
                            },
                            onLogoutClick = {
                                currentUser = null
                                currentRoute = AppRoutes.LOGIN
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.CAMPUS_MAP -> CampusMapScreen(
                            onCreateReportForBuilding = { buildingId ->
                                pendingReportBuildingId = buildingId
                                currentRoute = AppRoutes.CREATE_REPORT
                            },
                            onBackHomeClick = { currentRoute = AppRoutes.HOME },
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
