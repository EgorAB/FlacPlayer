package com.example.flacplayer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

class LibrarySceneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.library_scene, container, false)
        view.findViewById<ListView>(R.id.libraryListView).adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1,
            arrayOf(
                    "Избранные аудиозаписи",
                    "Сохраненные в памяти устройства",
                    "Синхронизированные с Telegram",
                    "Синхронизированные с VK"))
        return view
    }
}