package com.example.flacplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class PlaylistFragment : Fragment() {

    // TODO: 1. Реализовать открытие меню поиска свайпом и непосредственно поиск песни в плейлисте

    interface PlaylistInteractionListener {
        fun initPlayer(song: Song)
        fun play()
        fun pause()
        fun playing(): Boolean
    }

    var mListener: PlaylistInteractionListener? = null
    var songList: ArrayList<Song> = arrayListOf()
    var songViews: ArrayList<View> = arrayListOf()
    var playlist: LinearLayout? = null

    var currentSongView: View? = null

    val selectedItems: ArrayList<View> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.playlist, container, false)
        playlist = view.findViewById(R.id.playlist)
        songList = (context as MainActivity).songList
        fill()
        selectSong(currentSongView)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = context as PlaylistInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context должен реализовывать интерфейс PlaylistInteractionListener")
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun deselectSong(songV: View?) {
        if (songV == null) return
        songV.findViewWithTag<TextView>("artist").setTextColor(resources.getColor(R.color.materialLightGray))
        songV.findViewWithTag<TextView>("title").setTextColor(resources.getColor(R.color.materialDarkGray))
    }

    private fun selectSong(songV: View?) {
        if (songV == null) return
        deselectSong(currentSongView)
        songV.findViewWithTag<TextView>("artist").setTextColor(resources.getColor(R.color.darkThemeColorPrimary))
        songV.findViewWithTag<TextView>("title").setTextColor(resources.getColor(R.color.darkThemeColorPrimaryDark))
        currentSongView = songV
    }

    private fun songFromView(v: View?): Song {
        if (v == null) return songList[0]
        if(v in songViews) return songList[songViews.indexOf(v)]
        return songList[0]
    }

    // вернет песню, если она первая в списке, иначе предыдущую
    public fun prevSong(select: Boolean = false): Song {
        val song: Song = songFromView(currentSongView)
        if (song == songList[0]) return songList[0]
        var index = 0
        for (i in 1 until songList.size)
            if (song == songList[i]) index = i - 1
        if (select) selectSong(songViews[index])
        return songList[index]
    }

    // вернет null, если текущая песня последняя в списке
    public fun nextSong(select: Boolean = false): Song? {
        val song: Song = songFromView(currentSongView)
        if (song == songList.last()) return null
        var index = 0
        for (i in 0 until songList.size - 1)
            if (song == songList[i]) index = i + 1
        if (select) selectSong(songViews[index])
        return songList[index]
    }

    // очистка объектов, составляющих плейлист
    private fun clear() {
        songViews.forEach {
            playlist?.removeView(it)
        }
        songViews.clear()
    }

    // заполнение плейлиста треками
    private fun fill(content: ArrayList<Song>? = null) {
        if (content == null) fill(songList)
        content?.forEach {
            val v = LayoutInflater.from(this.context).inflate(R.layout.song, null, false)
            playlist?.addView(v)
            songViews.add(v)
            v.findViewWithTag<TextView>("title").text = it.title
            v.findViewWithTag<TextView>("artist").text = it.artist
            if (it.coverUri != null) v.findViewWithTag<ImageView>("cover").setImageURI(Uri.parse(it.coverUri))
            v.setOnClickListener {
                if (selectedItems.size == 0) {
                    val s = songFromView(v)
                    if (mListener != null) {
                        if (v == currentSongView) {
                            if (mListener!!.playing()) mListener!!.pause()
                            else mListener!!.play()
                        } else {
                            mListener?.initPlayer(s)
                            deselectSong(currentSongView)
                            selectSong(v)
                            currentSongView = v
                        }
                    }
                } else {
                    if(v in selectedItems) {
                        selectedItems.remove(v)
                        if(songFromView(v).coverUri == null)
                            v.findViewWithTag<ImageView>("cover")
                                .setImageDrawable(resources.getDrawable(R.drawable.ic_corgi_icon_final))
                        else v.findViewWithTag<ImageView>("cover").setImageURI(Uri.parse(songFromView(v).coverUri))
                        if(selectedItems.size == 0) (activity as MainActivity).hidePISBar()
                    }
                    else {
                        selectedItems.add(v)
                        v.findViewWithTag<ImageView>("cover")
                            .setImageDrawable(resources.getDrawable(R.drawable.ic_done_black_24dp))
                    }
                }
            }
            val s = it
            v.setOnLongClickListener {
                (activity as MainActivity).showPISBar()
                v.findViewWithTag<ImageView>("cover")
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_done_black_24dp))
                if(v in selectedItems) {
                    selectedItems.remove(v)
                    if(songFromView(v).coverUri == null)
                        v.findViewWithTag<ImageView>("cover")
                            .setImageDrawable(resources.getDrawable(R.drawable.ic_corgi_icon_final))
                    else v.findViewWithTag<ImageView>("cover").setImageURI(Uri.parse(songFromView(v).coverUri))
                    if(selectedItems.size == 0) (activity as MainActivity).hidePISBar()
                }
                else {
                    selectedItems.add(v)
                    v.findViewWithTag<ImageView>("cover")
                        .setImageDrawable(resources.getDrawable(R.drawable.ic_done_black_24dp))
                }
                true
            }
        }
    }

    public fun PISBarCloseButton(){
        for(v in selectedItems){
            if(songFromView(v).coverUri == null)
                v.findViewWithTag<ImageView>("cover")
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_corgi_icon_final))
            else v.findViewWithTag<ImageView>("cover").setImageURI(Uri.parse(songFromView(v).coverUri))
        }
        selectedItems.clear()
        (activity as MainActivity).hidePISBar()
    }

    public fun PISBarDeleteButton(){
        for(v in selectedItems){
            songViews.remove(v)
            songList.remove(songFromView(v))
            playlist?.removeView(v)
        }
        selectedItems.clear()
        (activity as MainActivity).hidePISBar()
    }

    // обновление плейлиста
    public fun update(content: ArrayList<Song>? = null) {
        clear()
        fill(content)
    }
}