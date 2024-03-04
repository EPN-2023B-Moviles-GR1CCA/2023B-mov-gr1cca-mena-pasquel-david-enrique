package com.example.gr1examendemp


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import java.lang.Exception

class EFirestoreHelper {

    private val TAG = "Firestore"
    private val base = FirebaseFirestore.getInstance()


    // Zoologico

    fun crearZoologico(idZoo:Int, nombreComun:String, nombreCientifico:String, diurno:Boolean, paisOriginario:String ): Boolean{
        val zoologicos = hashMapOf(
            "idZoo" to idZoo,
            "nombreComun" to nombreComun,
            "nombreCientifico" to nombreCientifico,
            "diurno" to diurno,
            "paisOriginario" to paisOriginario
        )

        base.collection("zoologicos")
            .add(zoologicos)
            .addOnSuccessListener { documentReference: DocumentReference ->
                Log.d(TAG, "DocumentSnapshot agregado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error agregando documento", e)
            }
        return true
    }


    fun listarZoo(callback: (ArrayList<ZooBase>) -> Unit) {
        val lista = arrayListOf<ZooBase>()
        base.collection("zoologicos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val zoo = document.toObject(ZooBase::class.java)
                    lista.add(zoo)
                }
                // Ordenar la lista por alguna propiedad (por ejemplo, nombreComun)
                val listaOrdenada = lista.sortedBy { it.idZoo }
                callback(ArrayList(listaOrdenada))
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error getting documents", e)
                callback(arrayListOf())
            }
    }



    fun actualizarZoologico(idZoo: Int, nombreComun: String, nombreCientifico: String, diurno: Boolean, paisOriginario: String, callback: (Boolean) -> Unit) {
        val zoo = hashMapOf(
            "idZoo" to idZoo,
            "nombreComun" to nombreComun,
            "nombreCientifico" to nombreCientifico,
            "diurno" to diurno,
            "paisOriginario" to paisOriginario
        )
        Log.d("idActu", "${idZoo}")

        base.collection("zoologicos")
            .whereEqualTo("idZoo", idZoo) // Buscar por el campo personalizado
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    val documentId = documents.documents[0].id
                    base.collection("zoologicos").document(documentId)
                        .update(zoo as Map<String, Any>)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot actualizado con éxito!")
                            callback(true)
                        }
                        .addOnFailureListener { e: Exception ->
                            Log.w(TAG, "Error actualizando document", e)
                            callback(false)
                        }
                } else {
                    Log.d(TAG, "No se encontró ningún documento con el campo idZoo igual a $idZoo")
                    callback(false)
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error buscando documento", e)
                callback(false)
            }
    }


    fun eliminarZooPorId(idZoo: Int, callback: () -> Unit) {
        base.collection("zoologicos")
            .whereEqualTo("idZoo", idZoo)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                callback()
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error eliminando documento", e)
            }
    }


    //Fauna

    fun crearFauna(idAnimal: Int, nombreNacimiento: String, peso: Double, fechaNacimiento: String, idZoo: Int): Boolean {
        val fauna = hashMapOf(
            "idAnimal" to idAnimal,
            "nombreNacimiento" to nombreNacimiento,
            "peso" to peso,
            "fechaNacimiento" to fechaNacimiento,
            "idZoo" to idZoo  // Asociación al Zoologico
        )

        base.collection("faunas")
            .add(fauna)
            .addOnSuccessListener { documentReference: DocumentReference ->
                Log.d(TAG, "DocumentSnapshot agregado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error agregando documento", e)
            }
        return true
    }

    fun listarFaunas(idZoo: Int, callback: (ArrayList<FaunaBase>) -> Unit) {
        val lista = arrayListOf<FaunaBase>()

        base.collection("faunas")
            .whereEqualTo("idZoo", idZoo) // Filtrar por el idZoo
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val fauna = document.toObject(FaunaBase::class.java)
                    lista.add(fauna)
                }
                val listaOrdenada = lista.sortedBy { it.idAnimal }
                callback(ArrayList(listaOrdenada))
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error obteniendo documents", e)
                callback(arrayListOf())
            }
    }


    fun actualizarFauna(idAnimal: Int, nombreNacimiento: String, peso: Double, fechaNacimiento: String, idZoo: Int, callback: (Boolean) -> Unit) {
        val fauna = hashMapOf(
            "idAnimal" to idAnimal,
            "nombreNacimiento" to nombreNacimiento,
            "peso" to peso,
            "fechaNacimiento" to fechaNacimiento,
            "idZoo" to idZoo
        )

        base.collection("faunas")
            .whereEqualTo("idZoo", idZoo) // Filtrar por el idZoo específico
            .whereEqualTo("idAnimal", idAnimal) // Filtrar por el idAnimal específico
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    val documentId = documents.documents[0].id
                    base.collection("faunas").document(documentId)
                        .update(fauna as Map<String, Any>)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot actualizado con éxito!")
                            callback(true)
                        }
                        .addOnFailureListener { e: Exception ->
                            Log.w(TAG, "Error actualizando document", e)
                            callback(false)
                        }
                } else {
                    Log.d(TAG, "No se encontró ningún documento con el campo idAnimal igual a $idAnimal y idZoo igual a $idZoo")
                    callback(false)
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error buscando documento", e)
                callback(false)
            }
    }




    fun eliminarFaunaPorIds(idZoo: Int, idAnimal: Int, callback: () -> Unit) {
        base.collection("faunas")
            .whereEqualTo("idZoo", idZoo)
            .whereEqualTo("idAnimal", idAnimal) // Agrega la condición para idAnimal
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                callback()
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error eliminando documento", e)
            }
    }

}