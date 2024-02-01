package com.example.gr1examendemp

data class ZooBase(
    var idZoo: Int?,
    var nombreComun: String,
    var nombreCientifico: String,
    var diurno: Boolean,
    var paisOriginario: String
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
