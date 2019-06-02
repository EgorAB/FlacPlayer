package com.example.flacplayer

class Song(songId: Long = 0, songArtist: String = "Hello", songTitle: String = "World)") {
    var id: Long = 0
    var title: String = ""
    var artist: String = ""
    init {
        id = songId
        title = songTitle
        artist = songArtist
    }
}