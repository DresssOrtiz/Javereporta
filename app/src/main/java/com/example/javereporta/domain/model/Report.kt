package com.example.javereporta.domain.model

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val category: ReportCategory,
    val status: ReportStatus,
    val location: LocationPoint,
    val authorUserId: String,
    val createdAtMillis: Long
)
