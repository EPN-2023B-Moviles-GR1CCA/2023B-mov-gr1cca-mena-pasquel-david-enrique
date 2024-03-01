package com.example.gr1examendemp


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

class EFirestoreHelper {

    private val TAG = "Firesore"
    private val base = FirebaseFirestore.getInstance()


    // Zoologico

    fun crearZoologico(idZoo:Int, nombreComun:String, nombreCientifico:String, diurno:Boolean, paisOriginario:String ): Boolean{
        val universidad = hashMapOf(
            "idZoo" to idZoo,
            "nombreComun" to nombreComun,
            "nombreCientifico" to nombreCientifico,
            "diurno" to diurno,
            "paisOriginario" to paisOriginario
        )

        base.collection("zoologicos")
            .add(universidad)
            .addOnSuccessListener { documentReference: DocumentReference ->
                Log.d(TAG, "DocumentSnapshot agregado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error agregando documento", e)
            }
        return true
    }


    fun listarZoo(): ArrayList<ZooBase>{
        var lista = arrayListOf<ZooBase>()
        base.collection("zoologicos")
            .get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                for (document in documents) {
                    val zoo = document.toObject(ZooBase::class.java)
                    lista.add(zoo)
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error getting documents", e)
            }
        return lista
    }



    fun actualizarZoologico(id1:Int, nombre:String, fechaFundacion:String, tipo:String, estudiantes:String, postgrado:String ):Boolean{
        val universidad = hashMapOf(
            "nombre" to nombre,
            "fundacion" to fechaFundacion,
            "tipo" to tipo,
            "estudiantes" to estudiantes,
            "postgrado" to postgrado
        )
        base.collection("universidades").document(id1.toString())
            .set(universidad)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Error updating document", e)
            }
        return true
    }
}