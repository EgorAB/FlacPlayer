package com.example.flacplayer

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class PlaylistFragment : Fragment() {

    // TODO: 1. Реализовать открытие меню поиска свайпом и непосредственно поиск песни в плейлисте

    interface PlaylistInteractionListener{
        fun initPlayer(song: Song)
        fun play()
        fun pause()
        fun mediaPlayerPlaying(): Boolean
    }

    var mListener: PlaylistInteractionListener? = null
    var songList: ArrayList<Song> = arrayListOf()
    var songViews: ArrayList<View> = arrayListOf()
    var playlist: LinearLayout? = null
    var currentSongIndex: Int = 0
        get() = if(field >= 0) if(field >= songList.size) 0 else field else songList.size - 1
        set(value){
            field = if (value >= 0) if (value >= songList.size) 0 else value else songList.size - 1
        }

    var currentSongView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view: View = inflater.inflate(R.layout.playlist, container, false)
        playlist = view.findViewById(R.id.playlist)
        getSongList(view)
        update()
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = context as PlaylistInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " должен реализовывать интерфейс OnFragmentInteractionListener")
        }
    }

    override fun onPause(){
        super.onPause()
        clear()
    }

    override fun onResume(){
        super.onResume()
        update()
    }

    // заполнение songList треками с устройства
    private fun getSongList(view: View){
        if(ContextCompat.checkSelfPermission(view.context, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) return
        val musicResolver: ContentResolver = context!!.contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            val data = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val coverColumn: Int = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART)
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisData = musicCursor.getString(data)
                songList.add(Song(thisId, thisArtist, thisTitle, Uri.parse("file:///$thisData")))
            } while (musicCursor.moveToNext())
        }
        musicCursor?.close()
        // songListView?.adapter = SongAdapter(this.context!!, songList)
    }

    private fun deselectSong(song: View?){
        if(song == null) return
        song.findViewWithTag<TextView>("artist").setTextColor(resources.getColor(R.color.materialLightGray))
        song.findViewWithTag<TextView>("title").setTextColor(resources.getColor(R.color.materialDarkGray))
    }

    private fun selectSong(song: View?){
        if(song == null) return
        song.findViewWithTag<TextView>("artist").setTextColor(resources.getColor(R.color.darkThemeColorPrimary))
        song.findViewWithTag<TextView>("title").setTextColor(resources.getColor(R.color.darkThemeColorPrimaryDark))
    }

    private fun songFromView(v: View?): Song {
        if(v == null) return songList[0]
        for(i in 0 until songViews.size)
            if(songViews[i] == v) return songList[i]
        return songList[0]
    }

    // вернет null если текущая песня последняя в списке
    public fun nextSong(): Song? {
        val song: Song? = songFromView(currentSongView)
        if(song == songList.last()) return null
        for(i in 0 until songList.size - 1)
            if(song == songList[i]) return songList[i + 1]
        return songList[0]
    }

    // очистка объектов, составляющих плейлист
    private fun clear(){
        songViews.forEach{
            playlist?.removeView(it)
        }
        songViews.clear()
    }

    // заполнение плейлиста треками
    private fun fill(content: ArrayList<Song>? = null){
        if(content == null) fill(songList)
        content?.forEach {
            val v = LayoutInflater.from(this.context).inflate(R.layout.song, null, false)
            playlist?.addView(v)
            songViews.add(v)
            v.findViewWithTag<TextView>("title").text = it.title
            v.findViewWithTag<TextView>("artist").text = it.artist
            if(it.cover != null) v.findViewWithTag<ImageView>("icon").setImageBitmap(it.cover)
            v.setOnClickListener{
                val s = songFromView(v)
                if(mListener != null) {
                    if (v == currentSongView) {
                        if(mListener!!.mediaPlayerPlaying()) mListener!!.pause()
                        else mListener!!.play()
                    }
                    else {
                        mListener?.initPlayer(s)
                        deselectSong(currentSongView)
                        selectSong(v)
                        currentSongView = v
                    }
                }
                Log.d("DEBUG", "Now playing: ${s.artist} - ${s.title}")
            }
            val s = it
            v.setOnLongClickListener {
                AlertDialog.Builder(this.context).run{
                    setTitle("Вы уверены, что хотите удалить трек ${s.artist} — ${s.title}?")
                    setPositiveButton("Да") { _, _ ->
                        songViews.remove(v)
                        songList.remove(s)
                        playlist?.removeView(v)
                    }
                    setNegativeButton("Нет") { _, _ -> {} }
                }.create().show()
                true
            }
        }
    }

    // обновление плейлиста
    public fun update(content: ArrayList<Song>? = null){
        clear()
        fill(content)
    }
}