package com.example.recycleviewspotify

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlbumDetailActivity : AppCompatActivity() {

    // creating variables on below line.
    private var albumID = ""
    private var albumImgUrl: String? = null
    private var albumName: String? = null
    private var artist: String? = null
    private var albumUrl: String? = null

    private lateinit var albumNameTV: TextView
    private lateinit var artistTV: TextView
    private lateinit var albumIV: ImageView
    private lateinit var playFAB: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initializing variables on below line.
        setContentView(R.layout.activity_album_detail)
        albumID = intent.getStringExtra("id") ?: ""
        albumIV = findViewById(R.id.idIVAlbum)
        albumImgUrl = intent.getStringExtra("img")
        albumName = intent.getStringExtra("name")
        artist = intent.getStringExtra("artist")
        albumUrl = intent.getStringExtra("albumUrl")
        Log.e("TAG", "album id is : $albumID")
        albumNameTV = findViewById(R.id.idTVAlbumName)
        playFAB = findViewById(R.id.idFABPlay)
        artistTV = findViewById(R.id.idTVArtistName)
        // setting data on below line.
        albumNameTV.text = albumName
        artistTV.text = artist
        // adding click listener for fab on below line.
        playFAB.setOnClickListener {
            // opening album from url on below line.
            val uri = Uri.parse(albumUrl) // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        // loading image on below line.
        Picasso.get().load(albumImgUrl).into(albumIV)
        // getting album tracks on below line.
        getAlbumTracks(albumID)
    }

    // method to get access token.
    private fun getToken(): String {
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sh.getString("token", "Not Found") ?: ""
    }

    // method to get tracks from albums.
    private fun getAlbumTracks(albumID: String) {
        // on below line creating variable for url
        val url = "https://api.spotify.com/v1/albums/$albumID/tracks"
        // on below line creating list, initializing adapter and setting it to recycler view.
        val trackRVModals = ArrayList<TrackRVModal>()
        val trackRVAdapter = TrackRVAdapter(trackRVModals, this)
        val trackRV: RecyclerView = findViewById(R.id.idRVTracks)
        trackRV.adapter = trackRVAdapter
        val queue: RequestQueue = Volley.newRequestQueue(this)
        // on below line making json object request to parse json data.
        val trackObj = object : JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val itemsArray: JSONArray = response.getJSONArray("items")
                    for (i in 0 until itemsArray.length()) {
                        val itemObj: JSONObject = itemsArray.getJSONObject(i)
                        val trackName: String = itemObj.getString("name")
                        val id: String = itemObj.getString("id")
                        val trackArtist: String =
                            itemObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        // on below line adding data to array list.
                        trackRVModals.add(TrackRVModal(trackName, trackArtist, id))
                    }
                    trackRVAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@AlbumDetailActivity, "Fail to get Tracks$error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                // on below line passing headers.
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = getToken()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        // on below line adding
        // request to queue.
        queue.add(trackObj)
    }
}