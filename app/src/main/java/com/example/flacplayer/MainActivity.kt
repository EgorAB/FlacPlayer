package com.example.flacplayer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Layout
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.blocks_scene.*
import kotlinx.android.synthetic.main.library_scene.*
import kotlinx.android.synthetic.main.options_scene.*
import kotlinx.android.synthetic.main.play_scene.*
import kotlinx.android.synthetic.main.playlist_scene.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


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
        addTextBlockButton.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(
                R.layout.text_block, null, false
            )
            blocks_scene.addView(view)
            ObjectAnimator.ofInt(play_scene, "scrollY", parent_of_blocks_scene.bottom).setDuration(1400).start()
            view.findViewWithTag<ImageButton>("delete_button").setOnClickListener {
                view.animate().setDuration(500).alpha(0F).withEndAction {
                    run { blocks_scene.removeView(view) }
                }
            }
            view.findViewWithTag<ImageButton>("edit_button").setOnClickListener {
                view.findViewWithTag<EditText>("edit_text").visibility = View.VISIBLE
                view.findViewWithTag<EditText>("edit_text").setSelection(view.findViewWithTag<EditText>("edit_text").text.length)
                view.findViewWithTag<EditText>("edit_text").setText(view.findViewWithTag<TextView>("text_view").text)
                view.findViewWithTag<TextView>("text_view").visibility = View.GONE
                view.findViewWithTag<ImageButton>("edit_button").visibility = View.GONE
                view.findViewWithTag<ImageButton>("ok_button").visibility = View.VISIBLE
            }
            view.findViewWithTag<ImageButton>("ok_button").setOnClickListener {
                view.findViewWithTag<TextView>("text_view").visibility = View.VISIBLE
                view.findViewWithTag<TextView>("text_view").text = view.findViewWithTag<EditText>("edit_text").text
                view.findViewWithTag<EditText>("edit_text").visibility = View.GONE
                view.findViewWithTag<ImageButton>("ok_button").visibility = View.GONE
                view.findViewWithTag<ImageButton>("edit_button").visibility = View.VISIBLE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.findViewWithTag<EditText>("edit_text").windowToken, 0)
            }
        }
    }

    private fun selectScene(newScene: View) {
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
