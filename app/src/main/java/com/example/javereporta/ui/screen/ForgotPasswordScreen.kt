package com.example.javereporta.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.ui.theme.JaveReportaTheme

@Composable
fun ForgotPasswordScreen(
    onLoginClick: () -> Unit,
    onPasswordResetRequest: (
        email: String,
        onResult: (PasswordResetResult) -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                successMessage = null
            },
            label = { Text(text = "Email") },
            isError = emailError != null,
            supportingText = { emailError?.let { Text(text = it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                emailError = AuthValidation.validateEmail(email)
                formError = null
                if (emailError == null) {
                    isLoading = true
                    successMessage = null
                    onPasswordResetRequest(email.trim()) { resetResult ->
                        isLoading = false
                        emailError = resetResult.emailError
                        formError = resetResult.formError
                        successMessage = if (resetResult.isSuccess) {
                            "Solicitud de recuperacion enviada."
                        } else {
                            null
                        }
                    }
                } else {
                    successMessage = null
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLoading) "Enviando..." else "Solicitar recuperación")
        }
        formError?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
        successMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onLoginClick) {
            Text(text = "Volver a login")
        }
    }
}

data class PasswordResetResult(
    val emailError: String? = null,
    val formError: String? = null
) {
    val isSuccess: Boolean
        get() = emailError == null && formError == null
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    JaveReportaTheme {
        ForgotPasswordScreen(
            onLoginClick = {},
            onPasswordResetRequest = { _, onResult -> onResult(PasswordResetResult()) }
        )
    }
}
