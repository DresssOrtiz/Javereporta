package com.example.javereporta.domain.model

data class CreateReportDraft(
    val buildingId: Int,
    val buildingName: String,
    val floorName: String,
    val zoneName: String,
    val category: ReportCategory,
    val description: String
)
