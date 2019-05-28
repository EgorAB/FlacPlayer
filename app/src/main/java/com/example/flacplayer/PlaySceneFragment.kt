package com.example.flacplayer

import android.animation.ObjectAnimator
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.blocks_scene.*
import kotlinx.android.synthetic.main.play_scene.*


class PlaySceneFragment : Fragment() {

    private var playerActive = false

    private fun play(playButton: ImageButton) {
        playerActive = true
        playButton.setImageResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pause(playButton: ImageButton) {
        playerActive = false
        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // нельзя использовать ресурсозависящий код. ресурсы и компоненты еще неизвестны и не иницилизированы
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // можно проводить все требуемые манипуляции
        // создаем view-элемент из разметки и проводим с ним манипуляции, после чего возвращаем из метода
        val view = inflater.inflate(R.layout.play_scene, container, false)
        view.findViewById<ImageButton>(R.id.playButton).setOnClickListener {
            if (playerActive) pause(view.findViewById(R.id.playButton))
            else play(view.findViewById(R.id.playButton))
        }
        view.findViewById<FloatingActionButton>(R.id.addTextBlockButton).setOnClickListener {
            val view = LayoutInflater.from(this.context).inflate(
                R.layout.text_block, null, false
            )
            blocks_scene.addView(view)
            ObjectAnimator.ofInt(play_scene, "scrollY", parent_of_blocks_scene.bottom).setDuration(900).start()
            view.findViewWithTag<ImageButton>("delete_button").setOnClickListener {
                view.animate().setDuration(500).alpha(0F).withEndAction {
                    run { blocks_scene.removeView(view) }
                }
            }
            view.findViewWithTag<ImageButton>("edit_button").setOnClickListener {
                view.findViewWithTag<EditText>("edit_text").visibility = View.VISIBLE
                view.findViewWithTag<EditText>("edit_text")
                    .setSelection(view.findViewWithTag<EditText>("edit_text").text.length)
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
                val imm = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // после выполнения onCreate в активити фрагмента, пользователь на момент выполнения еще не видит интерфейс
        super.onActivityCreated(savedInstanceState)
    }
}