package com.example.javereporta.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.javereporta.ui.theme.JaveReportaTheme

private val CampusBlue = Color(0xFF0B2D5B)
private val CampusLightBlue = Color(0xFFEAF1FA)
private val CampusSoftBlue = Color(0xFFF5F8FC)

@Composable
fun HomeScreen(
    onOpenCampusMap: () -> Unit,
    onCreateReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CampusSoftBlue)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "JaveReporta",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = CampusBlue
        )
        Text(
            // Aqui se mostrara el nombre del usuario autenticado cuando exista persistencia.
            text = "Hola",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Bienvenido de nuevo. Reporta novedades del campus en segundos.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        CampusLocationCard()
        QuickRoutesCard(
            onOpenCampusMap = onOpenCampusMap,
            onCreateReportClick = onCreateReportClick
        )
        CampusMapCard()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CampusLocationCard() {
    HomeCard {
        Text(
            text = "Ubicación del campus",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Campus principal",
            style = MaterialTheme.typography.bodyLarge,
            color = CampusBlue,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Selecciona o confirma la zona donde ocurre la novedad.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickRoutesCard(
    onOpenCampusMap: () -> Unit,
    onCreateReportClick: () -> Unit
) {
    HomeCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rutas rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Accesos preparados para reportar y ubicar novedades.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onCreateReportClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Reportar un problema")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onOpenCampusMap,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ver mapa del campus")
        }
    }
}

@Composable
private fun CampusMapCard() {
    HomeCard {
        Text(
            text = "Mapa del campus",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            color = CampusLightBlue,
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Espacio reservado para el mapa del campus",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CampusBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun HomeCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    JaveReportaTheme {
        HomeScreen(
            onOpenCampusMap = {},
            onCreateReportClick = {}
        )
    }
}
