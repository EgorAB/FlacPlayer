package com.example.flacplayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class SongAdapter(c: Context, theSongs: ArrayList<Song>) : BaseAdapter() {
    private var songs: ArrayList<Song> = theSongs
    private var songInflater: LayoutInflater? = null

    init {
        songInflater = LayoutInflater.from(c)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val songLay: ConstraintLayout = songInflater?.inflate(R.layout.song, parent, false) as ConstraintLayout
        val songView: TextView = songLay.findViewById(R.id.song_title)
        val artistView: TextView = songLay.findViewById(R.id.song_artist)
        val songCover: ImageView = songLay.findViewById(R.id.song_cover)
        val currSong: Song = songs[position]
        songView.text =  currSong.title
        artistView.text = currSong.artist
        if(currSong.coverUri != null) songCover.setImageURI(Uri.parse(currSong.coverUri))
        songLay.tag = position
        return songLay
    }

    override fun getItem(position: Int): Any {
        return songs[position]
    }

    override fun getItemId(position: Int): Long {
        return songs[position].id
    }

    override fun getCount(): Int {
        return songs.size
    }

}