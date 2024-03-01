package com.example.gr1examendemp

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

class ZooBase(
    val idZoo: Int?,
    val nombreComun: String,
    val nombreCientifico: String,
    val diurno: Boolean,
    val paisOriginario: String
) : Parcelable{
    override fun toString(): String {
        return """
            |Zoo ID: $idZoo
            |Nombre Común: $nombreComun
            |Nombre Científico: $nombreCientifico
            |Diurno: $diurno
            |Pais Originario: $paisOriginario
        """.trimMargin()
    }


    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idZoo)
        parcel.writeString(nombreComun)
        parcel.writeString(nombreCientifico)
        parcel.writeByte(if (diurno) 1 else 0)
        parcel.writeString(paisOriginario)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ZooBase> {
        override fun createFromParcel(parcel: Parcel): ZooBase {
            return ZooBase(parcel)
        }

        override fun newArray(size: Int): Array<ZooBase?> {
            return arrayOfNulls(size)
        }
    }

}
