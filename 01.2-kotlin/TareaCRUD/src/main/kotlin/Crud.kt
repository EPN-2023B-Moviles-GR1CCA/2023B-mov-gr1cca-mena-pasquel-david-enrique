import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class Zoologico(val idZoo: Int, val nombreComun: String, val nombreCientifico: String, val diurno: Boolean, val paisOriginario: String)
data class Fauna(val idAnimal: Int, val idZoo: Int, val nombreNacimiento: String, val peso: Double, val fechaNacimiento: String)


class GestorDatos {
    private val gson = Gson()
    private var zoologicos: MutableList<Zoologico> = mutableListOf()
    private var fauna: MutableList<Fauna> = mutableListOf()

    init {
        leerDatos()
    }

    private fun leerDatos() {
        val archivoZoologicos = File("data/zoologicos.json")
        val archivoFauna = File("data/fauna.json")

        if (archivoZoologicos.exists()) {
            val type = object : TypeToken<List<Zoologico>>() {}.type
            zoologicos = gson.fromJson(archivoZoologicos.readText(), type)
        }

        if (archivoFauna.exists()) {
            val type = object : TypeToken<List<Fauna>>() {}.type
            fauna = gson.fromJson(archivoFauna.readText(), type)
        }
    }

    private fun escribirDatos() {
        File("data/zoologicos.json").writeText(gson.toJson(zoologicos))
        File("data/fauna.json").writeText(gson.toJson(fauna))
    }

    fun crearZoologico(zoologico: Zoologico) {
        zoologicos.add(zoologico)
        escribirDatos()
    }

    fun crearFauna(fauna: Fauna) {
        this.fauna.add(fauna)
        escribirDatos()
    }

    fun obtenerZoologico(idZoo: Int): Zoologico? {
        return zoologicos.find { it.idZoo == idZoo }
    }

    fun obtenerFauna(idAnimal: Int): Fauna? {
        return fauna.find { it.idAnimal == idAnimal }
    }

    fun actualizarZoologico(zoologico: Zoologico) {
        val index = zoologicos.indexOfFirst { it.idZoo == zoologico.idZoo }
        if (index != -1) {
            zoologicos[index] = zoologico
            escribirDatos()
        }
    }

    fun actualizarFauna(fauna: Fauna) {
        val index = this.fauna.indexOfFirst { it.idAnimal == fauna.idAnimal }
        if (index != -1) {
            this.fauna[index] = fauna
            escribirDatos()
        }
    }

    fun eliminarZoologico(idZoo: Int) {
        zoologicos.removeIf { it.idZoo == idZoo }
        escribirDatos()
    }

    fun eliminarFauna(idAnimal: Int) {
        fauna.removeIf { it.idAnimal == idAnimal }
        escribirDatos()
    }

    fun obtenerIdZooPorIdAnimal(idAnimal: Int): Int {
        val fauna = fauna.find { it.idAnimal == idAnimal }
        return fauna?.idZoo ?: throw IllegalArgumentException("No se encontró el animal con id: $idAnimal")
    }

}
fun main() {
    val gestorDatos = GestorDatos()

    while (true) {
        println("1. Crear zoologico")
        println("2. Crear fauna")
        println("3. Ver zoologico")
        println("4. Ver fauna")
        println("5. Actualizar zoologico")
        println("6. Actualizar fauna")
        println("7. Eliminar zoologico")
        println("8. Eliminar fauna")
        println("9. Salir")
        when (readLine()) {
            "1" -> {
                println("Ingrese los datos del zoologico (idZoo, nombreComun, nombreCientifico, diurno, paisOriginario):")
                val datos = readLine()!!.split(',').map { it.trim() }
                if (datos.size == 5) {
                    val (idZoo, nombreComun, nombreCientifico, diurno, paisOriginario) = datos
                    gestorDatos.crearZoologico(Zoologico(idZoo.toInt(), nombreComun, nombreCientifico, diurno.toBoolean(), paisOriginario))
                } else {
                    println("Por favor, ingrese exactamente 5 valores.")
                }
            }
            "2" -> {
                println("Ingrese los datos de la fauna (idAnimal, idZoo, nombreNacimiento, peso, fechaNacimiento):")
                val datos = readLine()!!.split(',').map { it.trim() }
                if (datos.size == 5) {
                    val (idAnimal, idZoo, nombreNacimiento, peso, fechaNacimiento) = datos
                    gestorDatos.crearFauna(Fauna(idAnimal.toInt(), idZoo.toInt(), nombreNacimiento, peso.toDouble(),fechaNacimiento))
                } else {
                    println("Por favor, ingrese exactamente 5 valores.")
                }
            }
            "3" -> {
                println("Ingrese el id del zoologico:")
                val idZoo = readLine()!!.toInt()
                println(gestorDatos.obtenerZoologico(idZoo))
            }
            "4" -> {
                println("Ingrese el id del animal:")
                val idAnimal = readLine()!!.toInt()
                println(gestorDatos.obtenerFauna(idAnimal))
            }
            "5" -> {
                println("Ingrese el id del zoologico a modificar:")
                val idZoo = readLine()!!.trim().toInt()
                println("Datos actuales: \n" + gestorDatos.obtenerZoologico(idZoo) + "\n")
                println("Ingrese los nuevos datos del zoologico (nombreComun, nombreCientifico, diurno, paisOriginario):")
                val (nombreComun, nombreCientifico, diurno, paisOriginario) = readLine()!!.split(',').map { it.trim() }
                gestorDatos.actualizarZoologico(Zoologico(idZoo, nombreComun, nombreCientifico, diurno.toBoolean(), paisOriginario))
            }
            "6" -> {
                println("Ingrese el id del animal a modificar:")
                val idAnimal = readLine()!!.trim().toInt()
                val idZoo = gestorDatos.obtenerIdZooPorIdAnimal(idAnimal)
                println("Datos Actuales: \n"+ gestorDatos.obtenerFauna(idAnimal)+ "\n")
                println("Ingrese los nuevos datos de la fauna (nombreNacimiento, peso, fechaNacimiento):")
                val datos = readLine()!!.split(',').map { it.trim() }
                if (datos.size == 5) {
                    val (nombreNacimiento, peso, fechaNacimiento) = datos
                    gestorDatos.actualizarFauna(Fauna(idAnimal, idZoo, nombreNacimiento, peso.toDouble(), fechaNacimiento))
                } else {
                    println("Por favor, ingrese exactamente 5 valores.")
                }
            }
            "7" -> {
                println("Ingrese el id del zoologico a eliminar:")
                val idZoo = readLine()!!.toInt()
                gestorDatos.eliminarZoologico(idZoo)
            }
            "8" -> {
                println("Ingrese el id del animal a eliminar:")
                val idAnimal = readLine()!!.toInt()
                gestorDatos.eliminarFauna(idAnimal)
            }
            "9" -> break
        }
    }
}