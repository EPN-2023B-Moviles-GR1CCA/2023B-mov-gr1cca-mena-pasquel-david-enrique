package com.example.gr1comproyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import android.view.View

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val tabLayout: LinearLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // No necesitas implementar este método, pero puedes hacerlo si necesitas realizar acciones mientras se desplaza entre las páginas.
            }

            override fun onPageSelected(position: Int) {
                // Cuando se selecciona una página, cambia el color de los botones según la posición de la página.
                updateButtonColors(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // No necesitas implementar este método, pero puedes hacerlo si necesitas realizar acciones mientras cambia el estado del desplazamiento.
            }
        })

        findViewById<Button>(R.id.btnTab1).setOnClickListener {
            viewPager.currentItem = 0
        }

        findViewById<Button>(R.id.btnTab2).setOnClickListener {
            viewPager.currentItem = 1
        }

        findViewById<Button>(R.id.btnTab3).setOnClickListener {
            viewPager.currentItem = 2
        }

    }
    fun onTabButtonClick(view: View) {
        val viewPager: ViewPager = findViewById(R.id.viewPager)

        when (view.id) {
            R.id.btnTab1 -> viewPager.currentItem = 0
            R.id.btnTab2 -> viewPager.currentItem = 1
            R.id.btnTab3 -> viewPager.currentItem = 2
        }
    }



    private fun updateButtonColors(selectedPosition: Int) {
        val btnTab1: Button = findViewById(R.id.btnTab1)
        val btnTab2: Button = findViewById(R.id.btnTab2)
        val btnTab3: Button = findViewById(R.id.btnTab3)

        btnTab1.setBackgroundResource(R.drawable.boton_design)
        btnTab2.setBackgroundResource(R.drawable.boton_design)
        btnTab3.setBackgroundResource(R.drawable.boton_design)

        when (selectedPosition) {
            0 -> btnTab1.setBackgroundColor(resources.getColor(R.color.colorSelectedTab))
            1 -> btnTab2.setBackgroundColor(resources.getColor(R.color.colorSelectedTab))
            2 -> btnTab3.setBackgroundColor(resources.getColor(R.color.colorSelectedTab))
        }
    }

}