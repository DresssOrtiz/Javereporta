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
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.javereporta.ui.screen.PasswordResetResult
import com.example.javereporta.ui.screen.ProfileScreen
import com.example.javereporta.ui.screen.RegisterAttemptResult
import com.example.javereporta.ui.screen.RegisterScreen
import com.example.javereporta.ui.screen.ReportDetailScreen
import com.example.javereporta.ui.screen.ReportSuccessScreen
import com.example.javereporta.ui.screen.ReportsListScreen
import com.example.javereporta.ui.theme.JaveReportaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val firebaseAuth = remember { FirebaseAuth.getInstance() }
            val userNamesByEmail = remember { mutableStateMapOf<String, String>() }
            fun currentFirebaseUserAsDomainUser(): User? {
                val firebaseUser = firebaseAuth.currentUser ?: return null
                val email = firebaseUser.email.orEmpty()
                return User(
                    id = firebaseUser.uid,
                    name = userNamesByEmail[email.lowercase()]
                        ?: firebaseUser.displayName
                        ?: "Usuario",
                    email = email,
                    role = UserRole.USER
                )
            }

            var currentRoute by rememberSaveable {
                mutableStateOf(
                    if (firebaseAuth.currentUser == null) {
                        AppRoutes.LOGIN
                    } else {
                        AppRoutes.HOME
                    }
                )
            }
            var selectedReportId by rememberSaveable { mutableStateOf<String?>(null) }
            var pendingReportBuildingId by rememberSaveable { mutableStateOf<Int?>(null) }
            var nextReportNumber by remember { mutableStateOf(1) }
            var currentUser by remember { mutableStateOf(currentFirebaseUserAsDomainUser()) }
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
                            onLoginAttempt = { email, password, onResult ->
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        currentUser = currentFirebaseUserAsDomainUser()
                                        currentRoute = AppRoutes.HOME
                                        onResult(LoginAttemptResult())
                                    }
                                    .addOnFailureListener { exception ->
                                        onResult(exception.toLoginAttemptResult())
                                    }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.REGISTER -> RegisterScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            onRegisterAttempt = { name, email, password, onResult ->
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { authResult ->
                                        userNamesByEmail[email.lowercase()] = name
                                        val firebaseUser = authResult.user
                                        currentUser = if (firebaseUser == null) {
                                            currentFirebaseUserAsDomainUser()
                                        } else {
                                            User(
                                                id = firebaseUser.uid,
                                                name = name,
                                                email = firebaseUser.email.orEmpty(),
                                                role = UserRole.USER
                                            )
                                        }
                                        currentRoute = AppRoutes.HOME
                                        onResult(RegisterAttemptResult())
                                    }
                                    .addOnFailureListener { exception ->
                                        onResult(exception.toRegisterAttemptResult())
                                    }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        AppRoutes.FORGOT_PASSWORD -> ForgotPasswordScreen(
                            onLoginClick = { currentRoute = AppRoutes.LOGIN },
                            onPasswordResetRequest = { email, onResult ->
                                firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener {
                                        onResult(PasswordResetResult())
                                    }
                                    .addOnFailureListener { exception ->
                                        onResult(exception.toPasswordResetResult())
                                    }
                            },
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
                                    reports.removeAt(reportIndex)
                                }
                                selectedReportId = null
                                currentRoute = AppRoutes.REPORTS_LIST
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
                                    userNamesByEmail[user.email.lowercase()] = newName
                                }
                            },
                            onLogoutClick = {
                                firebaseAuth.signOut()
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

private fun Exception.toLoginAttemptResult(): LoginAttemptResult {
    return when (this) {
        is FirebaseAuthInvalidUserException -> LoginAttemptResult(
            emailError = "No existe una cuenta registrada con este correo."
        )
        is FirebaseAuthInvalidCredentialsException -> LoginAttemptResult(
            passwordError = "La contrasena no coincide."
        )
        else -> LoginAttemptResult(
            passwordError = "No se pudo iniciar sesion. Revisa tus datos."
        )
    }
}

private fun Exception.toRegisterAttemptResult(): RegisterAttemptResult {
    return when (this) {
        is FirebaseAuthUserCollisionException -> RegisterAttemptResult(
            emailError = "Ya existe una cuenta registrada con este correo."
        )
        is FirebaseAuthWeakPasswordException -> RegisterAttemptResult(
            passwordError = "La contrasena no cumple los requisitos de Firebase."
        )
        is FirebaseAuthInvalidCredentialsException -> RegisterAttemptResult(
            emailError = "Firebase rechazo este correo."
        )
        else -> RegisterAttemptResult(
            formError = "No se pudo crear la cuenta. Intenta de nuevo."
        )
    }
}

private fun Exception.toPasswordResetResult(): PasswordResetResult {
    return when (this) {
        is FirebaseAuthInvalidUserException -> PasswordResetResult(
            emailError = "No existe una cuenta registrada con este correo."
        )
        is FirebaseAuthInvalidCredentialsException -> PasswordResetResult(
            emailError = "El correo no es valido para Firebase."
        )
        else -> PasswordResetResult(
            formError = "No se pudo enviar el correo de recuperacion."
        )
    }
}

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
