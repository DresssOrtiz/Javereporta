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
import com.example.javereporta.navigation.AppRoutes
import com.example.javereporta.ui.screen.ConstructionScreen
import com.example.javereporta.ui.screen.CreateReportScreen
import com.example.javereporta.ui.screen.EditReportScreen
import com.example.javereporta.ui.screen.ForgotPasswordScreen
import com.example.javereporta.ui.screen.HomeScreen
import com.example.javereporta.ui.screen.InitialScreen
import com.example.javereporta.ui.screen.LoginScreen
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
            var nextReportNumber by remember { mutableStateOf(1) }
            val reports = remember { mutableStateListOf<Report>() }

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
                            onCreateReportClick = { currentRoute = AppRoutes.CREATE_REPORT },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.CREATE_REPORT -> CreateReportScreen(
                            onBackHomeClick = { currentRoute = AppRoutes.HOME },
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
                                currentRoute = AppRoutes.REPORT_SUCCESS
                            },
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
