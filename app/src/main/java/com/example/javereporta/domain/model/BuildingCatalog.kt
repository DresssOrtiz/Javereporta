package com.example.javereporta.domain.model

object BuildingCatalog {
    private val academicZones = listOf(
        "Entrada",
        "Pasillo",
        "Salón",
        "Baño",
        "Escaleras",
        "Ascensor",
        "Zona común",
        "Otro"
    )

    private val chapelZones = listOf(
        "Entrada",
        "Nave principal",
        "Altar",
        "Sacristía",
        "Baño",
        "Zona común",
        "Otro"
    )

    private val parkingZones = listOf(
        "Entrada",
        "Bahía",
        "Rampa",
        "Cajero",
        "Escaleras",
        "Ascensor",
        "Zona común",
        "Otro"
    )

    private val facultyZones = listOf(
        "Entrada",
        "Recepción",
        "Salón",
        "Laboratorio",
        "Consultorio",
        "Baño",
        "Zona común",
        "Otro"
    )

    private val sportsZones = listOf(
        "Entrada",
        "Cancha",
        "Gimnasio",
        "Camerinos",
        "Baño",
        "Graderías",
        "Zona común",
        "Otro"
    )

    val buildings = listOf(
        BuildingInfo(0, "Biblioteca Alfonso Barrero Cabal", floors(5, academicZones)),
        BuildingInfo(1, "Casa Navarro", floors(2, academicZones)),
        BuildingInfo(2, "Fernando Barón", floors(5, academicZones)),
        BuildingInfo(3, "Gabriel Giraldo", floors(5, academicZones)),
        BuildingInfo(4, "Gerardo Arango Puerta Artes", floors(3, facultyZones)),
        BuildingInfo(5, "Taller de Diseño Industrial", floors(2, academicZones)),
        BuildingInfo(8, "Centro Atico", floors(4, academicZones)),
        BuildingInfo(9, "Julio Carrizosa", floors(3, academicZones)),
        BuildingInfo(11, "José Gabriel Maldonado Oficinas", floors(3, academicZones)),
        BuildingInfo(12, "José Gabriel Maldonado Laboratorios", floors(5, facultyZones)),
        BuildingInfo(15, "Leopoldo Rother", floors(3, academicZones)),
        BuildingInfo(16, "Carlos Arbeláez Camacho", floors(3, academicZones)),
        BuildingInfo(18, "Talleres de Arquitectura", floors(2, academicZones)),
        BuildingInfo(20, "Jorge Hoyos Vásquez", floors(6, academicZones)),
        BuildingInfo(21, "Emilio Arango", floors(3, academicZones)),
        BuildingInfo(24, "Edificio San Ignacio", floors(3, academicZones)),
        BuildingInfo(25, "Facultad de Odontología", floors(4, facultyZones)),
        BuildingInfo(27, "José del Carmen Acosta", floors(3, academicZones)),
        BuildingInfo(31, "Rafael Barrientos Conto - Morfología", floors(3, facultyZones)),
        BuildingInfo(42, "Facultad de Artes Ala Oriental", floors(3, facultyZones)),
        BuildingInfo(45, "Capilla San Francisco Javier", singleFloor(chapelZones)),
        BuildingInfo(50, "Féliz Restrepo", floors(3, academicZones)),
        BuildingInfo(51, "Ángel Valtierra", floors(3, academicZones)),
        BuildingInfo(52, "Carlos Ortiz", floors(3, academicZones)),
        BuildingInfo(53, "Jesus Emilio Ramirez", floors(3, academicZones)),
        BuildingInfo(67, "José Rafael Arboleda", floors(6, academicZones)),
        BuildingInfo(70, "Capilla Nuestra Señora del Camino", singleFloor(chapelZones)),
        BuildingInfo(91, "Centro de Formacion Deportiva", floors(3, sportsZones)),
        BuildingInfo(94, "Pedro Arrupe", floors(6, academicZones)),
        BuildingInfo(95, "Manuel Briceño Jáurequi", floors(6, academicZones)),
        BuildingInfo(100, "Edificio Ciencias", floors(6, facultyZones)),
        BuildingInfo(115, "Parqueadero Don Guillermo Castro", floors(5, parkingZones))
    )

    private fun floors(count: Int, zones: List<String>): List<BuildingFloor> {
        return (1..count).map { floorNumber ->
            BuildingFloor(
                name = "Piso $floorNumber",
                zones = zones
            )
        }
    }

    private fun singleFloor(zones: List<String>): List<BuildingFloor> {
        return listOf(
            BuildingFloor(
                name = "Piso 1",
                zones = zones
            )
        )
    }
}
