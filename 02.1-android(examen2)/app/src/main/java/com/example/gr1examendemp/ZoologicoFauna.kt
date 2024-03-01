package com.example.gr1examendemp

import android.os.Parcel
import android.os.Parcelable

class ZoologicoFauna(
    val idZooFauna: Int,
    val idZoo: Int,
    val idFauna: Int
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idZooFauna)
        parcel.writeInt(idZoo)
        parcel.writeInt(idFauna)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ZoologicoFauna> {
        override fun createFromParcel(parcel: Parcel): ZoologicoFauna {
            return ZoologicoFauna(parcel)
        }

        override fun newArray(size: Int): Array<ZoologicoFauna?> {
            return arrayOfNulls(size)
        }
    }


}