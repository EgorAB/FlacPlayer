package com.example.flacplayer

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.blocks_scene.*
import kotlinx.android.synthetic.main.play_scene.*
import kotlinx.android.synthetic.main.playlist_scene.*

class PlaySceneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.play_scene, container, false)
    }

    private var playerActive = false

    private fun play() {
        playerActive = true
        playButton.setImageResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pause() {
        playerActive = false
        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        playButton.setOnClickListener {
//            if (playerActive) pause()
//            else play()
//        }
//        sampleButton.setOnClickListener {
//            var tr = Fade()
//            TransitionManager.beginDelayedTransition(sampleButtonContainer, Slide(Gravity.START))
//        }
//        addTextBlockButton.setOnClickListener {
//            val view = LayoutInflater.from(activity).inflate(
//                R.layout.text_block, null, false
//            )
//            blocks_scene.addView(view)
//            ObjectAnimator.ofInt(play_scene, "scrollY", parent_of_blocks_scene.bottom).setDuration(900).start()
//            view.findViewWithTag<ImageButton>("delete_button").setOnClickListener {
//                view.animate().setDuration(500).alpha(0F).withEndAction {
//                    run { blocks_scene.removeView(view) }
//                }
//            }
//            view.findViewWithTag<ImageButton>("edit_button").setOnClickListener {
//                view.findViewWithTag<EditText>("edit_text").visibility = View.VISIBLE
//                view.findViewWithTag<EditText>("edit_text")
//                    .setSelection(view.findViewWithTag<EditText>("edit_text").text.length)
//                view.findViewWithTag<EditText>("edit_text").setText(view.findViewWithTag<TextView>("text_view").text)
//                view.findViewWithTag<TextView>("text_view").visibility = View.GONE
//                view.findViewWithTag<ImageButton>("edit_button").visibility = View.GONE
//                view.findViewWithTag<ImageButton>("ok_button").visibility = View.VISIBLE
//            }
//            view.findViewWithTag<ImageButton>("ok_button").setOnClickListener {
//                view.findViewWithTag<TextView>("text_view").visibility = View.VISIBLE
//                view.findViewWithTag<TextView>("text_view").text = view.findViewWithTag<EditText>("edit_text").text
//                view.findViewWithTag<EditText>("edit_text").visibility = View.GONE
//                view.findViewWithTag<ImageButton>("ok_button").visibility = View.GONE
//                view.findViewWithTag<ImageButton>("edit_button").visibility = View.VISIBLE
////                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////                imm.hideSoftInputFromWindow(view.findViewWithTag<EditText>("edit_text").windowToken, 0)
//            }
//        }
//    }
}