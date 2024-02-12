package com.example.recycleviewspotify

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
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SearchActivity : AppCompatActivity() {

    // on below line creating variables
    private var searchQuery = ""
    private lateinit var searchEdt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        // on below line initializing variables.
        searchEdt = findViewById(R.id.idEdtSearch)
        searchQuery = intent.getStringExtra("searchQuery") ?: ""
        searchEdt.setText(searchQuery)
        // on below line adding action listener
        // for search edit text
        searchEdt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // on below line calling method to get tracks.
                getTracks(searchEdt.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        // on below line getting tracks.
        getTracks(searchQuery)
    }

    // method to get token.
    private fun getToken(): String {
        val sh: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sh.getString("token", "Not Found") ?: ""
    }

    private fun getTracks(searchQuery: String) {
        // on below line creating and initializing variables
        // for recycler view, list and adapter.
        val songsRV: RecyclerView = findViewById(R.id.idRVSongs)
        val trackRVModals = ArrayList<TrackRVModal>()
        val trackRVAdapter = TrackRVAdapter(trackRVModals, this)
        songsRV.adapter = trackRVAdapter
        // on below line creating variable for url.
        val url = "https://api.spotify.com/v1/search?q=$searchQuery&type=track"
        val queue: RequestQueue = Volley.newRequestQueue(this)
        // on below line making json object request
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val trackObj: JSONObject = response.getJSONObject("tracks")
                    val itemsArray: JSONArray = trackObj.getJSONArray("items")
                    for (i in 0 until itemsArray.length()) {
                        val itemObj: JSONObject = itemsArray.getJSONObject(i)
                        val trackName: String = itemObj.getString("name")
                        val trackArtist: String =
                            itemObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        val trackID: String = itemObj.getString("id")
                        // on below line adding data to array list
                        trackRVModals.add(TrackRVModal(trackName, trackArtist, trackID))
                    }
                    // on below line notifying adapter
                    trackRVAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@SearchActivity, "Fail to get data : $error", Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): Map<String, String> {
                // on below line adding headers.
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = getToken()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // adding json object request to queue.
        queue.add(jsonObjectRequest)
    }
}
