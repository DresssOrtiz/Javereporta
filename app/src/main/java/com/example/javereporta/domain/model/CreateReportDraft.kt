package com.example.javereporta.domain.model

data class CreateReportDraft(
    val title: String,
    val description: String,
    val category: ReportCategory,
    val location: LocationPoint?
)
