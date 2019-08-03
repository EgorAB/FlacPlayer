package com.example.flacplayer

import android.app.AlertDialog
import android.content.ContentResolver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayInputStream


class PlaylistSceneFragment : Fragment() {

    var songList: ArrayList<Song> = arrayListOf()
    private var songListView: ListView? = null
    private var playSceneFragment: PlaylistSceneFragment? = null
    private val mListener: OnFragmentInteractionListener? = null
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(id: Long)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playlist_scene, container, false)
        songListView = view.findViewById(R.id.songListView)
        val songAdapter = SongAdapter(this.context!!, songList)
        if(songList.size != 0) {
            songListView?.adapter = songAdapter
            view.findViewById<Button>(R.id.openFolderButton).visibility = View.INVISIBLE
        }

        songListView?.setOnItemClickListener { parent, v, position, id ->
            Log.d("DEBUG", songList[position].id.toString())
        }
        songListView?.setOnItemLongClickListener { parent, v, position, id ->
            AlertDialog.Builder(this.context).run{
                setTitle("Вы уверены, что хотите удалить трек ${songList[position].artist} — ${songList[position].title}?")
                setPositiveButton("Да") { _, _ ->
                    songAdapter.notifyDataSetChanged()
                }
                setNegativeButton("Нет") { _, _ -> {} }
            }.create().show()
            true
        }

        getSongList(view)
        if(ContextCompat.checkSelfPermission(view.context, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED) view.findViewById<Button>(R.id.openFolderButton).visibility = View.INVISIBLE
        return view
    }
    private fun getSongList(view: View){
        if(ContextCompat.checkSelfPermission(view.context, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) return
        val musicResolver:ContentResolver = context!!.contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val coverColumn: Int = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART)
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                songList.add(Song(thisId, thisArtist, thisTitle))
            } while (musicCursor.moveToNext())
        }
        musicCursor?.close()
        songListView?.adapter = SongAdapter(this.context!!, songList)
    }
    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        val arrayInputStream = ByteArrayInputStream(byteArray)
        return BitmapFactory.decodeStream(arrayInputStream)
    }
}