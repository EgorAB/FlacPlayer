package com.example.flacplayer

import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.more_layout.*
import kotlinx.android.synthetic.main.player_tabs.*


class MainActivity : FragmentActivity(), PlaylistFragment.PlaylistInteractionListener,
    PlaySceneFragment.PlaySceneInteractionListener {

    private var selectedScene: View? = null

    // Фрагменты трех вкладок в TabLayout:
    private val libraryFragment: LibraryFragment = LibraryFragment()
    private val playlistFragment: PlaylistFragment = PlaylistFragment()
    private val playSceneFragment: PlaySceneFragment = PlaySceneFragment()


    var songList = arrayListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSongList()
        selectedScene = player_tabs
        playlistItemSelectedBar.alpha = 0f
        playlistItemSelectedBar.findViewWithTag<ImageButton>("delete").setOnClickListener {
            playlistFragment.PISBarDeleteButton()
        }
        playlistItemSelectedBar.findViewWithTag<ImageButton>("close").setOnClickListener {
            playlistFragment.PISBarCloseButton()
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            val appBars = arrayListOf<View>(libraryBar, playBar, playlistBar)
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 2 || tab.position == 0) {
                    hideBottomNavigationView()
                    // showPISBar()
                }
                appBars[tab.position].alpha = 1F
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.position == 2 || tab.position == 0) {
                    showBottomNavigationView()
                    // hidePISBar()
                }
                if(tab.position == 2) playlistFragment.PISBarCloseButton()
                appBars[tab.position].alpha = 0F
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        bottomNavigationView.selectedItemId = R.id.playButton
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeButton -> {
                    selectScene(home_layout)
                    true
                }
                R.id.playButton -> {
                    selectScene(player_tabs)
                    true
                }
                R.id.moreButton -> {
                    selectScene(more_layout)
                    true
                }
                else -> true
            }
        }

        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        moreListView.adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            arrayOf(
                "Настройки",
                "О приложении"
            )
        )
    }

    // имплементация PlaylistInteractionListener
    override fun initPlayer(song: Song) {
        playSceneFragment.initializePlayer(song)
        playSceneFragment.play()
        // (supportFragmentManager.findFragmentById(R.id.play_scene) as PlaySceneFragment).initializePlayer(songId)
    }

    override fun play() = playSceneFragment.play()

    override fun pause() = playSceneFragment.pause()

    override fun playing(): Boolean {
        if (playSceneFragment.mediaPlayer == null) return false
        return playSceneFragment.mediaPlayer!!.isPlaying
    }

    // имплементация PlaySceneInteractionListener
    override fun nextSong(select: Boolean): Song? = playlistFragment.nextSong(select)

    override fun prevSong(select: Boolean): Song = playlistFragment.prevSong(select)

    private fun selectScene(newScene: View) {
        selectedScene?.visibility = View.GONE
        selectedScene = newScene
        selectedScene?.visibility = View.VISIBLE
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(libraryFragment, "Библиотека")
        adapter.addFragment(playSceneFragment, "Трек")
        adapter.addFragment(playlistFragment, "Плейлист")
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }

    // заполнение songList треками с устройства
    private fun getSongList() {
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val musicResolver: ContentResolver = contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            val data = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val coverColumn: Int = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                // val thisCoverUri = musicCursor.getString(coverColumn)
                val thisData = musicCursor.getString(data)
                songList.add(Song(thisId, thisArtist, thisTitle, Uri.parse("file:///$thisData")))
            } while (musicCursor.moveToNext())
        }
        musicCursor?.close()
        // songListView?.adapter = SongAdapter(this.context!!, songList)
    }

    fun hideBottomNavigationView() {
        bottomNavigationView.clearAnimation()
        bottomNavigationView.animate().translationY(bottomNavigationView.height.toFloat()).duration = 200
    }

    fun showBottomNavigationView() {
        bottomNavigationView.clearAnimation()
        bottomNavigationView.animate().translationY(0f).duration = 200
    }

    // hide playlistItemSelectedBar
    fun hidePISBar(){
        playlistItemSelectedBar.clearAnimation()
        playlistItemSelectedBar.animate().alpha(0f).duration = 200
    }

    // show playlistItemSelectedBar
    fun showPISBar(){
        playlistItemSelectedBar.clearAnimation()
        playlistItemSelectedBar.animate().alpha(1f).duration = 200
    }
}
