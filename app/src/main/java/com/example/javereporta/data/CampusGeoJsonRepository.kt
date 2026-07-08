package com.example.javereporta.data

import android.content.Context
import android.location.Location
import com.example.javereporta.domain.model.BuildingCatalog
import com.example.javereporta.domain.model.CampusMapBuilding
import com.example.javereporta.domain.model.CampusMapBuilding.Companion.DEFAULT_CAMPUS_CENTER
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

class CampusGeoJsonRepository(
    private val context: Context
) {
    fun loadBuildings(): List<CampusMapBuilding> {
        val geoJsonBuildings = runCatching {
            val geoJson = context.assets.open(CAMPUS_GEOJSON_FILE)
                .bufferedReader()
                .use { it.readText() }
            parseFeatureCollection(JSONObject(geoJson))
        }.getOrDefault(emptyList())

        return geoJsonBuildings.ifEmpty { fallbackCatalogBuildings() }
    }

    fun campusCenter(buildings: List<CampusMapBuilding>): LatLng {
        val points = buildings.flatMap { it.polygon }
        if (points.isEmpty()) return DEFAULT_CAMPUS_CENTER

        return LatLng(
            points.map { it.latitude }.average(),
            points.map { it.longitude }.average()
        )
    }

    fun buildingAt(
        point: LatLng,
        buildings: List<CampusMapBuilding>
    ): CampusMapBuilding? {
        return buildings.firstOrNull { it.contains(point) }
    }

    fun recommendedBuildingFor(
        point: LatLng,
        buildings: List<CampusMapBuilding>
    ): CampusMapBuilding? {
        return buildingAt(point, buildings) ?: buildings
            .map { building -> building to distanceBetween(point, building.center) }
            .filter { (_, distanceMeters) -> distanceMeters <= NEARBY_BUILDING_THRESHOLD_METERS }
            .minByOrNull { (_, distanceMeters) -> distanceMeters }
            ?.first
    }

    private fun parseFeatureCollection(featureCollection: JSONObject): List<CampusMapBuilding> {
        val features = featureCollection.optJSONArray("features") ?: return emptyList()
        return buildList {
            for (index in 0 until features.length()) {
                val feature = features.optJSONObject(index) ?: continue
                val properties = feature.optJSONObject("properties") ?: JSONObject()
                val geometry = feature.optJSONObject("geometry") ?: continue
                val buildingId = properties.readBuildingId() ?: continue
                val buildingName = properties.readBuildingName() ?: "Edificio $buildingId"
                val polygon = geometry.readPolygon()
                if (polygon.size >= 3) {
                    add(
                        CampusMapBuilding(
                            id = buildingId,
                            name = buildingName,
                            polygon = polygon
                        )
                    )
                }
            }
        }
    }

    private fun JSONObject.readBuildingId(): Int? {
        return listOf("id", "buildingId", "building_id", "codigo", "code")
            .firstNotNullOfOrNull { key ->
                when (val value = opt(key)) {
                    is Number -> value.toInt()
                    is String -> value.toIntOrNull()
                    else -> null
                }
            }
    }

    private fun JSONObject.readBuildingName(): String? {
        return listOf("name", "nombre", "buildingName", "building_name")
            .firstNotNullOfOrNull { key ->
                optString(key).takeIf { it.isNotBlank() }
            }
    }

    private fun JSONObject.readPolygon(): List<LatLng> {
        val coordinates = optJSONArray("coordinates") ?: return emptyList()
        return when (optString("type")) {
            "Polygon" -> coordinates.optJSONArray(0).toLatLngList()
            "MultiPolygon" -> coordinates.optJSONArray(0)?.optJSONArray(0).toLatLngList()
            else -> emptyList()
        }
    }

    private fun JSONArray?.toLatLngList(): List<LatLng> {
        if (this == null) return emptyList()

        return buildList {
            for (index in 0 until length()) {
                val coordinate = optJSONArray(index) ?: continue
                val longitude = coordinate.optDouble(0, Double.NaN)
                val latitude = coordinate.optDouble(1, Double.NaN)
                if (!latitude.isNaN() && !longitude.isNaN()) {
                    add(LatLng(latitude, longitude))
                }
            }
        }
    }

    private fun fallbackCatalogBuildings(): List<CampusMapBuilding> {
        val startLatitude = 4.6267
        val startLongitude = -74.0662
        val latitudeStep = 0.00034
        val longitudeStep = 0.00034
        val size = 0.00018

        return BuildingCatalog.buildings.mapIndexed { index, building ->
            val row = index / 6
            val column = index % 6
            val latitude = startLatitude + row * latitudeStep
            val longitude = startLongitude + column * longitudeStep
            CampusMapBuilding(
                id = building.id,
                name = building.name,
                polygon = rectanglePolygon(
                    center = LatLng(latitude, longitude),
                    halfSize = size
                )
            )
        }
    }

    private fun rectanglePolygon(
        center: LatLng,
        halfSize: Double
    ): List<LatLng> {
        return listOf(
            LatLng(center.latitude - halfSize, center.longitude - halfSize),
            LatLng(center.latitude - halfSize, center.longitude + halfSize),
            LatLng(center.latitude + halfSize, center.longitude + halfSize),
            LatLng(center.latitude + halfSize, center.longitude - halfSize)
        )
    }

    private fun distanceBetween(
        first: LatLng,
        second: LatLng
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            first.latitude,
            first.longitude,
            second.latitude,
            second.longitude,
            results
        )
        return results[0]
    }

    private companion object {
        const val CAMPUS_GEOJSON_FILE = "campus.geojson"
        const val NEARBY_BUILDING_THRESHOLD_METERS = 120f
    }
}
