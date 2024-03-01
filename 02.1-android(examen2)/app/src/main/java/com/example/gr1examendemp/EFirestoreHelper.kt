package com.example.gr1examendemp


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
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


    fun listarZoo(): ArrayList<ZooBase>{
        val lista = arrayListOf<ZooBase>()
        base.collection("zoologicos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val zoo = document.toObject(ZooBase::class.java)

                    lista.add(zoo)
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error getting documents", e)
            }
        for (lis in lista){
            Log.d(TAG, "$lis")
        }
        return lista
    }



    fun actualizarZoologico(idZoo:Int, nombreComun:String, nombreCientifico:String, diurno:Boolean, paisOriginario:String ): Boolean{
        val universidad = hashMapOf(
            "idZoo" to idZoo,
            "nombreComun" to nombreComun,
            "nombreCientifico" to nombreCientifico,
            "diurno" to diurno,
            "paisOriginario" to paisOriginario
        )
        base.collection("zoologico").document(idZoo.toString())
            .set(universidad)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot acualizado con exito!")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error actualizando document", e)
            }
        return true
    }


    fun eliminarZoo(id:Int):Boolean{
        base.collection("zoologico").document(id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot eliminado con exio!")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error eliminando document", e)
            }
        return true
    }


    //Fauna

    fun crearFauna(idAnimal:Int, nombreNacimiento:String, peso:Double, fechaNacimiento: String):Boolean{
        val facultad = hashMapOf(
            "idAnimal" to idAnimal,
            "nombreNacimiento" to nombreNacimiento,
            "peso" to peso,
            "fechaNacimiento" to fechaNacimiento,
        )

        base.collection("faunas")
            .add(facultad)
            .addOnSuccessListener { documentReference: DocumentReference ->
                Log.d(TAG, "DocumentSnapshot agregado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error agregando documento", e)
            }
        return true
    }

    fun listarFaunas(): ArrayList<FaunaBase>{
        var lista = arrayListOf<FaunaBase>()
        base.collection("faunas")
            .get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                for (document in documents) {
                    val facultad = document.toObject(FaunaBase::class.java)
                    lista.add(facultad)
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error obteniendo documents", e)
            }
        return lista
    }



    fun actualizarFacultad(idAnimal:Int, nombreNacimiento:String, peso:Double, fechaNacimiento: String):Boolean{
        val facultad = hashMapOf(
            "idAnimal" to idAnimal,
            "nombreNacimiento" to nombreNacimiento,
            "peso" to peso,
            "fechaNacimiento" to fechaNacimiento,
        )
        base.collection("faunas").document(idAnimal.toString())
            .set(facultad)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error updating document", e)
            }
        return true
    }

    fun eliminarFauna(id:Int):Boolean{
        base.collection("faunas").document(id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error deleting document", e)
            }
        return true
    }

}