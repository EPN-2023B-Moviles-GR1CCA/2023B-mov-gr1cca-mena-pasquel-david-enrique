package com.example.gr1examendemp

data class FaunaBase(
    val idAnimal: Int?,
    val idZoo: Int,
    val nombreNacimiento: String,
    val peso: Double?,
    val fechaNacimiento: String
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
