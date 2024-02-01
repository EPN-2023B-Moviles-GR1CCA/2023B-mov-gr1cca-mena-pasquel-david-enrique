package com.example.gr1examendemp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SqliteGestorBase(
    context: Context?
) : SQLiteOpenHelper(
    context,
    "moviles", // nombre BDD
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaZooBase = """
               CREATE TABLE ZOOBASE(
               idZoo INTEGER PRIMARY KEY AUTOINCREMENT,
               nombreComun VARCHAR(50),
               nombreCientifico VARCHAR(50),
               diurno INTEGER,
               paisOriginario VARCHAR(50)
               ) 
            """.trimIndent()

        val scriptSQLCrearTablaFauna = """
               CREATE TABLE FAUNA(
               idAnimal INTEGER PRIMARY KEY AUTOINCREMENT,
               idZoo INTEGER,
               nombreNacimiento VARCHAR(50),
               peso REAL,
               fechaNacimiento VARCHAR(50),
               FOREIGN KEY (idZoo) REFERENCES ZOOBASE(idZoo)
               ) 
            """.trimIndent()

        db?.execSQL(scriptSQLCrearTablaZooBase)
        db?.execSQL(scriptSQLCrearTablaFauna)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Manejar la actualización de la base de datos si es necesario
    }

    // Métodos CRUD personalizados para ZooBase y FaunaBase

    fun crearZooBase(nombreZoo: String, nombreCientifico: String, diurno: Int, paisOriginario: String): Boolean {
        val basedatosEscritura = writableDatabase
        val valoresAGuardar = ContentValues()
        valoresAGuardar.put("nombreZoo", nombreZoo)
        valoresAGuardar.put("nombreCientifico", nombreCientifico)
        valoresAGuardar.put("diurno", diurno)
        valoresAGuardar.put("paisOriginario", paisOriginario)
        val resultadoGuardar = basedatosEscritura
            .insert(
                "ZOOBASE", // Nombre tabla
                null,
                valoresAGuardar // valores
            )
        basedatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }

    fun crearFauna(idZoo: Int, nombreNacimiento: String, peso: Double, fechaNacimiento:String): Boolean {
        val basedatosEscritura = writableDatabase
        val valoresAGuardar = ContentValues()
        valoresAGuardar.put("idZoo", idZoo)
        valoresAGuardar.put("nombreNacimiento", nombreNacimiento)
        valoresAGuardar.put("peso", peso)
        valoresAGuardar.put("fechaNacimiento", fechaNacimiento)
        val resultadoGuardar = basedatosEscritura
            .insert(
                "ENTRENADOR", // Nombre tabla
                null,
                valoresAGuardar // valores
            )
        basedatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }


    fun actualizarZooBase(idZoo: Int, nombreZoo: String): Boolean {
        val conexionEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("nombreZoo", nombreZoo)

        val parametrosConsultaActualizar = arrayOf(idZoo.toString())
        val resultadoActualizacion = conexionEscritura.update(
            "ZOOBASE", // Nombre tabla
            valoresAActualizar, // Valores
            "idZoo=?", // Consulta Where
            parametrosConsultaActualizar
        )

        conexionEscritura.close()
        return resultadoActualizacion != -1
    }

    fun actualizarFaunaBase(FaunaBase: FaunaBase) {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("nombreNacimiento", FaunaBase.nombreNacimiento)
        valoresAActualizar.put("peso", FaunaBase.peso)
        valoresAActualizar.put("fechaNacimiento", FaunaBase.fechaNacimiento)

        val parametrosConsultaActualizar = arrayOf(FaunaBase.idAnimal.toString())
        val condicionWhere = "idAnimal=? AND idZoo=?"

        val resultadoActualizacion = baseDatosEscritura.update(
            "FAUNA",
            valoresAActualizar,
            condicionWhere,
            arrayOf(FaunaBase.idAnimal.toString(), FaunaBase.idZoo.toString())
        )

        baseDatosEscritura.close()

        if (resultadoActualizacion > 0) {
            // Actualización exitosa, puedes manejarlo según tus necesidades
        } else {
            // La fila no existe, no se actualizó correctamente o el idZoo no coincide
        }
    }

    fun eliminarFaunaBase(idZoo: Int, idAnimal: Int): Boolean {
        val conexionEscritura = writableDatabase

        val condicionWhere = "idZoo=? AND idAnimal=?"
        val parametrosConsultaDelete = arrayOf(idZoo.toString(), idAnimal.toString())

        val resultadoEliminacion = conexionEscritura.delete(
            "ZOOBASE", // Nombre tabla
            condicionWhere, // Consulta Where
            parametrosConsultaDelete
        )

        conexionEscritura.close()
        return resultadoEliminacion != -1
    }



    fun obtenerUltimoId(): Int? {
        val baseDatosLectura = readableDatabase

        val scriptConsultaLectura = """
            SELECT idZoo FROM ZOOBASE ORDER BY idZoo DESC LIMIT 1
        """.trimIndent()

        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)
        Log.d("RegBaseZoo", "$resultadoConsultaLectura")
        val ultimoIdZoo: Int? = if (resultadoConsultaLectura.moveToFirst()) {
            resultadoConsultaLectura.getInt(0)
        } else {
            null
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()

        return ultimoIdZoo
    }


    fun obtenerUltimoIdFaunaPorIdZoo(idZoo: Int): Int {
        val baseDatosLectura = readableDatabase

        val scriptConsultaLectura = """
            SELECT idAnimal FROM FAUNA WHERE idZoo = ? ORDER BY idAnimal DESC LIMIT 1
        """.trimIndent()
        val parametrosConsultaLectura = arrayOf(idZoo.toString())

        val resultadoConsultaLectura =
            baseDatosLectura.rawQuery(scriptConsultaLectura, parametrosConsultaLectura)

        val ultimoIdFauna: Int = if (resultadoConsultaLectura.moveToFirst()) {
            resultadoConsultaLectura.getInt(0)
        } else {
            0
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()

        return ultimoIdFauna
    }

    fun getFaunaBasePorIdZoo(idZoo: Int): FaunaBase {
        val baseDatosLectura = readableDatabase

        val scriptConsultaLectura = """
            SELECT * FROM FAUNA WHERE idZoo = ?
        """.trimIndent()

        val parametrosConsultaLectura = arrayOf(idZoo.toString())

        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, parametrosConsultaLectura)

        val faunaBases = mutableListOf<FaunaBase>()

        val faunaEncontrada = FaunaBase(0, 0,"", 0.1, "")

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val idAnimal = resultadoConsultaLectura.getInt(0)
                val idZoo = resultadoConsultaLectura.getInt(1)
                val nombreNacimiento = resultadoConsultaLectura.getString(2)
                val peso = resultadoConsultaLectura.getDouble(3)
                val fechaNacimiento = resultadoConsultaLectura.getString(4)
                if (idAnimal != null){
                    faunaEncontrada.idAnimal = idAnimal
                    faunaEncontrada.idZoo = idZoo
                    faunaEncontrada.nombreNacimiento = nombreNacimiento
                    faunaEncontrada.peso = peso
                    faunaEncontrada.fechaNacimiento = fechaNacimiento
                }
            } while (resultadoConsultaLectura.moveToNext())
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()

        return faunaEncontrada
    }

    fun getZooBases(): List<ZooBase> {
        val baseDatosLectura = readableDatabase

        val scriptConsultaLectura = """
            SELECT * FROM ZOOBASE
        """.trimIndent()
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        val zooBases = mutableListOf<ZooBase>()

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val idZoo = resultadoConsultaLectura.getInt(0)
                val nombreComun = resultadoConsultaLectura.getString(1)
                val nombreCientifico = resultadoConsultaLectura.getString(2)
                val diurno = resultadoConsultaLectura.getInt(3) == 1
                val paisOriginario = resultadoConsultaLectura.getString(4)

                zooBases.add(ZooBase(idZoo, nombreComun, nombreCientifico, diurno, paisOriginario))
            } while (resultadoConsultaLectura.moveToNext())
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()

        return zooBases.toList()
    }


}
