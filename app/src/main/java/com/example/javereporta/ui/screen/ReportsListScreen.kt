package com.example.javereporta.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.domain.model.Report
import com.example.javereporta.domain.model.ReportCategory
import com.example.javereporta.domain.model.ReportStatus
import com.example.javereporta.ui.theme.JaveReportaTheme

@Composable
fun ReportsListScreen(
    reports: List<Report>,
    onReportClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Mis reportes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (reports.isEmpty()) {
            Text(
                text = "Aún no has creado reportes.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports, key = { it.id }) { report ->
                    ReportListItem(
                        report = report,
                        onClick = { onReportClick(report.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportListItem(
    report: Report,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = report.category.label(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Estado: ${report.status.label()}")
            Text(text = "Edificio: ${report.buildingName}")
            Text(text = "Piso: ${report.floorName}")
            Text(text = "Zona: ${report.zoneName}")
            Text(
                text = "Descripción: ${report.description}",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportsListScreenPreview() {
    JaveReportaTheme {
        ReportsListScreen(
            reports = listOf(
                Report(
                    id = "local-1",
                    buildingId = 0,
                    buildingName = "Biblioteca Alfonso Barrero Cabal",
                    floorName = "Piso 1",
                    zoneName = "Entrada",
                    category = ReportCategory.OTHER,
                    description = "Puerta principal con problema reportado.",
                    status = ReportStatus.ABIERTO,
                    createdAtMillis = 0L
                )
            ),
            onReportClick = {}
        )
    }
}
