package com.example.flacplayer

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.library_scene.*
import kotlinx.android.synthetic.main.options_scene.*
import kotlinx.android.synthetic.main.play_scene.*
import kotlinx.android.synthetic.main.playlist_scene.*

class MainActivity : AppCompatActivity() {
    private var playerActive = false
    private var selectedScene: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectedScene = play_scene
        bottomNavigationView.selectedItemId = R.id.playButton
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.playButton -> {
                    selectScene(play_scene)
                    true
                }
                R.id.playlistButton -> {
                    selectScene(playlist_scene)
                    true
                }
                R.id.libraryButton -> {
                    selectScene(library_scene)
                    true
                }
                R.id.moreButton -> {
                    selectScene(options_scene)
                    true
                }
                else -> true
            }
        }
        playButton.setOnClickListener {
            if (playerActive) pause()
            else play()
        }

        sampleButton.setOnClickListener {
            var tr = Fade()
            TransitionManager.beginDelayedTransition(sampleButtonContainer, Slide(Gravity.START))
        }
    }

    fun selectScene(newScene:View) {
        selectedScene?.visibility = View.GONE
        selectedScene = newScene
        selectedScene?.visibility = View.VISIBLE
    }

    private fun play() {
        playerActive = true
        playButton.setImageResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pause() {
        playerActive = false
        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.nav_settings -> selectScene(options_scene)
        }
        return super.onOptionsItemSelected(item)
    }
}
