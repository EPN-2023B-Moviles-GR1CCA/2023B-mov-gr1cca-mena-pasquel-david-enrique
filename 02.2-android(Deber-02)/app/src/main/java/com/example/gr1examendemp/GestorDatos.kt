package com.example.gr1examendemp

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class GestorDatos(private val context: Context) {

    private val gson = Gson()
    private var ZooBases: MutableList<ZooBase> = mutableListOf()
    private var FaunaBase: MutableList<FaunaBase> = mutableListOf()

    private val nombreArchivoZoologicos = "zoologicos.json"
    private val nombreArchivoFauna = "fauna.json"

    init {
        copiarArchivoSiNoExiste(R.raw.zoologicos, nombreArchivoZoologicos)
        copiarArchivoSiNoExiste(R.raw.fauna, nombreArchivoFauna)
        leerDatos()
    }
    private fun leerDatos() {
        try {
            val jsonFauna = leerDatosDesdeArchivo(nombreArchivoFauna)

            val typeFaun = object : TypeToken<List<FaunaBase>>() {}.type
            FaunaBase = gson.fromJson(jsonFauna, typeFaun)

            val jsonZoo = leerDatosDesdeArchivo(nombreArchivoZoologicos)

            val typeZoo = object : TypeToken<List<ZooBase>>() {}.type
            ZooBases = gson.fromJson(jsonZoo, typeZoo)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun escribirDatos() {
        try {
            val jsonZoo = gson.toJson(ZooBases)
            escribirDatosEnArchivo(nombreArchivoZoologicos, jsonZoo)

            val jsonFauna= gson.toJson(FaunaBase)
            escribirDatosEnArchivo(nombreArchivoFauna, jsonFauna)

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

    private fun copiarArchivoDesdeRawARuta(rawResourceId: Int, nombreArchivo: String) {
        try {
            val inputStream: InputStream = context.resources.openRawResource(rawResourceId)
            val outputStream: OutputStream = context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("CopiarArchivo", "Archivo $nombreArchivo copiado exitosamente.")
        } catch (e: IOException) {
            Log.e("CopiarArchivo", "Error al copiar archivo $nombreArchivo: ${e.message}")
        }
    }

    private fun copiarArchivoSiNoExiste(rawResourceId: Int, nombreArchivo: String) {
        val archivo = File(context.filesDir, nombreArchivo)
        if (!archivo.exists()) {
            copiarArchivoDesdeRawARuta(rawResourceId, nombreArchivo)
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
        return ultimoIdFauna ?: 0
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