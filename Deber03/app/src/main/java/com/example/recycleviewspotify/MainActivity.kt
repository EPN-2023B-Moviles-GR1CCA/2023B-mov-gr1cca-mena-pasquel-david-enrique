package com.example.recycleviewspotify

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // en la línea de abajo llamando al método para
        // cargar nuestros recycler views y search view.
        initializeAlbumsRV()
        initializePopularAlbumsRV()
        initializeTrendingAlbumsRV()
        initializeSearchView()
    }

    // el método siguiente se usa para inicializar la vista de búsqueda.
    private fun initializeSearchView() {
        // en la línea de abajo inicializando nuestro
        // edit text para las vistas de búsqueda.
        val searchEdt = findViewById<EditText>(R.id.idEdtSearch)
        searchEdt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // en la línea de abajo llamando al método para buscar pistas.
                searchTracks(searchEdt.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

    // el método siguiente se usa para abrir la actividad de búsqueda de pistas.
    private fun searchTracks(searchQuery: String) {
        // en la línea de abajo abriendo la actividad de búsqueda para
        // mostrar los resultados de la búsqueda en la actividad de búsqueda.
        val i = Intent(this@MainActivity, SearchActivity::class.java)
        i.putExtra("searchQuery", searchQuery)
        startActivity(i)
    }

    // el método siguiente se usa para obtener el token.
    private fun getToken(): String {
        // en la línea de abajo obteniendo el token de las preferencias compartidas.
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sh.getString("token", "Not Found") ?: "Not Found"
    }

    override fun onStart() {
        super.onStart()
        // en la línea de abajo llamando al método para generar
        // el token.
        generateToken()
    }

    private fun generateToken() {
        // en la línea de abajo creando una variable para
        // la URL para generar el token de acceso.
        val url = "https://accounts.spotify.com/api/token?grant_type=client_credentials"
        val queue = Volley.newRequestQueue(this@MainActivity)
        // en la línea de abajo haciendo una solicitud de cadena para generar el token de acceso.
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    // en la línea de abajo obteniendo el token de acceso y
                    // guardándolo en las preferencias compartidas.
                    val tk = jsonObject.getString("access_token")
                    val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                    val myEdit = sharedPreferences.edit()
                    myEdit.putString("token", "Bearer $tk")
                    myEdit.apply()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // método para manejar errores.
                Toast.makeText(this@MainActivity, "Error al obtener respuesta = $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                // en la línea de abajo pasando encabezados.
                // Asegúrate de agregar tu autorización.
                headers["Authorization"] = "Basic MGQ5MDE4MmYwYjUzNDA4MDhmMTM3MTYwMTBiNDc4YWM6NzgzMWVkNDQ3OTcwNDMxMzlkOTA3MDFmZDhlN2M2YzA="
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }
        // agregando la solicitud a la cola.
        queue.add(request)
    }

    private fun initializeAlbumsRV() {
        // en la línea de abajo inicializando albums rv
        val albumsRV = findViewById<RecyclerView>(R.id.idRVAlbums)
        // en la línea de abajo creando una lista, inicializando el adaptador
        // y estableciéndolo en la vista de reciclaje.
        val albumRVModalArrayList = ArrayList<AlbumRVModal>()
        val albumRVAdapter = AlbumRVAdapter(albumRVModalArrayList, this)
        albumsRV.adapter = albumRVAdapter
        // en la línea de abajo creando una variable para la URL
        val url = "https://api.spotify.com/v1/albums?ids=2oZSF17FtHQ9sYBscQXoBe%2C0z7bJ6UpjUw8U4TATtc5Ku%2C36UJ90D0e295TvlU109Xvy%2C3uuu6u13U0KeVQsZ3CZKK4%2C45ZIondgVoMB84MQQaUo9T%2C15CyNDuGY5fsG0Hn9rjnpG%2C1HeX4SmCFW4EPHQDvHgrVS%2C6mCDTT1XGTf48p6FkK9qFL"
        val queue = Volley.newRequestQueue(this@MainActivity)
        // en la línea de abajo haciendo una solicitud de objeto JSON para analizar los datos JSON.
        val albumObjReq = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val albumArray = response.getJSONArray("albums")
                    for (i in 0 until albumArray.length()) {
                        val albumObj = albumArray.getJSONObject(i)
                        val album_type = albumObj.getString("album_type")
                        val artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        val external_ids = albumObj.getJSONObject("external_ids").getString("upc")
                        val external_urls = albumObj.getJSONObject("external_urls").getString("spotify")
                        val href = albumObj.getString("href")
                        val id = albumObj.getString("id")
                        val imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url")
                        val label = albumObj.getString("label")
                        val name = albumObj.getString("name")
                        val popularity = albumObj.getInt("popularity")
                        val release_date = albumObj.getString("release_date")
                        val total_tracks = albumObj.getInt("total_tracks")
                        val type = albumObj.getString("type")
                        // en la línea de abajo agregando datos a la lista.
                        albumRVModalArrayList.add(AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type))
                    }
                    albumRVAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@MainActivity, "Error al obtener datos: $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // en la línea de abajo pasando encabezados.
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = getToken()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // en la línea de abajo agregando la solicitud a la cola.
        queue.add(albumObjReq)
    }

    private fun initializePopularAlbumsRV() {
        // en la línea de abajo creando una lista, inicializando
        // el adaptador y estableciéndolo en la vista de reciclaje.
        val albumsRV = findViewById<RecyclerView>(R.id.idRVPopularAlbums)
        val albumRVModalArrayList = ArrayList<AlbumRVModal>()
        val albumRVAdapter = AlbumRVAdapter(albumRVModalArrayList, this)
        albumsRV.adapter = albumRVAdapter
        // en la línea de abajo creando una variable para la URL
        val url = "https://api.spotify.com/v1/albums?ids=0sjyZypccO1vyihqaAkdt3%2C17vZRWjKOX7TmMktjQL2Qx%2C7lF34sP6HtRAL7VEMvYHff%2C2zXKlf81VmDHIMtQe3oD0r%2C7Gws1vUsWltRs58x8QuYVQ%2C7uftfPn8f7lwtRLUrEVRYM%2C7kSY0fqrPep5vcwOb1juye"
        val queue = Volley.newRequestQueue(this@MainActivity)
        // en la línea de abajo haciendo una solicitud de objeto JSON para analizar los datos JSON.
        val albumObjReq = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val albumArray = response.getJSONArray("albums")
                    for (i in 0 until albumArray.length()) {
                        val albumObj = albumArray.getJSONObject(i)
                        val album_type = albumObj.getString("album_type")
                        val artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        val external_ids = albumObj.getJSONObject("external_ids").getString("upc")
                        val external_urls = albumObj.getJSONObject("external_urls").getString("spotify")
                        val href = albumObj.getString("href")
                        val id = albumObj.getString("id")
                        val imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url")
                        val label = albumObj.getString("label")
                        val name = albumObj.getString("name")
                        val popularity = albumObj.getInt("popularity")
                        val release_date = albumObj.getString("release_date")
                        val total_tracks = albumObj.getInt("total_tracks")
                        val type = albumObj.getString("type")
                        // en la línea de abajo agregando datos a la lista.
                        albumRVModalArrayList.add(AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type))
                    }
                    albumRVAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@MainActivity, "Error al obtener datos : $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // en la línea de abajo pasando encabezados.
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = getToken()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // en la línea de abajo agregando la solicitud a la cola.
        queue.add(albumObjReq)
    }

    private fun initializeTrendingAlbumsRV() {
        // en la línea de abajo creando una lista, inicializando el adaptador
        // y estableciéndolo en la vista de reciclaje.
        val albumsRV = findViewById<RecyclerView>(R.id.idRVTrendingAlbums)
        val albumRVModalArrayList = ArrayList<AlbumRVModal>()
        val albumRVAdapter = AlbumRVAdapter(albumRVModalArrayList, this)
        albumsRV.adapter = albumRVAdapter
        // en la línea de abajo creando una variable para la URL
        val url = "https://api.spotify.com/v1/albums?ids=1P4eCx5b11Tfmi4s1GmWmQ%2C2SsEtiB6yJYn8hRRAmtVda%2C7hhxms8KCwlQCWffIJpN9b%2C3umvKIjsD484pa9pCyPK2x%2C3OHC6XD29wXWADtAOP2geV%2C3RZxrS2dDZlbsYtMRM89v8%2C24C47633GRlozws7WBth7t"
        val queue = Volley.newRequestQueue(this@MainActivity)
        // en la línea de abajo haciendo una solicitud de objeto JSON para analizar los datos JSON.
        val albumObjReq = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val albumArray = response.getJSONArray("albums")
                    for (i in 0 until albumArray.length()) {
                        val albumObj = albumArray.getJSONObject(i)
                        val album_type = albumObj.getString("album_type")
                        val artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        val external_ids = albumObj.getJSONObject("external_ids").getString("upc")
                        val external_urls = albumObj.getJSONObject("external_urls").getString("spotify")
                        val href = albumObj.getString("href")
                        val id = albumObj.getString("id")
                        val imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url")
                        val label = albumObj.getString("label")
                        val name = albumObj.getString("name")
                        val popularity = albumObj.getInt("popularity")
                        val release_date = albumObj.getString("release_date")
                        val total_tracks = albumObj.getInt("total_tracks")
                        val type = albumObj.getString("type")
                        // en la línea de abajo agregando datos a la lista.
                        albumRVModalArrayList.add(AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type))
                    }
                    albumRVAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@MainActivity, "Error al obtener datos : $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // en la línea de abajo pasando encabezados.
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = getToken()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // en la línea de abajo agregando la solicitud a la cola.
        queue.add(albumObjReq)
    }
}


