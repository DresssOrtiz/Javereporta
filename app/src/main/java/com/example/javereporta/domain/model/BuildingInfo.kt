package com.example.javereporta.domain.model

data class BuildingInfo(
    val id: Int,
    val name: String,
    val floors: List<BuildingFloor>
)
