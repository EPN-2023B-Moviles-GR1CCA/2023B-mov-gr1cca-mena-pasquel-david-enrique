package com.example.gr1examendemp

import android.os.Parcel
import android.os.Parcelable

class FaunaBase(
    val idAnimal: Int,
    val nombreNacimiento: String?,
    val peso: Double?,
    val fechaNacimiento: String?
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt() ,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    )
    constructor() : this(0, null, null, null)

    override fun toString(): String {
        return """
            |ID Animal: $idAnimal
            |Nombre de Nacimiento: $nombreNacimiento
            |Peso: $peso
            |Fecha de Nacimiento: $fechaNacimiento
        """.trimMargin()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idAnimal)
        parcel.writeString(nombreNacimiento)
        parcel.writeValue(peso)
        parcel.writeString(fechaNacimiento)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FaunaBase> {
        override fun createFromParcel(parcel: Parcel): FaunaBase {
            return FaunaBase(parcel)
        }

        override fun newArray(size: Int): Array<FaunaBase?> {
            return arrayOfNulls(size)
        }
    }
}
