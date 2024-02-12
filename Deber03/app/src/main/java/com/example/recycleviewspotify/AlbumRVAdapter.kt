package com.example.recycleviewspotify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AlbumRVAdapter(private val albumRVModalArrayList: ArrayList<AlbumRVModal>, private val context: Context) :
    RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating layout file on below lin

        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to text view and image view on below line.
        val albumRVModal = albumRVModalArrayList[position]
        Picasso.get().load(albumRVModal.imageUrl).into(holder.albumIV)
        holder.albumNameTV.text = albumRVModal.name
        holder.albumDetailTV.text = albumRVModal.artistName

        // adding click listener for album item on below line.
        holder.itemView.setOnClickListener {
            // on below line opening a new album detail activity for displaying songs within that album.
            val i = Intent(context, AlbumDetailActivity::class.java)
            // on below line passing album related data.
            i.putExtra("id", albumRVModal.id)
            i.putExtra("name", albumRVModal.name)
            i.putExtra("img", albumRVModal.imageUrl)
            i.putExtra("artist", albumRVModal.artistName)
            i.putExtra("albumUrl", albumRVModal.external_urls)
            context.startActivity(i)
        }
    }

    // on below line returning the size of list
    override fun getItemCount(): Int {
        return albumRVModalArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // on below line creating variables for image view and text view.
        var albumIV: ImageView = itemView.findViewById(R.id.idIVAlbum)
        var albumNameTV: TextView = itemView.findViewById(R.id.idTVAlbumName)
        var albumDetailTV: TextView = itemView.findViewById(R.id.idTVALbumDetails)
    }
}
