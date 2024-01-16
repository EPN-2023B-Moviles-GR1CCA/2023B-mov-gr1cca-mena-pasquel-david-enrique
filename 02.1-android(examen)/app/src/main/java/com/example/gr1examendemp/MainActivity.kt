package com.example.gr1examendemp

import android.annotation.SuppressLint
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
import android.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var gestorDatos: GestorDatos
    private lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gestorDatos = GestorDatos(this)



        setContentView(R.layout.activity_main)
        listView = findViewById<ListView>(R.id.lv_list_view)

        configurarBotonAgregarZoo()
        registerForContextMenu(actualizarLista())

        Log.d("fuera","sigue snackbar")
        // Mostrar Snackbar de éxito
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro ingresado con éxito", Snackbar.LENGTH_SHORT)
        snackbar.show()

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
                findViewById(R.id.lv_list_view), // view
                texto, // texto
                Snackbar.LENGTH_LONG // tiempo
            )
            .show()

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_editar -> {
                val zooSeleccionado = gestorDatos.getZooBases()[posicionItemSeleccionado].idZoo
                val idnew = gestorDatos.getZooBases()[posicionItemSeleccionado].idZoo
                val nombreComun = gestorDatos.getZooBases()[posicionItemSeleccionado].nombreComun
                val nombreCientifico = gestorDatos.getZooBases()[posicionItemSeleccionado].nombreCientifico
                val diurno = gestorDatos.getZooBases()[posicionItemSeleccionado].diurno
                val pais = gestorDatos.getZooBases()[posicionItemSeleccionado].paisOriginario


                val formularioBase = layoutInflater.inflate(R.layout.formulario_base, null)
                setContentView(formularioBase)

                // Obtener referencias a las vistas en el nuevo layout
                val editTextNombreComun: EditText = findViewById(R.id.editTextNombreComun)
                val editTextNombreCientifico: EditText = findViewById(R.id.editTextNombreCientifico)
                val checkBoxDiurno: CheckBox = findViewById(R.id.checkBoxDiurno)
                val editTextPaisOriginario: EditText = findViewById(R.id.editTextPaisOriginario)
                val buttonGuardar: Button = findViewById(R.id.buttonGuardar)

                // Llenar el formulario con los datos existentes
                editTextNombreComun.setText(nombreComun)
                editTextNombreCientifico.setText(nombreCientifico)
                checkBoxDiurno.isChecked = diurno
                editTextPaisOriginario.setText(pais)

                buttonGuardar.setOnClickListener {
                    // Obtener datos ingresados por el usuario
                    val nuevoNombreComun = editTextNombreComun.text.toString()
                    val nuevoNombreCientifico = editTextNombreCientifico.text.toString()
                    val nuevoDiurno = checkBoxDiurno.isChecked
                    val nuevoPaisOriginario = editTextPaisOriginario.text.toString()

                    val zooBase = idnew?.let { id ->
                        ZooBase(id, nuevoNombreComun, nuevoNombreCientifico, nuevoDiurno, nuevoPaisOriginario)
                    } ?: ZooBase(null, nuevoNombreComun, nuevoNombreCientifico, nuevoDiurno, nuevoPaisOriginario)

                    Log.d("MainActivity", zooBase.toString())

                    // Código para actualizar el elemento en la base de datos
                    gestorDatos.actualizarZooBase(zooBase)

                    val dat = gestorDatos.getZooBases()
                    // Cambiar la visibilidad de las vistas para mostrar la interfaz principal
                    setContentView(R.layout.activity_main)
                    configurarBotonAgregarZoo()
                    registerForContextMenu(actualizarLista())

                    // Mostrar Snackbar de éxito
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro editado con éxito", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }

                // val elementoSeleccionado = gestorDatos.actualizarZooBase(nuevo zooBase)
                // mostrarSnackbar


                return true
            }
            R.id.menu_eliminar ->{
                val zooId = gestorDatos.getZooBases()[posicionItemSeleccionado].idZoo
                val nombre = gestorDatos.getZooBases()[posicionItemSeleccionado].nombreComun
                if (zooId != null) {
                    gestorDatos.eliminarZooBase(zooId)
                    mostrarSnackbar("Eliminando: $nombre")
                    Log.d("eliminado", "Eliminando: $zooId")
                    actualizarLista()
                    //abrirDialogo()
                } else {
                    mostrarSnackbar("No se encontró el elemento para eliminar")
                }
                return true
            }
            R.id.menu_ver -> {
                val zooId = gestorDatos.getZooBases()[posicionItemSeleccionado].idZoo

                // Crear un Intent para abrir la nueva actividad (BFauna)
                val intent = Intent(this, BFauna::class.java)

                // Adjuntar el dato (zooId) al Intent
                intent.putExtra("ZOO_ID", zooId)

                // Iniciar la nueva actividad con el Intent
                startActivity(intent)

                return true
            }
            else -> super.onContextItemSelected(item)
        }
    }


    private fun actualizarLista():ListView {
        val arreglo = gestorDatos.getZooBases()
        val listView = findViewById<ListView>(R.id.lv_list_view)
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            arreglo
        )

        listView.adapter = adaptador
        adaptador.notifyDataSetChanged()
        return listView
    }




    private fun configurarBotonAgregarZoo() {
        val botonAgregarZoo = findViewById<Button>(R.id.btn_anadir_zoo_list_view)

        botonAgregarZoo.setOnClickListener {
            // Inflar el nuevo layout (formulario_base.xml)
            val formularioBase = layoutInflater.inflate(R.layout.formulario_base, null)

            // Obtener referencias a las vistas en el nuevo layout
            val editTextNombreComun: EditText = formularioBase.findViewById(R.id.editTextNombreComun)
            val editTextNombreCientifico: EditText = formularioBase.findViewById(R.id.editTextNombreCientifico)
            val checkBoxDiurno: CheckBox = formularioBase.findViewById(R.id.checkBoxDiurno)
            val editTextPaisOriginario: EditText = formularioBase.findViewById(R.id.editTextPaisOriginario)

            val buttonGuardar: Button = formularioBase.findViewById(R.id.buttonGuardar)
            buttonGuardar.visibility = View.GONE

            val dialog = AlertDialog.Builder(this)
                .setView(formularioBase)
                .setPositiveButton("Guardar") { _, _ ->
                    // Obtener datos ingresados por el usuario
                    val nombreComun = editTextNombreComun.text.toString()
                    val nombreCientifico = editTextNombreCientifico.text.toString()
                    val diurno = checkBoxDiurno.isChecked
                    val paisOriginario = editTextPaisOriginario.text.toString()

                    val lastId = gestorDatos.obtenerUltimoId()
                    val nuevoIdZoo = lastId?.plus(1)

                    val zooBase = nuevoIdZoo?.let { id ->
                        ZooBase(id, nombreComun, nombreCientifico, diurno, paisOriginario)
                    } ?: ZooBase(null, nombreComun, nombreCientifico, diurno, paisOriginario)

                    Log.d("RegBaseZoo", zooBase.toString())

                    // guardar el nuevo elemento
                    gestorDatos.crearZooBase(zooBase)

                    // Actualizar la lista después de guardar el nuevo registro
                    actualizarLista()
                }
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.show()
        }
    }



}
