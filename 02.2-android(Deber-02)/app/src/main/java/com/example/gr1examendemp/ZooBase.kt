package com.example.gr1examendemp

data class ZooBase(
    val idZoo: Int?,
    val nombreComun: String,
    val nombreCientifico: String,
    val diurno: Boolean,
    val paisOriginario: String
) {
    override fun toString(): String {
        return """
            |Zoo ID: $idZoo
            |Nombre Común: $nombreComun
            |Nombre Científico: $nombreCientifico
            |Diurno: $diurno
            |Pais Originario: $paisOriginario
        """.trimMargin()
    }
}
