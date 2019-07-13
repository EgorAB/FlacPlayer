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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.blocks_scene.*
import kotlinx.android.synthetic.main.play_scene.*


class PlaySceneFragment : Fragment() {

    var songList: ArrayList<Song> = arrayListOf()

    private var currentSongListIndex: Int = 0
        set(value) {
            field = if (value >= 0) if (value >= songList.size) 0 else value else songList.size - 1
        }
    private var isRepeating: Boolean = false
        set(value) {
            field = value
            repeatButton?.setColorFilter(resources.getColor(if (value) R.color.materialBlack else R.color.materialExtraLightGray))
            mediaPlayer?.isLooping = value
        }

    private var clickedOnSeekBar = false
    private var handler = Handler()

    private var mediaPlayer: MediaPlayer? = null
    private var timeCurrent: TextView? = null
    private var timeLeft: TextView? = null
    private var playButton: ImageButton? = null
    private var seekBar: SeekBar? = null
    private var songArtist: TextView? = null
    private var songTitle: TextView? = null
    private var gradient: ImageView? = null

    private var previousButton: ImageButton? = null
    private var nextButton: ImageButton? = null
    private var repeatButton: ImageButton? = null
    private var shuffleButton: ImageButton? = null

    private fun play() {
        try {
            mediaPlayer!!.start()
            startPlayProgressUpdater()
            playButton!!.setImageResource(R.drawable.ic_pause_black_24dp)
        } catch (e: IllegalStateException) {
            mediaPlayer!!.pause()
        }
    }

    private fun pause() {
        mediaPlayer?.pause()
        playButton?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
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
        songList = arrayListOf(
            Song(R.raw.farzam_finding_hope.toLong(), "FARZAM", "Finding Hope"),
            Song(R.raw.asking_alexandria_alone_in_a_room.toLong(), "Asking Alexandria", "Alone in a room"),
            Song(R.raw.dez_money_wings.toLong(), "Dez Money", "Wings")
        )

        initializeValues(view)
        initializePlayer(this.context!!, songList[currentSongListIndex].id)

        playButton?.setOnClickListener {
            if (mediaPlayer!!.isPlaying) pause()
            else play()
        }

        view.findViewById<FloatingActionButton>(R.id.addTextBlockButton).setOnClickListener {
            val v = LayoutInflater.from(this.context).inflate(
                R.layout.text_block, null, false
            )
            blocks_scene.addView(v)
            ObjectAnimator.ofInt(play_scene, "scrollY", parent_of_blocks_scene.bottom).setDuration(900).start()
            v.findViewWithTag<ImageButton>("delete_button").setOnClickListener {
                v.animate().setDuration(300).alpha(0F).withEndAction {
                    run { blocks_scene.removeView(v) }
                }
            }
            v.findViewWithTag<ImageButton>("edit_button").setOnClickListener {
                v.findViewWithTag<EditText>("edit_text").visibility = View.VISIBLE
                v.findViewWithTag<EditText>("edit_text")
                    .setSelection(v.findViewWithTag<EditText>("edit_text").text.length)
                v.findViewWithTag<EditText>("edit_text").setText(v.findViewWithTag<TextView>("text_view").text)
                v.findViewWithTag<TextView>("text_view").visibility = View.GONE
                v.findViewWithTag<ImageButton>("edit_button").visibility = View.GONE
                v.findViewWithTag<ImageButton>("ok_button").visibility = View.VISIBLE
            }
            v.findViewWithTag<ImageButton>("ok_button").setOnClickListener {
                v.findViewWithTag<TextView>("text_view").visibility = View.VISIBLE
                v.findViewWithTag<TextView>("text_view").text = v.findViewWithTag<EditText>("edit_text").text
                v.findViewWithTag<EditText>("edit_text").visibility = View.GONE
                v.findViewWithTag<ImageButton>("ok_button").visibility = View.GONE
                v.findViewWithTag<ImageButton>("edit_button").visibility = View.VISIBLE
                val imm = v.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        previousButton?.setOnClickListener {
            if (seekBar!!.progress > 5000) {
                mediaPlayer!!.seekTo(0)
                view.findViewById<SeekBar>(R.id.seekBar).progress = 0
            } else {
                currentSongListIndex--
                initializePlayer(this.context!!, songList[currentSongListIndex].id)
                play()
            }
        }
        nextButton?.setOnClickListener {
            currentSongListIndex++
            initializePlayer(this.context!!, songList[currentSongListIndex].id)
            play()
        }
        repeatButton?.setOnClickListener {
            isRepeating = !isRepeating
        }
        seekBar!!.max = mediaPlayer!!.duration
        seekBar!!.setOnTouchListener { _, _ ->
            mediaPlayer!!.seekTo(view.findViewById<SeekBar>(R.id.seekBar).progress)
            updateTimeCounters()
            false
        }
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) { clickedOnSeekBar = true }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTimeCounters()
            }
        })
        return view
    }

    private fun initializeValues(v: View) {
        gradient = v.findViewById(R.id.Gradient)
        songTitle = v.findViewById(R.id.songTitle1)
        songArtist = v.findViewById(R.id.songArtist)
        playButton = v.findViewById(R.id.playButton)
        timeCurrent = v.findViewById(R.id.timeCurrent)
        timeLeft = v.findViewById(R.id.timeLeft)
        previousButton = v.findViewById(R.id.previousButton)
        nextButton = v.findViewById(R.id.nextButton)
        repeatButton = v.findViewById(R.id.repeatButton)
        shuffleButton = v.findViewById(R.id.shuffleButton)
        seekBar = v.findViewById(R.id.seekBar)
    }

    private fun initializePlayer(context: Context, songId: Long) {
        if (mediaPlayer != null) {
            pause()
            mediaPlayer!!.reset()
        }
        mediaPlayer = MediaPlayer.create(context, songId.toInt())


        seekBar!!.progress = 0
        mediaPlayer!!.seekTo(0)
        seekBar!!.max = mediaPlayer?.duration!!
        updateTimeCounters()
        songArtist?.text = songList[currentSongListIndex].artist
        songTitle?.text = songList[currentSongListIndex].title
        mediaPlayer!!.setOnCompletionListener {
            if(!isRepeating) {
                currentSongListIndex++
                initializePlayer(this.context!!, songList[currentSongListIndex].id)
                play()
            }
        }
    }


    private fun startPlayProgressUpdater() {
        if(!clickedOnSeekBar) seekBar!!.progress = mediaPlayer!!.currentPosition
        else { mediaPlayer!!.seekTo(seekBar!!.progress); clickedOnSeekBar = false }
        if (mediaPlayer!!.isPlaying) {
            val notification = Runnable { startPlayProgressUpdater() }
            handler.postDelayed(notification, 100)
            updateTimeCounters()
        } else {
            mediaPlayer!!.pause()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateTimeCounters(){
        timeCurrent?.text = "${seekBar!!.progress / 1000 / 60}:" +
                (if (seekBar!!.progress / 1000 % 60 / 10 == 0) "0" else "") +
                "${seekBar!!.progress / 1000 % 60}"
        timeLeft?.text = "-${(seekBar!!.max - seekBar!!.progress) / 1000 / 60}:" +
                (if ((seekBar!!.max - seekBar!!.progress) / 1000 % 60 / 10 == 0) "0" else "") +
                "${(seekBar!!.max - seekBar!!.progress) / 1000 % 60}"
    }
    override fun onDestroy() {
        super.onDestroy()
        pause()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // после выполнения onCreate в активити фрагмента, пользователь на момент выполнения еще не видит интерфейс
        super.onActivityCreated(savedInstanceState)
    }
}