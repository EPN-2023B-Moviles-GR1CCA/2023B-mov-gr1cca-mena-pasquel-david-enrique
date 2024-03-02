package com.example.gr1examendemp

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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

        listViewU = findViewById(R.id.lv_fauna)

    }


    override fun onStart() {
        super.onStart()
        Log.i("ciclo-vida", "onStart")


        BDCompObj.CompZoologico?.listarZoo { lista ->
            datoZoo = intent.getIntExtra("ZOO_ID",1)
            Log.d("datoZOO: ", "${datoZoo}")
            lista.forEachIndexed { indice: Int, zoo: ZooBase ->
                if (datoZoo == zoo.idZoo) {
                    val titleZoo = findViewById<TextView>(R.id.buttonToZoo)
                    titleZoo.text = zoo.nombreComun
                }
            }
        }

        listViewFauna()

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
        val listaIDFaunas = arrayListOf<Int>()
        val faunaLists = arrayListOf<FaunaBase>()


        BDCompObj.CompZoologico!!.listarZoo { lista ->
            Log.d("datoZOO2: ", "${datoZoo}")
            lista.forEachIndexed{ indice: Int, zoo:ZooBase ->
                if(datoZoo == zoo.idZoo){
                    idZoologico = zoo.idZoo
                    Log.d("idZoologico: ", "${idZoologico}")
                }
            }

            InitData.arregloZoologicosFauna.forEachIndexed{ indice: Int, pp: ZoologicoFauna ->
                if(pp.idZoo == idZoologico){
                    listaIDFaunas.add(pp.idFauna)
                }
            }
            Log.d("faunasIdentificadores: ", "${listaIDFaunas}")

            BDCompObj.CompZoologico?.listarFaunas { listaFaunas ->
                listaFaunas.forEachIndexed { index: Int, fauna: FaunaBase ->

                    for(i in listaIDFaunas){
                        Log.d("faunasId: ", "${i}")
                        if(i==fauna.idAnimal){
                            Log.d("faunas: ", "${fauna}")
                            faunaLists.add(fauna)
                        }
                    }

                }

                val listViewFacultad = findViewById<ListView>(R.id.lv_fauna)
                val adaptador = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    faunaLists
                )
                listViewFacultad.adapter = adaptador
                adaptador.notifyDataSetChanged()

                // Registrar el ListView para el menú contextual
                registerForContextMenu(listViewFacultad)
            }


        }





    }


    var posicionItemSeleccionado = 0
    override fun onCreateContextMenu(menu: ContextMenu?,
                                     v: View?,
                                     menuInfo: ContextMenu.ContextMenuInfo?)
    {
        super.onCreateContextMenu(menu, v, menuInfo)
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
                val formularioFauna = layoutInflater.inflate(R.layout.formulario_fauna, null)
                setContentView(formularioFauna)

                // Obtener referencias a las vistas en el nuevo layout
                val editTextNombreNacimiento: EditText = findViewById(R.id.editTextNombreNacimiento)
                val editTextPeso: EditText = findViewById(R.id.editTextPeso)
                val editTextFechaNacimiento: EditText = findViewById(R.id.editTextFechaNacimiento)
                val buttonGuardar: Button = findViewById(R.id.buttonGuardarFauna)

                BDCompObj.CompZoologico?.listarFaunas { lista ->
                    lista.forEachIndexed { index: Int, fauna: FaunaBase ->
                        if (posicionItemSeleccionado == fauna.idAnimal) {
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


                    if (nuevoPeso != null) {
                        BDCompObj.CompZoologico!!.actualizarFacultad(posicionItemSeleccionado, nuevoNombreNacimiento, nuevoPeso, nuevoFechaNacimiento)
                    }

                    // Cambiar la visibilidad de las vistas para mostrar la interfaz principal
                    setContentView(R.layout.activity_b_fauna)
                    configurarBotonAgregarFauna()
                    listViewFauna()

                    // Mostrar Snackbar de éxito
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro editado con éxito", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }


                return true
            }
            R.id.menu_eliminar ->{
                BDCompObj.CompZoologico!!.eliminarFauna(posicionItemSeleccionado)
                listViewFauna()
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

            var IndexZoo = 0
            var lastFauna = 0
            var lastIdZooFa = 0

            val dialog = AlertDialog.Builder(this)
                .setView(formularioBase)
                .setPositiveButton("Guardar") { _, _ ->


                    BDCompObj.CompZoologico?.listarZoo { lista ->
                        lista.forEachIndexed { indice: Int, zoo: ZooBase ->
                            if (indice == datoZoo) {
                                IndexZoo = zoo.idZoo!!
                            }
                        }
                    }

                    BDCompObj.CompZoologico?.listarFaunas { lista ->
                        var idUltimaFauna =0
                        if (lista.isNotEmpty()) {
                            val ultimaFauna = lista.last()
                            idUltimaFauna = ultimaFauna.idAnimal!!
                        }

                        val plusIDFauna = idUltimaFauna.plus(1)

                        var longPP = InitData.arregloZoologicosFauna.lastIndex
                        InitData.arregloZoologicosFauna.forEachIndexed { indice: Int, zooFaunas: ZoologicoFauna ->
                            if(indice==longPP)
                                lastIdZooFa = zooFaunas.idZooFauna
                        }
                        val plusLastIdZooFa = lastIdZooFa.plus(1)

                        // Obtener datos ingresados por el usuario
                        val nuevoNombreNacimiento = TextNombreNacimiento.text.toString()
                        val nuevoPeso = TextPeso.text.toString().toDoubleOrNull()
                        val nuevoFechaNacimiento = TextFechaNacimiento.text.toString()

                        if (nuevoPeso != null) {
                            BDCompObj.CompZoologico!!.crearFauna(plusIDFauna,nuevoNombreNacimiento,nuevoPeso,nuevoNombreNacimiento)
                        }


                        BDCompObj.CompZoologico?.listarZoo { lista ->
                            lista.forEachIndexed { indice: Int, zoo: ZooBase ->
                                if (datoZoo == datoZoo) {
                                    idZoologico = zoo.idZoo!!
                                }
                            }
                            InitData.arregloZoologicosFauna.add(ZoologicoFauna(plusLastIdZooFa,idZoologico, plusIDFauna))
                        }
                    }
                    listViewFauna()
                }
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.show()
        }
    }
    
}