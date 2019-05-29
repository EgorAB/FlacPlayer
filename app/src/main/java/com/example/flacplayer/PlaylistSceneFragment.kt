package com.example.flacplayer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

class PlaylistSceneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.playlist_scene, container, false)
        view.findViewById<ListView>(R.id.playlistListView).adapter =
            ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1,
                arrayOf(
                    "Pixel Terror - Amnesia",
                    "Bandlez - Together Again",
                    "ADEZIA - Precipitate",
                    "Au5 & Fractal - Pavonine (Original Mix)",
                    "Direct feat. Holly Drummond - Memory",
                    "Stonebank - The Pressure",
                    "Slips & Slurs - Divided",
                    "Virtual Riot - With You",
                    "Illenium - Leaving",
                    "Porter Robinson - Divinity",
                    "Au5 - Depths Of Ice (Original Mix)",
                    "Trivecta feat. Aloma Steele - Evaporate",
                    "Asking Alexandria - Alone In a Room",
                    "Crywolf  - Shrike",
                    "Infected Mushroom - Guitarmass",
                    "Muse - Algorithm",
                    "ODESZA - Boy",
                    "The Neighbourhood - Afraid (Album Version)",
                    "Au5 - Zero (Original Mix)",
                    "San Holo - Light",
                    "Pixel Terror - Amnesia",
                    "Bandlez - Together Again",
                    "ADEZIA - Precipitate",
                    "Au5 & Fractal - Pavonine (Original Mix)",
                    "Direct feat. Holly Drummond - Memory",
                    "Stonebank - The Pressure",
                    "Slips & Slurs - Divided",
                    "Virtual Riot - With You",
                    "Illenium - Leaving",
                    "Porter Robinson - Divinity",
                    "Au5 - Depths Of Ice (Original Mix)",
                    "Trivecta feat. Aloma Steele - Evaporate",
                    "Asking Alexandria - Alone In a Room",
                    "Crywolf  - Shrike",
                    "Infected Mushroom - Guitarmass",
                    "Muse - Algorithm",
                    "ODESZA - Boy",
                    "The Neighbourhood - Afraid (Album Version)",
                    "Au5 - Zero (Original Mix)",
                    "San Holo - Light",
                    "Pixel Terror - Amnesia",
                    "Bandlez - Together Again",
                    "ADEZIA - Precipitate",
                    "Au5 & Fractal - Pavonine (Original Mix)",
                    "Direct feat. Holly Drummond - Memory",
                    "Stonebank - The Pressure",
                    "Slips & Slurs - Divided",
                    "Virtual Riot - With You",
                    "Illenium - Leaving",
                    "Porter Robinson - Divinity",
                    "Au5 - Depths Of Ice (Original Mix)",
                    "Trivecta feat. Aloma Steele - Evaporate",
                    "Asking Alexandria - Alone In a Room",
                    "Crywolf  - Shrike",
                    "Infected Mushroom - Guitarmass",
                    "Muse - Algorithm",
                    "ODESZA - Boy",
                    "The Neighbourhood - Afraid (Album Version)",
                    "Au5 - Zero (Original Mix)",
                    "San Holo - Light"
                ))
        return view
    }
}