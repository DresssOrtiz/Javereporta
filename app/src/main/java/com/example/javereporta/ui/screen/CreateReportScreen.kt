package com.example.javereporta.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.domain.model.BuildingCatalog
import com.example.javereporta.domain.model.BuildingFloor
import com.example.javereporta.domain.model.BuildingInfo
import com.example.javereporta.domain.model.CreateReportDraft
import com.example.javereporta.domain.model.ReportCategory
import com.example.javereporta.ui.theme.JaveReportaTheme

@Composable
fun CreateReportScreen(
    onBackHomeClick: () -> Unit,
    onReportPrepared: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBuilding by remember { mutableStateOf<BuildingInfo?>(null) }
    var selectedFloor by remember { mutableStateOf<BuildingFloor?>(null) }
    var selectedZone by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<ReportCategory?>(null) }
    var description by remember { mutableStateOf("") }
    var buildingError by remember { mutableStateOf<String?>(null) }
    var floorError by remember { mutableStateOf<String?>(null) }
    var zoneError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var preparedDraft by remember { mutableStateOf<CreateReportDraft?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Nuevo reporte",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Describe la novedad para preparar el reporte.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SelectionDropdown(
            label = "Edificio o lugar",
            selectedText = selectedBuilding?.let { "${it.id} - ${it.name}" },
            placeholder = "Selecciona un edificio",
            options = BuildingCatalog.buildings,
            optionText = { "${it.id} - ${it.name}" },
            onOptionSelected = {
                selectedBuilding = it
                selectedFloor = null
                selectedZone = null
                buildingError = null
                floorError = null
                zoneError = null
                preparedDraft = null
            }
        )
        buildingError?.let { FieldError(text = it) }
        SelectionDropdown(
            label = "Piso",
            selectedText = selectedFloor?.name,
            placeholder = "Selecciona un piso",
            options = selectedBuilding?.floors.orEmpty(),
            optionText = { it.name },
            enabled = selectedBuilding != null,
            onOptionSelected = {
                selectedFloor = it
                selectedZone = null
                floorError = null
                zoneError = null
                preparedDraft = null
            }
        )
        floorError?.let { FieldError(text = it) }
        SelectionDropdown(
            label = "Zona o espacio",
            selectedText = selectedZone,
            placeholder = "Selecciona una zona",
            options = selectedFloor?.zones.orEmpty(),
            optionText = { it },
            enabled = selectedFloor != null,
            onOptionSelected = {
                selectedZone = it
                zoneError = null
                preparedDraft = null
            }
        )
        zoneError?.let { FieldError(text = it) }
        Text(
            text = "Categoría del problema",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        CategorySelector(
            selectedCategory = selectedCategory,
            onCategorySelected = {
                selectedCategory = it
                categoryError = null
                preparedDraft = null
            }
        )
        categoryError?.let { FieldError(text = it) }
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                descriptionError = null
                preparedDraft = null
            },
            label = { Text(text = "Descripción") },
            isError = descriptionError != null,
            supportingText = { descriptionError?.let { Text(text = it) } },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                buildingError = if (selectedBuilding == null) {
                    "Selecciona el edificio o lugar."
                } else {
                    null
                }
                floorError = if (selectedFloor == null) {
                    "Selecciona el piso."
                } else {
                    null
                }
                zoneError = if (selectedZone == null) {
                    "Selecciona la zona o espacio."
                } else {
                    null
                }
                categoryError = if (selectedCategory == null) {
                    "Selecciona una categoría."
                } else {
                    null
                }
                descriptionError = if (description.trim().length < 10) {
                    "La descripción debe tener al menos 10 caracteres."
                } else {
                    null
                }

                val building = selectedBuilding
                val floor = selectedFloor
                val zone = selectedZone
                val category = selectedCategory
                if (
                    building != null &&
                    floor != null &&
                    zone != null &&
                    category != null &&
                    descriptionError == null
                ) {
                    preparedDraft = CreateReportDraft(
                        buildingId = building.id,
                        buildingName = building.name,
                        floorName = floor.name,
                        zoneName = zone,
                        category = category,
                        description = description.trim()
                    )
                    onReportPrepared()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enviar reporte")
        }
        TextButton(
            onClick = onBackHomeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver a Inicio")
        }
    }
}

@Composable
private fun <T> SelectionDropdown(
    label: String,
    selectedText: String?,
    placeholder: String,
    options: List<T>,
    optionText: (T) -> String,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selectedText ?: placeholder)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = optionText(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldError(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun CategorySelector(
    selectedCategory: ReportCategory?,
    onCategorySelected: (ReportCategory) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReportCategory.entries.forEach { category ->
            val selected = selectedCategory == category
            val label = category.label()
            if (selected) {
                Button(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = label)
                }
            } else {
                OutlinedButton(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = label)
                }
            }
        }
    }
}

private fun ReportCategory.label(): String {
    return when (this) {
        ReportCategory.ROADS -> "Vías y accesos"
        ReportCategory.LIGHTING -> "Iluminación"
        ReportCategory.WATER -> "Agua"
        ReportCategory.WASTE -> "Residuos"
        ReportCategory.SECURITY -> "Seguridad"
        ReportCategory.OTHER -> "Otro"
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateReportScreenPreview() {
    JaveReportaTheme {
        CreateReportScreen(
            onBackHomeClick = {},
            onReportPrepared = {}
        )
    }
}
