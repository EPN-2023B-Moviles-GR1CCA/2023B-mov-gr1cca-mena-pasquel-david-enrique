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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import com.google.android.material.snackbar.Snackbar

class BFauna : AppCompatActivity() {

    private lateinit var gestorDatos: GestorDatos
    private lateinit var listView: ListView
    var idZooB=0


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_fauna)
        // Obtener el dato (zooId) de Intent
        idZooB= intent.getIntExtra("ZOO_ID", -1)
        
        gestorDatos = GestorDatos(this)

        listView = findViewById<ListView>(R.id.lv_fauna)
        registerForContextMenu(actualizarLista())

        configurarBotonAgregarFauna()
        
    }


    var posicionItemSeleccionado = 0
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // Llenamos las opciones del menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        // Obtener el id del ArrayListSeleccionado
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        Log.d("IDen", "${info}")
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    fun mostrarSnackbar(texto:String){
        Snackbar
            .make(
                findViewById(R.id.lv_fauna), // view
                texto, // texto
                Snackbar.LENGTH_LONG // tiempo
            )
            .show()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_editar -> {
                val zooSeleccionado = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].idAnimal
                val idnew = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].idAnimal
                val NombreNacimiento = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].nombreNacimiento
                val Peso = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].peso.toString()
                val FechaNacimiento = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].fechaNacimiento


                val formularioBase = layoutInflater.inflate(R.layout.formulario_fauna, null)
                setContentView(formularioBase)

                // Obtener referencias a las vistas en el nuevo layout
                val editTextNombreNacimiento: EditText = findViewById(R.id.editTextNombreNacimiento)
                val editTextPeso: EditText = findViewById(R.id.editTextPeso)
                val editTextFechaNacimiento: EditText = findViewById(R.id.editTextFechaNacimiento)
                val buttonGuardar: Button = findViewById(R.id.buttonGuardarFauna)

                // Llenar el formulario con los datos existentes
                editTextNombreNacimiento.setText(NombreNacimiento)
                editTextPeso.setText(Peso)
                editTextFechaNacimiento.setText(FechaNacimiento)

                buttonGuardar.setOnClickListener {
                    // Obtener datos ingresados por el usuario
                    val nuevoNombreNacimiento = editTextNombreNacimiento.text.toString()
                    val nuevoPeso = editTextPeso.text.toString().toDoubleOrNull()
                    val nuevoFechaNacimiento = editTextFechaNacimiento.text.toString()

                    val faunaBase = idnew?.let { id ->
                        nuevoPeso?.let { it1 ->
                            FaunaBase(id,idZooB, nuevoNombreNacimiento,
                                it1, nuevoFechaNacimiento )
                        }
                    } ?: FaunaBase(null,idZooB, nuevoNombreNacimiento, null, nuevoFechaNacimiento)

                    Log.d("MainActivity", faunaBase.toString())

                    // Código para actualizar el elemento en la base de datos
                    gestorDatos.actualizarFaunaBase(faunaBase)

                    val dat = gestorDatos.getFaunaBasePorIdZoo(idZooB)
                    // Cambiar la visibilidad de las vistas para mostrar la interfaz principal
                    setContentView(R.layout.activity_b_fauna)
                    configurarBotonAgregarFauna()
                    registerForContextMenu(actualizarLista())

                    // Mostrar Snackbar de éxito
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro editado con éxito", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }

                // val elementoSeleccionado = gestorDatos.actualizarfaunaBase(nuevo faunaBase)
                // mostrarSnackbar


                return true
            }
            R.id.menu_eliminar ->{
                val zooId = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].idAnimal
                Log.d("idZOOO", "$zooId")
                val nombre = gestorDatos.getFaunaBasePorIdZoo(idZooB)[posicionItemSeleccionado].nombreNacimiento
                if (zooId != null) {
                    gestorDatos.eliminarFaunaBase(zooId)
                    mostrarSnackbar("Eliminando: $nombre")
                    Log.d("eliminado", "Eliminando: $zooId")
                    actualizarLista()
                    //abrirDialogo()
                } else {
                    gestorDatos.eliminarFaunaBase(zooId)
                    mostrarSnackbar("No se encontró el elemento para eliminar")
                }
                return true
            }
            
            else -> super.onContextItemSelected(item)
        }
    }


    private fun actualizarLista(): ListView {
        val arreglo = gestorDatos.getFaunaBasePorIdZoo(idZooB)
        val listView = findViewById<ListView>(R.id.lv_fauna)
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            arreglo
        )

        listView.adapter = adaptador
        adaptador.notifyDataSetChanged()
        return listView
    }



    private fun configurarBotonAgregarFauna() {
        val botonAgregarFauna = findViewById<Button>(R.id.btn_anadir_fauna)

        botonAgregarFauna.setOnClickListener {
            // Inflar el nuevo layout (formulario_base.xml)
            val formularioBase = layoutInflater.inflate(R.layout.formulario_fauna, null)

            // Obtener referencias a las vistas en el nuevo layout
            // Obtener referencias a las vistas en el nuevo layout
            val editTextNombreNacimiento: EditText = formularioBase.findViewById(R.id.editTextNombreNacimiento)
            val editTextPeso: EditText = formularioBase.findViewById(R.id.editTextPeso)
            val editTextFechaNacimiento: EditText = formularioBase.findViewById(R.id.editTextFechaNacimiento)
            val buttonGuardarFauna: Button = formularioBase.findViewById(R.id.buttonGuardarFauna)
            buttonGuardarFauna.visibility = View.GONE

            val dialog = AlertDialog.Builder(this)
                .setView(formularioBase)
                .setPositiveButton("Guardar") { _, _ ->
                    // Obtener datos ingresados por el usuario
                    val nuevoNombreNacimiento = editTextNombreNacimiento.text.toString()
                    val nuevoPeso = editTextPeso.text.toString().toDoubleOrNull()
                    val nuevoFechaNacimiento = editTextFechaNacimiento.text.toString()

                    val lastId = gestorDatos.obtenerUltimoIdFaunaPorIdZoo(idZooB)
                    val nuevoIdZoo = lastId?.plus(1)
                    val faunaBase = nuevoIdZoo?.let { id ->
                        nuevoPeso?.let { it1 ->
                            FaunaBase(id,idZooB, nuevoNombreNacimiento,
                                it1, nuevoFechaNacimiento )
                        }
                    } ?: FaunaBase(null,idZooB, nuevoNombreNacimiento, null, nuevoFechaNacimiento)

                    Log.d("RegBaseZoo", faunaBase.toString())

                    // guardar el nuevo elemento
                    gestorDatos.crearFaunaBase(faunaBase)

                    // Actualizar la lista después de guardar el nuevo registro
                    actualizarLista()
                }
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.show()
        }
    }
    
}