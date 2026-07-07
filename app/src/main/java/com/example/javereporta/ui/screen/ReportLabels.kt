package com.example.javereporta.ui.screen

import com.example.javereporta.domain.model.ReportCategory
import com.example.javereporta.domain.model.ReportStatus

fun ReportCategory.label(): String {
    return when (this) {
        ReportCategory.ROADS -> "Vías y accesos"
        ReportCategory.LIGHTING -> "Iluminación"
        ReportCategory.WATER -> "Agua"
        ReportCategory.WASTE -> "Residuos"
        ReportCategory.SECURITY -> "Seguridad"
        ReportCategory.OTHER -> "Otro"
    }
}

fun ReportStatus.label(): String {
    return when (this) {
        ReportStatus.ABIERTO -> "Abierto"
        ReportStatus.CANCELADO -> "Cancelado"
        ReportStatus.CERRADO -> "Cerrado"
        ReportStatus.DRAFT -> "Borrador"
        ReportStatus.SUBMITTED -> "Enviado"
        ReportStatus.IN_REVIEW -> "En revisión"
        ReportStatus.RESOLVED -> "Resuelto"
        ReportStatus.REJECTED -> "Rechazado"
    }
}
