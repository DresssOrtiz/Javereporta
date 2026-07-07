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
import com.example.javereporta.domain.model.Report
import com.example.javereporta.domain.model.ReportCategory
import com.example.javereporta.domain.model.ReportStatus
import com.example.javereporta.ui.theme.JaveReportaTheme

@Composable
fun EditReportScreen(
    report: Report?,
    onBackClick: () -> Unit,
    onSaveReport: (
        reportId: String,
        buildingId: Int,
        buildingName: String,
        floorName: String,
        zoneName: String,
        description: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    if (report == null) {
        MissingReportContent(
            onBackClick = onBackClick,
            modifier = modifier
        )
        return
    }

    if (report.status != ReportStatus.ABIERTO || report.hasBeenEdited) {
        EditUnavailableContent(
            onBackClick = onBackClick,
            modifier = modifier
        )
        return
    }

    val initialBuilding = remember(report.id) {
        BuildingCatalog.buildings.firstOrNull { it.id == report.buildingId }
    }
    val initialFloor = remember(report.id, initialBuilding) {
        initialBuilding?.floors?.firstOrNull { it.name == report.floorName }
    }
    var selectedBuilding by remember(report.id) { mutableStateOf(initialBuilding) }
    var selectedFloor by remember(report.id) { mutableStateOf(initialFloor) }
    var selectedZone by remember(report.id) { mutableStateOf(report.zoneName) }
    var description by remember(report.id) { mutableStateOf(report.description) }
    var buildingError by remember { mutableStateOf<String?>(null) }
    var floorError by remember { mutableStateOf<String?>(null) }
    var zoneError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Editar reporte",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Puedes cambiar la ubicacion y la descripcion una sola vez.",
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
                selectedZone = ""
                buildingError = null
                floorError = null
                zoneError = null
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
                selectedZone = ""
                floorError = null
                zoneError = null
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
            }
        )
        zoneError?.let { FieldError(text = it) }
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                descriptionError = null
            },
            label = { Text(text = "Descripcion") },
            isError = descriptionError != null,
            supportingText = { descriptionError?.let { Text(text = it) } },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                val building = selectedBuilding
                val floor = selectedFloor
                val trimmedZone = selectedZone.trim()
                val trimmedDescription = description.trim()
                buildingError = if (building == null) {
                    "Selecciona el edificio o lugar."
                } else {
                    null
                }
                floorError = if (floor == null) {
                    "Selecciona el piso."
                } else {
                    null
                }
                zoneError = if (trimmedZone.isEmpty()) {
                    "Selecciona la zona o espacio."
                } else {
                    null
                }
                descriptionError = if (trimmedDescription.length < 10) {
                    "La descripcion debe tener al menos 10 caracteres."
                } else {
                    null
                }

                if (
                    building != null &&
                    floor != null &&
                    zoneError == null &&
                    descriptionError == null
                ) {
                    onSaveReport(
                        report.id,
                        building.id,
                        building.name,
                        floor.name,
                        trimmedZone,
                        trimmedDescription
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar cambios")
        }
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver")
        }
    }
}

@Composable
private fun EditUnavailableContent(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Este reporte ya no se puede editar.")
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Volver")
        }
    }
}

@Composable
private fun MissingReportContent(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "No se encontro el reporte.")
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Volver")
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

@Preview(showBackground = true)
@Composable
private fun EditReportScreenPreview() {
    JaveReportaTheme {
        EditReportScreen(
            report = Report(
                id = "local-1",
                buildingId = 0,
                buildingName = "Biblioteca Alfonso Barrero Cabal",
                floorName = "Piso 1",
                zoneName = "Entrada",
                category = ReportCategory.OTHER,
                description = "Puerta principal con problema reportado.",
                status = ReportStatus.ABIERTO,
                createdAtMillis = 0L
            ),
            onBackClick = {},
            onSaveReport = { _, _, _, _, _, _ -> }
        )
    }
}
