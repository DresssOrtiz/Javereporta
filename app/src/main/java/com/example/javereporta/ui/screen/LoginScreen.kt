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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.ui.theme.JaveReportaTheme

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión",
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
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                successMessage = null
            },
            label = { Text(text = "Contraseña") },
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(text = it) } },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                emailError = AuthValidation.validateEmail(email)
                passwordError = AuthValidation.validatePassword(password)
                if (emailError == null && passwordError == null) {
                    successMessage = "Inicio de sesión válido."
                    onLoginSuccess()
                } else {
                    successMessage = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Iniciar sesión")
        }
        successMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onRegisterClick) {
            Text(text = "Ir a registro")
        }
        TextButton(onClick = onForgotPasswordClick) {
            Text(text = "Recuperar contraseña")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    JaveReportaTheme {
        LoginScreen(
            onRegisterClick = {},
            onForgotPasswordClick = {},
            onLoginSuccess = {}
        )
    }
}
