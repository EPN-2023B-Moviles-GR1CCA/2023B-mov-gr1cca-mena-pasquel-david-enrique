package com.example.gr1examendemp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Leer categorías desde JSON
        val categories = readCategoriesFromJson(this)

        // Imprimir información en el Logcat
        for (category in categories) {
            Log.d("CategoryData", "Category: ${category.categoryName}")
            for (animal in category.animals) {
                Log.d("CategoryData", "  Animal: ${animal.name}, Weight: ${animal.weight}, BirthDate: ${animal.birthDate}")
            }
        }
    }

    private fun readCategoriesFromJson(context: Context): List<AnimalCategory> {
        try {
            val inputStream = context.resources.openRawResource(R.raw.categories)
            val json = inputStream.bufferedReader().use { it.readText() }
            return Gson().fromJson(json, object : TypeToken<List<AnimalCategory>>() {}.type)
        } catch (e: Exception) {
            Log.e("CategoryData", "Error reading categories from JSON: ${e.message}")
            return emptyList()
        }
    }
}
