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


        setContentView(R.layout.activity_main)

        BDCompObj.CompZoologico = EFirestoreHelper()
        InitData.arregloZoologicosFauna



    }

    override fun onStart() {
        super.onStart()
        Log.i("ciclo-vida", "onStart")

        val listViewU = findViewById<ListView>(R.id.lv_list_view)
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            BDCompObj.CompZoologico!!.listarZoo()
        )
        Log.i("ciclo-vida", "${BDCompObj.CompZoologico!!.listarZoo()}")
        listViewU.adapter = adaptador
        adaptador.notifyDataSetChanged()

        this.registerForContextMenu(listViewU)

        configurarBotonAgregarZoo()

    }

    var idSelectItem = 0

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            // Guardar las variables
            // primitivos
            putInt("idItemSeleccionado",idSelectItem)
            putParcelableArrayList("arregloZoo",BDCompObj.CompZoologico!!.listarZoo())
            putParcelableArrayList("arregloPP",InitData.arregloZoologicosFauna)

        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        idSelectItem = savedInstanceState.getInt("idItemSeleccionado")

        InitData.arregloZoologicosFauna = savedInstanceState.getParcelableArrayList<ZoologicoFauna>("arregloPP")!!
        val listViewU = findViewById<ListView>(R.id.lv_list_view)
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            BDCompObj.CompZoologico!!.listarFaunas()
        )
        listViewU.adapter = adaptador
        adaptador.notifyDataSetChanged()
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



    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_editar -> {

                val formularioBase = layoutInflater.inflate(R.layout.formulario_base, null)
                setContentView(formularioBase)

                // Obtener referencias a las vistas en el nuevo layout
                val editTextNombreComun: EditText = findViewById(R.id.editTextNombreComun)
                val editTextNombreCientifico: EditText = findViewById(R.id.editTextNombreCientifico)
                val checkBoxDiurno: CheckBox = findViewById(R.id.checkBoxDiurno)
                val editTextPaisOriginario: EditText = findViewById(R.id.editTextPaisOriginario)



                BDCompObj.CompZoologico!!.listarZoo().forEachIndexed{ indice: Int, zoo: ZooBase ->
                    if(indice == posicionItemSeleccionado){

                        // Llenar el formulario con los datos existentes
                        editTextNombreComun.setText(zoo.nombreComun.toString())
                        editTextNombreCientifico.setText(zoo.nombreCientifico.toString())
                        checkBoxDiurno.isChecked = zoo.diurno==true
                        editTextPaisOriginario.setText(zoo.paisOriginario.toString())
                    }
                }

                //boton Guardar
                val buttonGuardar: Button = findViewById(R.id.buttonGuardar)


                buttonGuardar.setOnClickListener {
                    // Obtener datos ingresados por el usuario
                    val nuevoNombreComun = editTextNombreComun.text.toString()
                    val nuevoNombreCientifico = editTextNombreCientifico.text.toString()
                    val nuevoDiurno = checkBoxDiurno.isChecked
                    val nuevoPaisOriginario = editTextPaisOriginario.text.toString()

                    //actualizar datos
                    BDCompObj.CompZoologico!!.actualizarZoologico(posicionItemSeleccionado,nuevoNombreComun,nuevoNombreCientifico,nuevoDiurno,nuevoPaisOriginario)


                    // Cambiar la visibilidad de las vistas para mostrar la interfaz principal
                    setContentView(R.layout.activity_main)
                    configurarBotonAgregarZoo()

                    // Mostrar Snackbar de éxito
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro editado con éxito", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }

                // val elementoSeleccionado = gestorDatos.actualizarZooBase(nuevo zooBase)
                // mostrarSnackbar


                return true
            }
            R.id.menu_eliminar ->{

                BDCompObj.CompZoologico!!.eliminarZoo(posicionItemSeleccionado)

                val listView = findViewById<ListView>(R.id.lv_list_view)
                val adaptador = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    BDCompObj.CompZoologico!!.listarZoo()
                )
                listView.adapter = adaptador
                adaptador.notifyDataSetChanged()

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



    private fun configurarBotonAgregarZoo() {
        val botonAgregarZoo = findViewById<Button>(R.id.btn_anadir_zoo_list_view)

        botonAgregarZoo.setOnClickListener {
            // Inflar el nuevo layout (formulario_base.xml)
            val formularioBase = layoutInflater.inflate(R.layout.formulario_base, null)
            var idLast = 0

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
                    // traer info de firebase

                    val lastItemZoo = BDCompObj.CompZoologico!!.listarZoo().lastIndex
                    BDCompObj.CompZoologico!!.listarZoo().forEachIndexed{indice: Int, zoo: ZooBase ->
                        if(indice == lastItemZoo){
                            idLast = zoo.idZoo!!
                        }
                    }

                    val newIdLast = idLast.plus(1)

                    // Obtener datos ingresados por el usuario
                    val nombreComun = editTextNombreComun.text.toString()
                    val nombreCientifico = editTextNombreCientifico.text.toString()
                    val diurno = checkBoxDiurno.isChecked
                    val paisOriginario = editTextPaisOriginario.text.toString()

                    //val lastId = gestorDatos.obtenerUltimoId()
                    //val nuevoIdZoo = lastId?.plus(1)

                    /*val zooBase = nuevoIdZoo?.let { id ->
                        ZooBase(id, nombreComun, nombreCientifico, diurno, paisOriginario)
                    } ?: ZooBase(null, nombreComun, nombreCientifico, diurno, paisOriginario)*/


                    BDCompObj.CompZoologico!!.crearZoologico(newIdLast,nombreComun,nombreCientifico,
                        diurno, paisOriginario)


                }
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.show()


        }
    }



}
