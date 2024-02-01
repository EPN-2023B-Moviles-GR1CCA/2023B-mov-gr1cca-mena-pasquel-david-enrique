package com.example.gr1examendemp

data class FaunaBase(
    var idAnimal: Int?,
    var idZoo: Int,
    var nombreNacimiento: String,
    var peso: Double?,
    var fechaNacimiento: String
) {
    override fun toString(): String {
        return """
            |ID Animal: $idAnimal
            |ID Zoo: $idZoo
            |Nombre de Nacimiento: $nombreNacimiento
            |Peso: $peso
            |Fecha de Nacimiento: $fechaNacimiento
        """.trimMargin()
    }
}
