package com.example.gr1examendemp

class InitData {

    companion object{


        var arregloZoologicosFauna = arrayListOf<ZoologicoFauna>()

        init {


            // Zoologicos
            BDCompObj.CompZoologico!!.crearZoologico(1, "León", "Panthera leo", true, "África")
            BDCompObj.CompZoologico!!.crearZoologico(2, "Panda", "Ailuropoda melanoleuca", true, "China")



        }


    }

}