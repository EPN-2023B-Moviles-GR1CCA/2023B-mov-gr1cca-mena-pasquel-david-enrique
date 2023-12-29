package com.example.gr1accdemp2023b

class BEntrenador(
    var id: Int,
    var nombre: String?,
    var descripcion: String?
) {
    override fun toString(): String {
        return "${nombre} - ${descripcion}"
    }

}