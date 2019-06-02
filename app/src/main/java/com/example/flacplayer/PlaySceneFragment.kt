package com.example.flacplayer

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import kotlinx.android.synthetic.main.blocks_scene.*
import kotlinx.android.synthetic.main.play_scene.*


class PlaySceneFragment : Fragment() {

    var songList: Array<Song> = arrayOf()

    private var currentSongListIndex: Int = 0
        set(value) {
            if (value > 0) field = if (value >= songList.size) 0 else value
            else field = songList.size - 1
        }

    private var playerActive = false

    private var handler = Handler()

    private var mediaPlayer: MediaPlayer? = null
    private var timeCurrent: TextView? = null
    private var timeLeft: TextView? = null
    private var playButton: ImageButton? = null

    private var songArtist: TextView? = null
    private var songTitle: TextView? = null

    private var previousButton: ImageButton? = null
    private var nextButton: ImageButton? = null

    private fun play() {
        playerActive = true
        try {
            mediaPlayer!!.start()
            startPlayProgressUpdater(seekBar)
        } catch (e: IllegalStateException) {
            mediaPlayer!!.pause()
        }
        playButton!!.setImageResource(R.drawable.ic_pause_black_24dp)
    }

    private fun pause() {
        playerActive = false
        mediaPlayer!!.pause()
        playButton!!.setImageResource(R.drawable.ic_play_arrow_black_24dp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // нельзя использовать ресурсозависящий код. ресурсы и компоненты еще неизвестны и не иницилизированы
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility", "NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // можно проводить все требуемые манипуляции
        // создаем view-элемент из разметки и проводим с ним манипуляции, после чего возвращаем из метода
        val view = inflater.inflate(R.layout.play_scene, container, false)

        // songList initialization
        songList = arrayOf(
            Song(R.raw.farzam_finding_hope.toLong(), "FARZAM", "Finding Hope"),
            Song(R.raw.asking_alexandria_alone_in_a_room.toLong(), "Asking Alexandria", "Alone in a room"),
            Song(R.raw.asking_alexandria_here_i_am.toLong(), "Asking Alexandria", "Here I am"),
            Song(R.raw.asking_alexandria_send_me_home.toLong(), "Asking Alexandria", "Send me home"),
            Song(R.raw.demorandum_time_that_im_gone.toLong(), "demorandum", "time that I'm gone"),
            Song(R.raw.muse_dead_inside.toLong(), "Muse", "Dead inside"),
            Song(R.raw.illenium_feat_joni_fatora_sleepwalker.toLong(), "Illenium feat.Joni Fatora", "Sleepwalker"),
            Song(R.raw.xtrullor_nirmiti.toLong(), "Xtrullor", "Nirmiti"),
            Song(R.raw.porter_robinson_goodbye_to_a_world.toLong(), "Porter Robinson", "Goodbye to a world"),
            Song(R.raw.vicetone_feat_cozi_zuehlsdorff_nevada.toLong(), "Vicetone feat.Cozi Zuehlsdorff", "Nevada")
        )

        initializeValues(view)
        initializePlayer(this.context!!, view.findViewById(R.id.seekBar), songList[currentSongListIndex].id)

        playButton?.setOnClickListener {
            if (playerActive) pause()
            else play()
        }

        view.findViewById<FloatingActionButton>(R.id.addTextBlockButton).setOnClickListener {
            val view = LayoutInflater.from(this.context).inflate(
                R.layout.text_block, null, false
            )
            blocks_scene.addView(view)
            ObjectAnimator.ofInt(play_scene, "scrollY", parent_of_blocks_scene.bottom).setDuration(900).start()
            view.findViewWithTag<ImageButton>("delete_button").setOnClickListener {
                view.animate().setDuration(300).alpha(0F).withEndAction {
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
        previousButton?.setOnClickListener {
            if (view.findViewById<SeekBar>(R.id.seekBar).progress > 5000) {
                mediaPlayer!!.seekTo(0)
                view.findViewById<SeekBar>(R.id.seekBar).progress = 0
            }
            else {
                currentSongListIndex--
                initializePlayer(this.context!!, view.findViewById(R.id.seekBar), songList[currentSongListIndex].id)
            }
        }
        nextButton?.setOnClickListener {
            currentSongListIndex++
            initializePlayer(this.context!!, view.findViewById(R.id.seekBar), songList[currentSongListIndex].id)
        }
        view.findViewById<SeekBar>(R.id.seekBar).max = mediaPlayer!!.duration
        view.findViewById<SeekBar>(R.id.seekBar).setOnTouchListener { _, _ ->
            mediaPlayer!!.seekTo(view.findViewById<SeekBar>(R.id.seekBar).progress)
            false
        }
        view.findViewById<SeekBar>(R.id.seekBar).setOnContextClickListener {
            mediaPlayer!!.seekTo(view.findViewById<SeekBar>(R.id.seekBar).progress)
            false
        }
        view.findViewById<SeekBar>(R.id.seekBar).setOnClickListener {
            mediaPlayer!!.seekTo(view.findViewById<SeekBar>(R.id.seekBar).progress)
        }
        view.findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                timeCurrent?.text = "${mediaPlayer!!.currentPosition / 1000 / 60}:" +
                        "${if (mediaPlayer!!.currentPosition / 1000 % 60 / 10 == 0) "0" else ""}" +
                        "${mediaPlayer!!.currentPosition / 1000 % 60}"
                timeLeft?.text = "-${(mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 / 60}:" +
                        "${if ((mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 % 60 / 10 == 0) "0" else ""}" +
                        "${(mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 % 60}"
            }
        })
        return view
    }

    private fun initializeValues(v: View) {
        songTitle = v.findViewById(R.id.songTitle1)
        songArtist = v.findViewById(R.id.songArtist)
        playButton = v.findViewById(R.id.playButton)
        timeCurrent = v.findViewById(R.id.timeCurrent)
        timeLeft = v.findViewById(R.id.timeLeft)
        previousButton = v.findViewById(R.id.previousButton)
        nextButton = v.findViewById(R.id.nextButton)
    }

    private fun initializePlayer(context: Context, seekBar: SeekBar, songId: Long) {
        if (mediaPlayer != null) pause()
        mediaPlayer = MediaPlayer.create(context, songId.toInt())
        seekBar.progress = 0
        mediaPlayer!!.seekTo(0)
        seekBar.max = mediaPlayer?.duration!!
        timeCurrent?.text = "${mediaPlayer!!.currentPosition / 1000 / 60}:" +
                "${if (mediaPlayer!!.currentPosition / 1000 % 60 / 10 == 0) "0" else ""}" +
                "${mediaPlayer!!.currentPosition / 1000 % 60}"
        timeLeft?.text = "-${(mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 / 60}:" +
                "${if ((mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 % 60 / 10 == 0) "0" else ""}" +
                "${(mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 % 60}"
        songArtist?.text = songList[currentSongListIndex].artist
        songTitle?.text = songList[currentSongListIndex].title
    }

    private fun startPlayProgressUpdater(seekBar: SeekBar) {
        seekBar.progress = mediaPlayer!!.currentPosition

        if (mediaPlayer!!.isPlaying) {
            val notification = Runnable { startPlayProgressUpdater(seekBar) }
            handler.postDelayed(notification, 100)
            timeCurrent?.text = "${mediaPlayer!!.currentPosition / 1000 / 60}:" +
                    "${if (mediaPlayer!!.currentPosition / 1000 % 60 / 10 == 0) "0" else ""}" +
                    "${mediaPlayer!!.currentPosition / 1000 % 60}"
            timeLeft?.text = "-${(mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 / 60}:" +
                    "${if ((mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 % 60 / 10 == 0) "0" else ""}" +
                    "${(mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000 % 60}"
        } else {
            mediaPlayer!!.pause()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // после выполнения onCreate в активити фрагмента, пользователь на момент выполнения еще не видит интерфейс
        super.onActivityCreated(savedInstanceState)
    }
}