package com.example.flacplayer

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

class Song(songId: Long = 0, songArtist: String = "Hello", songTitle: String = "World)", songUri: Uri? = null, songCover: Bitmap? = null) {
    var uri: Uri? = null
    var id: Long = 0
    var title: String = ""
    var artist: String = ""
    var cover: Bitmap? = null

    init {
        id = songId
        title = songTitle
        artist = songArtist
        if(songUri != null) uri = songUri
        if(songCover != null) cover = songCover
    }
}