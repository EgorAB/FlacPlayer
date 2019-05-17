package com.example.flacplayer

import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.library_scene.*
import kotlinx.android.synthetic.main.play_scene.*
import kotlinx.android.synthetic.main.playlist_scene.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        toolbar.title = "Воспроизведение аудиозаписи"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        bottomNavigationView.selectedItemId = R.id.playButton
        bottomNavigationView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.playButton -> {
                    play_scene.visibility = View.VISIBLE
                    playlist_scene.visibility = View.GONE
                    library_scene.visibility = View.GONE
                    true
                }
                R.id.playlistButton -> {
                    play_scene.visibility = View.GONE
                    playlist_scene.visibility = View.VISIBLE
                    library_scene.visibility = View.GONE
                    true
                }
                R.id.libraryButton -> {
                    play_scene.visibility = View.GONE
                    playlist_scene.visibility = View.GONE
                    library_scene.visibility = View.VISIBLE
                    true
                }
                else -> true
            }
        }
    }
}
