package com.example.gr1examendemp

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

class ZooBase(
    val idZoo: Int,
    val nombreComun: String?,
    val nombreCientifico: String?,
    val diurno: Boolean?,
    val paisOriginario: String?
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


    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBoolean(),
        parcel.readString()
    )

    constructor() : this(0, null, null, null, null)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idZoo)
        parcel.writeString(nombreComun)
        parcel.writeString(nombreCientifico)
        parcel.writeBoolean(diurno!!)
        parcel.writeString(paisOriginario)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<ZooBase> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): ZooBase {
            return ZooBase(parcel)
        }

        override fun newArray(size: Int): Array<ZooBase?> {
            return arrayOfNulls(size)
        }
    }

}
