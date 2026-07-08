package com.example.javereporta.domain.model

import com.google.android.gms.maps.model.LatLng

data class CampusMapBuilding(
    val id: Int,
    val name: String,
    val polygon: List<LatLng>
) {
    val center: LatLng
        get() {
            if (polygon.isEmpty()) return DEFAULT_CAMPUS_CENTER
            val latitude = polygon.map { it.latitude }.average()
            val longitude = polygon.map { it.longitude }.average()
            return LatLng(latitude, longitude)
        }

    fun contains(point: LatLng): Boolean {
        if (polygon.size < 3) return false

        var contains = false
        var previousIndex = polygon.lastIndex
        polygon.indices.forEach { currentIndex ->
            val current = polygon[currentIndex]
            val previous = polygon[previousIndex]
            val crossesLatitude = current.latitude > point.latitude != previous.latitude > point.latitude
            if (crossesLatitude) {
                val intersectionLongitude =
                    (previous.longitude - current.longitude) *
                        (point.latitude - current.latitude) /
                        (previous.latitude - current.latitude) +
                        current.longitude
                if (point.longitude < intersectionLongitude) {
                    contains = !contains
                }
            }
            previousIndex = currentIndex
        }
        return contains
    }

    companion object {
        val DEFAULT_CAMPUS_CENTER = LatLng(4.6282, -74.0647)
    }
}
