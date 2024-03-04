package com.example.gr1examendemp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class BFauna : AppCompatActivity() {

    private lateinit var gestorDatos: GestorDatos
    private lateinit var listViewU: ListView
    var idZooB=0
    var datoZoo = 0 // dato que sale del anterior activity
    var idZoologico = 0  // dato que se actualiza el id del firebase



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_fauna)

        BDCompObj.CompZoologico = EFirestoreHelper()
        datoZoo = intent.getIntExtra("ZOO_ID",1)

        listViewU = findViewById(R.id.lv_fauna)

    }


    override fun onStart() {
        super.onStart()
        Log.i("ciclo-vida", "onStart")

        BDCompObj.CompZoologico?.listarFaunas(datoZoo) { lista ->
            val adaptador = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                lista.map { it.toString() }
            )
            listViewU.adapter = adaptador
            adaptador.notifyDataSetChanged()

            BDCompObj.CompZoologico!!.listarZoo{ lista ->
                lista.forEachIndexed{ index: Int, zooBase: ZooBase ->
                    if (zooBase.idZoo == datoZoo){
                        val button = findViewById<Button>(R.id.buttonToZoo)
                        button.text = zooBase.nombreComun
                    }
                }
            }
        }
        registerForContextMenu(listViewU)

        configurarBotonAgregarFauna()

        val button = findViewById<Button>(R.id.buttonToZoo)
        button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        listViewFauna()

    }





    private fun listViewFauna() {
        BDCompObj.CompZoologico?.listarFaunas(datoZoo) { lista ->
                val adaptador = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    lista.map { it.toString() }
                )
                listViewU.adapter = adaptador
                adaptador.notifyDataSetChanged()
        }
        registerForContextMenu(listViewU)
    }


    var posicionItemSeleccionado = 0
    override fun onCreateContextMenu(menu: ContextMenu?,
                                     v: View?,
                                     menuInfo: ContextMenu.ContextMenuInfo?)
    {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.menu_ver)
        item?.isVisible = false
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        Log.d("IDen", "${info}")
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_editar -> {
                val formularioFauna = layoutInflater.inflate(R.layout.formulario_fauna, null)
                setContentView(formularioFauna)

                // Obtener referencias a las vistas en el nuevo layout
                val editTextNombreNacimiento: EditText = findViewById(R.id.editTextNombreNacimiento)
                val editTextPeso: EditText = findViewById(R.id.editTextPeso)
                val editTextFechaNacimiento: EditText = findViewById(R.id.editTextFechaNacimiento)
                val buttonGuardar: Button = findViewById(R.id.buttonGuardarFauna)

                BDCompObj.CompZoologico!!.listarFaunas(datoZoo) { lista ->

                    val idFauna = lista[posicionItemSeleccionado].idAnimal
                    lista.forEachIndexed { index: Int, fauna: FaunaBase ->
                        if (idFauna == fauna.idAnimal) {
                            editTextNombreNacimiento.setText(fauna.fechaNacimiento)
                            editTextPeso.setText(fauna.peso.toString())
                            editTextFechaNacimiento.setText(fauna.fechaNacimiento)
                        }
                    }

                }

                buttonGuardar.setOnClickListener {
                    // Obtener datos ingresados por el usuario
                    val nuevoNombreNacimiento = editTextNombreNacimiento.text.toString()
                    val nuevoPeso = editTextPeso.text.toString().toDoubleOrNull()
                    val nuevoFechaNacimiento = editTextFechaNacimiento.text.toString()

                    BDCompObj.CompZoologico!!.listarFaunas(datoZoo){ lista ->
                        val id = lista[posicionItemSeleccionado].idAnimal
                        if (nuevoPeso != null) {
                            BDCompObj.CompZoologico!!.actualizarFauna(id, nuevoNombreNacimiento, nuevoPeso, nuevoFechaNacimiento, datoZoo) { success ->
                                if (success) {

                                    val intent = Intent(this, BFauna::class.java)
                                    intent.putExtra("ZOO_ID", datoZoo)
                                    startActivity(intent)
                                    finish()  // Cerrar la actividad actual
                                } else {
                                    // La actualización falló o no se encontró el documento
                                    // Puedes manejar la lógica de error aquí
                                }
                            }
                        }
                    }


                    // Mostrar Snackbar de éxito
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro editado con éxito", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }


                return true
            }
            R.id.menu_eliminar ->{


                BDCompObj.CompZoologico!!.listarFaunas(datoZoo){ lista ->
                    val idFauna = lista[posicionItemSeleccionado].idAnimal
                    BDCompObj.CompZoologico!!.eliminarFaunaPorIds(datoZoo,idFauna){
                        Log.d("FaunaDelete", "Fauna eliminada con éxito")
                        listViewFauna()
                    }
                }

                return true
            }
            
            else -> super.onContextItemSelected(item)
        }
    }




    private fun configurarBotonAgregarFauna() {
        val botonAgregarFauna = findViewById<Button>(R.id.btn_anadir_fauna)

        botonAgregarFauna.setOnClickListener {
            // Inflar el nuevo layout (formulario_base.xml)
            val formularioBase = layoutInflater.inflate(R.layout.formulario_fauna, null)

            // Obtener referencias a las vistas en el nuevo layout
            val TextNombreNacimiento: EditText = formularioBase.findViewById(R.id.editTextNombreNacimiento)
            val TextPeso: EditText = formularioBase.findViewById(R.id.editTextPeso)
            val TextFechaNacimiento: EditText = formularioBase.findViewById(R.id.editTextFechaNacimiento)
            val buttonGuardarFauna: Button = formularioBase.findViewById(R.id.buttonGuardarFauna)
            buttonGuardarFauna.visibility = View.GONE


            val dialog = AlertDialog.Builder(this)
                .setView(formularioBase)
                .setPositiveButton("Guardar") { _, _ ->
                    BDCompObj.CompZoologico?.listarFaunas(datoZoo) { lista ->

                        val idUltimaFauna = if (lista.isNotEmpty()) {
                            lista.last().idAnimal
                        } else {
                            0 // Asignar 1 si la lista está vacía
                        }

                        val plusIDFauna = idUltimaFauna.plus(1)

                        // Obtener datos ingresados por el usuario
                        val nuevoNombreNacimiento = TextNombreNacimiento.text.toString()
                        val nuevoPeso = TextPeso.text.toString().toDoubleOrNull()
                        val nuevoFechaNacimiento = TextFechaNacimiento.text.toString()

                        if (nuevoPeso != null) {
                            BDCompObj.CompZoologico!!.crearFauna(plusIDFauna,nuevoNombreNacimiento,nuevoPeso,nuevoFechaNacimiento, datoZoo)
                        }
                        listViewFauna()
                    }

                }
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.show()
        }
    }
    
}