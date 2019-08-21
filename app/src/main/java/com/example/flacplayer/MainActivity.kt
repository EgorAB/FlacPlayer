package com.example.flacplayer

import android.content.ContentResolver
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.ContentLoadingProgressBar
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.playlistItemSelectedBar
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.play_scene.*
import kotlinx.android.synthetic.main.player_tabs.*


class MainActivity : FragmentActivity(), PlaylistFragment.PlaylistInteractionListener,
    PlaySceneFragment.PlaySceneInteractionListener {

    private var selectedScene: View? = null

    // Фрагменты трех вкладок в TabLayout:
    private val libraryFragment: LibraryFragment = LibraryFragment()
    private val playlistFragment: PlaylistFragment = PlaylistFragment()
    private val playSceneFragment: PlaySceneFragment = PlaySceneFragment()

    // публичные вьюшки чтобы их могли юзать фрагменты
    public var publicPlayPauseBottomSheet: ImageButton? = null
    public var publicSongArtistBottomSheet: TextView? = null
    public var publicSongTitleBottomSheet: TextView? = null
    public var publicProgbarBottomSheet: ContentLoadingProgressBar? = null

    public fun initValues() {
        publicPlayPauseBottomSheet = playPauseBottomSheet
        publicSongArtistBottomSheet = songArtistBottomSheet
        publicSongTitleBottomSheet = songTitleBottomSheet
        publicProgbarBottomSheet = progbarBottomSheet
    }

    // нормальный код
    var songList = arrayListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_modificated)

        initValues()
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER

        getSongList()
        selectedScene = player_tabs

        // работа с Bar-ами
        // playlistItemSelectedBar
        playlistItemSelectedBar.alpha = 0f
        playlistItemSelectedBar.findViewWithTag<ImageButton>("delete").setOnClickListener {
            playlistFragment.PISBarDeleteButton()
        }
        playlistItemSelectedBar.findViewWithTag<ImageButton>("close").setOnClickListener {
            playlistFragment.PISBarCloseButton()
        }
        // tabsBar
        leftTab.setOnClickListener {
            if (tabLayout.selectedTabPosition > 0)
                viewPager.setCurrentItem(tabLayout.selectedTabPosition - 1, true)
        }
        rightTab.setOnClickListener {
            if (tabLayout.selectedTabPosition < 2)
                viewPager.setCurrentItem(tabLayout.selectedTabPosition + 1, true)
        }
        //
        playPauseBottomSheet.setOnClickListener {
            if (playing()) pause()
            else play()
        }

        // работа с TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            // val appBars = arrayListOf<View>(libraryBar, playBar, playlistBar)
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 2 || tab.position == 0) {
                    hideBottomNavigationView()
                }
                when (tab.position) {
                    0 -> {
                        leftTab.visibility = View.GONE
                        rightTab.visibility = View.VISIBLE
                        tabId.text = "Библиотека"
                    }
                    1 -> {
                        leftTab.visibility = View.VISIBLE
                        rightTab.visibility = View.VISIBLE
                        tabId.text = "Трек"
                    }
                    2 -> {
                        leftTab.visibility = View.VISIBLE
                        rightTab.visibility = View.GONE
                        tabId.text = "Плейлист"
                    }
                }

                if (tab.position == 2) playlistFragment.selectSong(playlistFragment.currentSongView)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.position == 2 || tab.position == 0) {
                    showBottomNavigationView()
                }
                if (tab.position == 2) playlistFragment.PISBarCloseButton()
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        // работа с BottomSheet
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tabsBar.visibility = View.GONE
                    playlistFragment.PISBarCloseButton()
                } else tabsBar.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                tabsBar.alpha = slideOffset
            }
        })
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
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisData = musicCursor.getString(data)
                // достаем картиночку
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(Uri.parse("file:///$thisData")!!.path)
                val pic = mmr.embeddedPicture
                var bitmap:Bitmap? = null
                if(pic != null) bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.size)
                songList.add(Song(thisId, thisArtist, thisTitle, Uri.parse("file:///$thisData"), bitmap))
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
    fun hidePISBar() {
        playlistItemSelectedBar.clearAnimation()
        playlistItemSelectedBar.animate().alpha(0f).withEndAction {
            playlistItemSelectedBar.visibility = View.GONE
        }.duration = 200
    }

    // show playlistItemSelectedBar
    fun showPISBar() {
        playlistItemSelectedBar.clearAnimation()
        playlistItemSelectedBar.visibility = View.VISIBLE
        playlistItemSelectedBar.animate().alpha(1f).duration = 200
    }
}
