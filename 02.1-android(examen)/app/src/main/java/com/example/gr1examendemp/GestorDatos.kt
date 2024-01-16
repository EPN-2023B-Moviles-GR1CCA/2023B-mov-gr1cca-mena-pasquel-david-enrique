package com.example.gr1examendemp

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class GestorDatos(private val context: Context) {

    private val gson = Gson()
    private var ZooBases: MutableList<ZooBase> = mutableListOf()
    private var FaunaBase: MutableList<FaunaBase> = mutableListOf()

    private val nombreArchivoZoologicos = "zoologicos.json"
    private val nombreArchivoFauna = "fauna.json"

    init{
        leerDatos()
    }
    private fun leerDatos() {
        try {
            val jsonZooBases = leerDatosDesdeArchivo(nombreArchivoZoologicos)
            val typeZooBases = object : TypeToken<List<ZooBase>>() {}.type
            ZooBases = gson.fromJson(jsonZooBases, typeZooBases)

            val jsonFaunaBase = leerDatosDesdeArchivo(nombreArchivoFauna)
            val typeFaunaBase = object : TypeToken<List<FaunaBase>>() {}.type
            FaunaBase = gson.fromJson(jsonFaunaBase, typeFaunaBase)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun escribirDatos() {
        try {
            Log.d("EscriDat", ZooBases.toString())
            val jsonZooBases = gson.toJson(ZooBases)
            escribirDatosEnArchivo(nombreArchivoZoologicos, jsonZooBases)

            val jsonFaunaBase = gson.toJson(FaunaBase)
            escribirDatosEnArchivo(nombreArchivoFauna, jsonFaunaBase)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun leerDatosDesdeArchivo(nombreArchivo: String): String {
        return try {
            val inputStream = context.openFileInput(nombreArchivo)
            val contenido = inputStream.bufferedReader().use {
                it.readText()
            }
            Log.d("LeerDatos", "Contenido leído de $nombreArchivo: $contenido")
            contenido
        } catch (e: IOException) {
            Log.e("LeerDatos", "Error al leer datos desde $nombreArchivo: ${e.message}")
            ""
        }
    }

    private fun escribirDatosEnArchivo(nombreArchivo: String, datos: String) {
        try {
            context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE).use {
                it.write(datos.toByteArray())
            }
            Log.d("EscribirDatos", "Datos escritos en $nombreArchivo: $datos")
        } catch (e: IOException) {
            Log.e("EscribirDatos", "Error al escribir datos en $nombreArchivo: ${e.message}")
            e.printStackTrace()
        }
    }

    fun crearZooBase(ZooBase: ZooBase) {
        ZooBases.add(ZooBase)
        escribirDatos()
    }

    fun crearFaunaBase(FaunaBase: FaunaBase) {
        this.FaunaBase.add(FaunaBase)
        escribirDatos()
    }

    fun obtenerZooBase(idZoo: Int): ZooBase? {
        return ZooBases.find { it.idZoo == idZoo }
    }

    fun obtenerFaunaBase(idAnimal: Int): FaunaBase? {
        return FaunaBase.find { it.idAnimal == idAnimal }
    }

    fun actualizarZooBase(ZooBase: ZooBase) {
        val index = ZooBases.indexOfFirst { it.idZoo == ZooBase.idZoo }
        if (index != -1) {
            ZooBases[index] = ZooBase
            escribirDatos()
        }
    }

    fun actualizarFaunaBase(FaunaBase: FaunaBase) {
        val index = this.FaunaBase.indexOfFirst { it.idAnimal == FaunaBase.idAnimal }
        if (index != -1) {
            this.FaunaBase[index] = FaunaBase
            escribirDatos()
        }
    }

    fun eliminarZooBase(idZoo: Int) {
        ZooBases.removeIf { it.idZoo == idZoo }
        escribirDatos()
    }

    fun eliminarFaunaBase(idAnimal: Int?) {
        if (idAnimal == null) {
            FaunaBase.removeIf { it.idAnimal == null }
        } else {
            FaunaBase.removeIf { it.idAnimal == idAnimal }
        }
        escribirDatos()
    }

    fun obtenerIdZooPorIdAnimal(idAnimal: Int): Int {
        val FaunaBase = FaunaBase.find { it.idAnimal == idAnimal }
        return FaunaBase?.idZoo ?: throw IllegalArgumentException("No se encontró el animal con id: $idAnimal")
    }

    fun obtenerUltimoId(): Int? {
        val ultimoIdZoo: Int? = ZooBases.lastOrNull()?.idZoo
        return ultimoIdZoo
    }

    fun obtenerUltimoIdFauna(): Int? {
        val ultimoIdFauna: Int? = FaunaBase.lastOrNull()?.idAnimal
        return ultimoIdFauna
    }

    fun obtenerUltimoIdFaunaPorIdZoo(idZoo: Int): Int {
        val faunaFiltrada = FaunaBase.toList().filter { it.idZoo == idZoo }
        val ultimoIdFauna: Int? = faunaFiltrada.lastOrNull()?.idAnimal
        return ultimoIdFauna ?: 1
    }

    fun getZooBases(): List<ZooBase> {
        return ZooBases.toList()
    }

    fun getFaunaBase():List<FaunaBase>{
        return FaunaBase.toList()
    }

    fun getFaunaBasePorIdZoo(idZoo: Int): List<FaunaBase> {
        // Filtrar la lista de FaunaBase por el idZoo especificado
        return FaunaBase.toList().filter { it.idZoo == idZoo }
    }
}