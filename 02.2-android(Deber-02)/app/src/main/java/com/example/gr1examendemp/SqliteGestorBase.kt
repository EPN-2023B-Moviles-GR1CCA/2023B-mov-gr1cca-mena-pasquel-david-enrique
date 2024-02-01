package com.example.gr1examendemp

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
               nombreZoo VARCHAR(50)
               ) 
            """.trimIndent()

        val scriptSQLCrearTablaFauna = """
               CREATE TABLE FAUNA(
               idAnimal INTEGER PRIMARY KEY AUTOINCREMENT,
               idZoo INTEGER,
               nombre VARCHAR(50),
               especie VARCHAR(50),
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

    fun crearZooBase(nombreZoo: String): Long {
        val basedatosEscritura = writableDatabase
        val valoresAGuardar = ContentValues()
        valoresAGuardar.put("nombreZoo", nombreZoo)
        return basedatosEscritura.insert("ZOOBASE", null, valoresAGuardar)
    }

    fun crearFauna(idZoo: Int, nombre: String, especie: String): Long {
        val basedatosEscritura = writableDatabase
        val valoresAGuardar = ContentValues()
        valoresAGuardar.put("idZoo", idZoo)
        valoresAGuardar.put("nombre", nombre)
        valoresAGuardar.put("especie", especie)
        return basedatosEscritura.insert("FAUNA", null, valoresAGuardar)
    }

    fun obtenerZooBase(idZoo: Int): ZooBase? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM ZOOBASE WHERE idZoo = ?"
        val parametrosConsultaLectura = arrayOf(idZoo.toString())
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(
            scriptConsultaLectura,
            parametrosConsultaLectura
        )

        val existeZoo = resultadoConsultaLectura.moveToFirst()
        val zooEncontrado = if (existeZoo) {
            val id = resultadoConsultaLectura.getInt(0)
            val nombre = resultadoConsultaLectura.getString(1)
            ZooBase(id, nombre)
        } else {
            null
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return zooEncontrado
    }

    fun obtenerFauna(idAnimal: Int): FaunaBase? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM FAUNA WHERE idAnimal = ?"
        val parametrosConsultaLectura = arrayOf(idAnimal.toString())
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(
            scriptConsultaLectura,
            parametrosConsultaLectura
        )

        val existeFauna = resultadoConsultaLectura.moveToFirst()
        val faunaEncontrada = if (existeFauna) {
            val idZoo = resultadoConsultaLectura.getInt(1)
            val nombre = resultadoConsultaLectura.getString(2)
            val especie = resultadoConsultaLectura.getString(3)
            FaunaBase(idAnimal, idZoo, nombre, especie)
        } else {
            null
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return faunaEncontrada
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

    fun actualizarFauna(idAnimal: Int, idZoo: Int, nombre: String, especie: String): Boolean {
        val conexionEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("idZoo", idZoo)
        valoresAActualizar.put("nombre", nombre)
        valoresAActualizar.put("especie", especie)

        val parametrosConsultaActualizar = arrayOf(idAnimal.toString())
        val resultadoActualizacion = conexionEscritura.update(
            "FAUNA", // Nombre tabla
            valoresAActualizar, // Valores
            "idAnimal=?", // Consulta Where
            parametrosConsultaActualizar
        )

        conexionEscritura.close()
        return resultadoActualizacion != -1
    }

    fun eliminarZooBase(idZoo: Int): Boolean {
        val conexionEscritura = writableDatabase

        val parametrosConsultaDelete = arrayOf(idZoo.toString())
        val resultadoEliminacion = conexionEscritura.delete(
            "ZOOBASE", // Nombre tabla
            "idZoo=?", // Consulta Where
            parametrosConsultaDelete
        )

        conexionEscritura.close()
        return resultadoEliminacion != -1
    }

    fun eliminarFauna(idAnimal: Int): Boolean {
        val conexionEscritura = writableDatabase

        val parametrosConsultaDelete = arrayOf(idAnimal.toString())
        val resultadoEliminacion = conexionEscritura.delete(
            "FAUNA", // Nombre tabla
            "idAnimal=?", // Consulta Where
            parametrosConsultaDelete
        )

        conexionEscritura.close()
        return resultadoEliminacion != -1
    }

    fun getTodasLasZooBases(): List<ZooBase> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM ZOOBASE"
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        val zooBases = mutableListOf<ZooBase>()

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val idZoo = resultadoConsultaLectura.getInt(0)
                val nombreZoo = resultadoConsultaLectura.getString(1)
                zooBases.add(ZooBase(idZoo, nombreZoo))
            } while (resultadoConsultaLectura.moveToNext())
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return zooBases
    }

    fun getTodaLaFauna(): List<FaunaBase> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = "SELECT * FROM FAUNA"
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        val faunaBases = mutableListOf<FaunaBase>()

        if (resultadoConsultaLectura.moveToFirst()) {
            do {
                val idAnimal = resultadoConsultaLectura.getInt(0)
                val idZoo = resultadoConsultaLectura.getInt(1)
                val nombre = resultadoConsultaLectura.getString(2)
                val especie = resultadoConsultaLectura.getString(3)
                faunaBases.add(FaunaBase(idAnimal, idZoo, nombre, especie))
            } while (resultadoConsultaLectura.moveToNext())
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return faunaBases
    }

    // Otros métodos según sea necesario

    // ...

}
