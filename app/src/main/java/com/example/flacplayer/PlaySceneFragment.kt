package com.example.flacplayer

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.lang.ClassCastException


class PlaySceneFragment : Fragment() {

    interface PlaySceneInteractionListener {
        fun nextSong(select: Boolean = false): Song?
        fun prevSong(select: Boolean = false): Song
    }

    var mListener: PlaySceneInteractionListener? = null
    var songList: ArrayList<Song> = arrayListOf()
    private var currentSongListIndex: Int = 0
        set(value) {
            field = if (value >= 0) if (value >= songList.size) 0 else value else songList.size - 1
        }
    private var isRepeating: Boolean = false
        set(value) {
            field = value
            repeatButton?.setColorFilter(resources.getColor(if (value) R.color.darkThemeColorPrimaryDark else R.color.materialExtraLightGray))
            mediaPlayer?.isLooping = value
        }

    private var clickedOnSeekBar = false
    private var handler = Handler()

    public var mediaPlayer: MediaPlayer? = null
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = context as PlaySceneInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " должен реализовывать интерфейс PlaySceneInteractionListener")
        }
    }

    public fun play() {
        try {
            mediaPlayer!!.start()
            startPlayProgressUpdater()
            playButton!!.setImageResource(R.drawable.ic_pause_black_24dp)
            if (activity != null)
                (activity as MainActivity).publicPlayPauseBottomSheet?.setImageResource(R.drawable.ic_pause_black_24dp)
        } catch (e: IllegalStateException) {
            mediaPlayer!!.pause()
        }
    }

    public fun pause() {
        mediaPlayer?.pause()
        playButton?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        if (activity != null)
            (activity as MainActivity).publicPlayPauseBottomSheet?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
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
        initializePlayer(songList[currentSongListIndex].id)

        playButton?.setOnClickListener {
            if (mediaPlayer!!.isPlaying) pause()
            else play()
        }
        previousButton?.setOnClickListener {
            if (seekBar!!.progress > 5000) {
                mediaPlayer!!.seekTo(0)
                seekBar!!.progress = 0
            } else {
                initializePlayer(mListener!!.prevSong(true))
                play()
            }
        }
        nextButton?.setOnClickListener {
            val s: Song? = mListener?.nextSong(true)
            if (s != null) {
                initializePlayer(s); play()
            } else pause()
        }
        repeatButton?.setOnClickListener {
            isRepeating = !isRepeating
        }
        seekBar!!.max = mediaPlayer!!.duration
        if (activity != null)
            (activity as MainActivity).publicProgbarBottomSheet?.max = mediaPlayer?.duration!!
        seekBar!!.setOnTouchListener { _, _ ->
            mediaPlayer!!.seekTo(seekBar!!.progress)
            updateTimeCounters()
            false
        }
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) { clickedOnSeekBar = true }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTimeCounters()
                if(activity != null)
                    (activity as MainActivity).publicProgbarBottomSheet?.progress = progress
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

    public fun initializePlayer(songId: Long) {
        if (context == null) return
        if (mediaPlayer != null) {
            pause()
            mediaPlayer!!.reset()
        }
        mediaPlayer = MediaPlayer.create(this.context!!, songId.toInt())

        seekBar!!.progress = 0
        mediaPlayer!!.seekTo(0)
        seekBar!!.max = mediaPlayer?.duration!!
        if (activity != null)
            (activity as MainActivity).publicProgbarBottomSheet?.max = mediaPlayer?.duration!!
        updateTimeCounters()
        songArtist?.text = songList[currentSongListIndex].artist
        songTitle?.text = songList[currentSongListIndex].title
        if (activity != null)
            (activity as MainActivity).publicSongArtistBottomSheet?.text = songList[currentSongListIndex].artist
        if (activity != null)
            (activity as MainActivity).publicSongTitleBottomSheet?.text = songList[currentSongListIndex].title

        mediaPlayer!!.setOnCompletionListener {
            if (!isRepeating) {
                currentSongListIndex++
                initializePlayer(songList[currentSongListIndex].id)
                play()
            }
        }
    }

    public fun initializePlayer(song: Song) {
        if (mediaPlayer != null) {
            pause()
            mediaPlayer!!.reset()
        }
        mediaPlayer = MediaPlayer.create(this.context!!, song.uri)

        seekBar!!.progress = 0
        mediaPlayer!!.seekTo(0)
        seekBar!!.max = mediaPlayer?.duration!!
        if(activity != null)
            (activity as MainActivity).publicProgbarBottomSheet?.max = mediaPlayer?.duration!!
        updateTimeCounters()
        songArtist?.text = song.artist
        songTitle?.text = song.title
        if (activity != null)
            (activity as MainActivity).publicSongArtistBottomSheet?.text = song.artist
        if (activity != null)
            (activity as MainActivity).publicSongTitleBottomSheet?.text = song.title
//        songArtistBottomSheet!!.text = song.artist
//        songTitleBottomSheet!!.text = song.title

        setOptimalTextSize(song.artist, song.title)
        mediaPlayer!!.setOnCompletionListener {
            if (!isRepeating) {
                val s: Song? = mListener?.nextSong(true)
                if (s != null) {
                    initializePlayer(s); play()
                } else pause()
            }
        }
    }

    private fun setOptimalTextSize(artist: String, title: String) {
        when (title.length) {
            in 0..22 -> {
                songArtist?.textSize = 22F
                songTitle?.textSize = 36F
            }
            in 23..27 -> {
                songArtist?.textSize = 20F
                songTitle?.textSize = 30F
            }
            else -> {
                songArtist?.textSize = 18F
                songTitle?.textSize = 24F
            }
        }
    }

    private fun startPlayProgressUpdater() {
        if (!clickedOnSeekBar) {
            seekBar!!.progress = mediaPlayer!!.currentPosition
        }
        else {
            mediaPlayer!!.seekTo(seekBar!!.progress); clickedOnSeekBar = false
        }
        if (mediaPlayer!!.isPlaying) {
            val notification = Runnable { startPlayProgressUpdater() }
            handler.postDelayed(notification, 100)
            updateTimeCounters()
        } else {
            mediaPlayer!!.pause()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeCounters() {
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