package com.example.javereporta.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.domain.model.UserRole
import com.example.javereporta.ui.theme.JaveReportaTheme

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    role: UserRole,
    sessionStatus: String,
    onNameChange: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameDraft by remember(name) { mutableStateOf(name) }
    var nameError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileLine(label = "Nombre", value = name)
                ProfileLine(label = "Correo", value = email)
                ProfileLine(label = "Rol", value = role.label())
                ProfileLine(label = "Estado de sesion", value = sessionStatus)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Editar nombre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = nameDraft,
                    onValueChange = {
                        nameDraft = it
                        nameError = null
                    },
                    label = { Text(text = "Nombre") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(text = it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = {
                        val trimmedName = nameDraft.trim()
                        nameError = if (trimmedName.isEmpty()) {
                            "El nombre no puede estar vacio."
                        } else {
                            null
                        }
                        if (nameError == null) {
                            onNameChange(trimmedName)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Editar nombre")
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cerrar sesion")
        }
    }
}

@Composable
private fun ProfileLine(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun UserRole.label(): String {
    return when (this) {
        UserRole.USER -> "Usuario"
        UserRole.ADMIN -> "Administrador"
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    JaveReportaTheme {
        ProfileScreen(
            name = "Usuario JaveReporta",
            email = "usuario@javeriana.edu.co",
            role = UserRole.USER,
            sessionStatus = "Activa",
            onNameChange = {},
            onLogoutClick = {}
        )
    }
}
