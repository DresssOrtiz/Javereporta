package com.example.javereporta.domain.model

data class Report(
    val id: String,
    val buildingId: Int,
    val buildingName: String,
    val floorName: String,
    val zoneName: String,
    val category: ReportCategory,
    val description: String,
    val status: ReportStatus,
    val createdAtMillis: Long,
    val hasBeenEdited: Boolean = false
)
