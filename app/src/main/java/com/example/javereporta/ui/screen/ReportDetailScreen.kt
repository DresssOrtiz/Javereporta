package com.example.javereporta.ui.screen

import android.widget.Toast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.domain.model.Report
import com.example.javereporta.domain.model.ReportCategory
import com.example.javereporta.domain.model.ReportStatus
import com.example.javereporta.ui.theme.JaveReportaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportDetailScreen(
    report: Report?,
    onBackClick: () -> Unit,
    onEditReport: (String) -> Unit,
    onCancelReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCancelConfirmation by remember { mutableStateOf(false) }

    if (report == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No se encontró el reporte.")
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Volver")
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = report.category.label(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        DetailLine(label = "Estado", value = report.status.label())
        DetailLine(label = "Edificio", value = "${report.buildingId} - ${report.buildingName}")
        DetailLine(label = "Piso", value = report.floorName)
        DetailLine(label = "Zona/espacio", value = report.zoneName)
        DetailLine(label = "Fecha/hora", value = report.createdAtMillis.formatDateTime())
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Descripción completa",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = report.description,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        if (report.status == ReportStatus.ABIERTO) {
            Button(
                onClick = {
                    if (report.hasBeenEdited) {
                        Toast.makeText(
                            context,
                            "Un reporte se puede editar una sola vez",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onEditReport(report.id)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Editar reporte")
            }
            OutlinedButton(
                onClick = { showCancelConfirmation = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancelar reporte")
            }
        }
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver")
        }
    }

    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            title = { Text(text = "Cancelar reporte") },
            text = { Text(text = "¿Seguro que quieres cancelar este reporte?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelConfirmation = false
                        onCancelReport(report.id)
                    }
                ) {
                    Text(text = "Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text(text = "No")
                }
            }
        )
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String
) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge
    )
}

private fun Long.formatDateTime(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(this))
}

@Preview(showBackground = true)
@Composable
private fun ReportDetailScreenPreview() {
    JaveReportaTheme {
        ReportDetailScreen(
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
            onEditReport = {},
            onCancelReport = {}
        )
    }
}
