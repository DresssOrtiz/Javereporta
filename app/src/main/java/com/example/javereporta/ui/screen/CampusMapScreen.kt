package com.example.javereporta.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.javereporta.data.CampusGeoJsonRepository
import com.example.javereporta.domain.model.BuildingCatalog
import com.example.javereporta.domain.model.CampusMapBuilding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun CampusMapScreen(
    onBackHomeClick: () -> Unit,
    onCreateReportForBuilding: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { CampusGeoJsonRepository(context) }
    val mapBuildings = remember { repository.loadBuildings() }
    val campusCenter = remember(mapBuildings) { repository.campusCenter(mapBuildings) }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(campusCenter, 17f)
    }

    var selectedBuilding by remember { mutableStateOf<CampusMapBuilding?>(null) }
    var suggestedBuilding by remember { mutableStateOf<CampusMapBuilding?>(null) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationMessage by remember { mutableStateOf<String?>(null) }
    var locationRequestCount by remember { mutableStateOf(0) }
    var hasLocationPermission by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            locationMessage = "No se concedio permiso de ubicacion."
        } else {
            locationRequestCount++
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(hasLocationPermission, locationRequestCount) {
        if (hasLocationPermission) {
            locationClient.requestCampusLocation(
                onLocationFound = { location ->
                    currentLocation = location
                    val building = repository.recommendedBuildingFor(location, mapBuildings)
                    suggestedBuilding = building
                    locationMessage = if (building == null) {
                        "No encontramos un edificio cercano a tu ubicacion."
                    } else {
                        "Sugerido por tu ubicacion: ${building.name}"
                    }
                },
                onLocationMissing = {
                    locationMessage = "No se pudo obtener tu ubicacion actual."
                }
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapClick = { coordinate ->
                selectedBuilding = repository.buildingAt(coordinate, mapBuildings)
            }
        ) {
            mapBuildings.forEach { building ->
                val isSelected = selectedBuilding?.id == building.id
                val isSuggested = suggestedBuilding?.id == building.id
                Polygon(
                    points = building.polygon,
                    clickable = true,
                    fillColor = when {
                        isSelected -> Color(0x803F7DDB)
                        isSuggested -> Color(0x8050A878)
                        else -> Color(0x402D5B9A)
                    },
                    strokeColor = when {
                        isSelected -> Color(0xFF164C96)
                        isSuggested -> Color(0xFF1D7A42)
                        else -> Color(0xFF2D5B9A)
                    },
                    strokeWidth = if (isSelected || isSuggested) 6f else 3f,
                    onClick = { selectedBuilding = building }
                )
            }

            currentLocation?.let { location ->
                Marker(
                    state = rememberUpdatedMarkerState(position = location),
                    title = "Tu ubicacion"
                )
            }
        }

        CampusMapPanel(
            mapBuildings = mapBuildings,
            selectedBuilding = selectedBuilding,
            suggestedBuilding = suggestedBuilding,
            locationMessage = locationMessage,
            onUseSuggestedBuilding = { selectedBuilding = it },
            onCreateReportForBuilding = onCreateReportForBuilding,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        OutlinedButton(
            onClick = onBackHomeClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
        ) {
            Text(text = "<")
        }
    }
}

@Composable
private fun CampusMapPanel(
    mapBuildings: List<CampusMapBuilding>,
    selectedBuilding: CampusMapBuilding?,
    suggestedBuilding: CampusMapBuilding?,
    locationMessage: String?,
    onUseSuggestedBuilding: (CampusMapBuilding) -> Unit,
    onCreateReportForBuilding: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Mapa del campus",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (mapBuildings.isEmpty()) {
                Text(
                    text = "No se encontraron edificios en campus.geojson.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            locationMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            suggestedBuilding?.let { building ->
                OutlinedButton(
                    onClick = { onUseSuggestedBuilding(building) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Usar sugerido: ${building.name}")
                }
            }
            selectedBuilding?.let { building ->
                val catalogBuilding = BuildingCatalog.buildings.firstOrNull { it.id == building.id }
                Text(
                    text = building.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (catalogBuilding == null) {
                    Text(
                        text = "Este edificio no existe en el catalogo local de edificios.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Button(
                        onClick = { onCreateReportForBuilding(catalogBuilding.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Crear reporte aqui")
                    }
                }
            } ?: Text(
                text = "Toca un edificio para seleccionarlo.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun android.content.Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
private fun com.google.android.gms.location.FusedLocationProviderClient.requestCampusLocation(
    onLocationFound: (LatLng) -> Unit,
    onLocationMissing: () -> Unit
) {
    getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location == null) {
                onLocationMissing()
            } else {
                onLocationFound(LatLng(location.latitude, location.longitude))
            }
        }
        .addOnFailureListener { onLocationMissing() }
}
