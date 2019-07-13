package com.example.flacplayer

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
class Song(songId: Long = 0, songArtist: String = "Hello", songTitle: String = "World)", songCover: Bitmap? = null) {
    var id: Long = 0
    var title: String = ""
    var artist: String = ""
    var cover: Bitmap? = null
    init {
        id = songId
        title = songTitle
        artist = songArtist
        if(songCover != null) cover = songCover
    }
}