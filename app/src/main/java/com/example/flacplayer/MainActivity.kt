package com.example.flacplayer

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.more_layout.*
import kotlinx.android.synthetic.main.player_tabs.*


class MainActivity : FragmentActivity(), PlaylistFragment.PlaylistInteractionListener {

    private var selectedScene: View? = null

    // Фрагменты трех вкладок в TabLayout:
    val librarySceneFragment: LibrarySceneFragment = LibrarySceneFragment()
    val playSceneFragment: PlaySceneFragment = PlaySceneFragment()
    val playlistFragment: PlaylistFragment = PlaylistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectedScene = player_tabs
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab.position == 2 || tab.position == 0) hideBottomNavigationView()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                if(tab.position == 2 || tab.position == 0) showBottomNavigationView()
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
        moreListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            arrayOf("Настройки",
                    "О приложении"))
    }

    override fun initPlayer(song: Song) {
        playSceneFragment.initializePlayer(song)
        playSceneFragment.play()
        // (supportFragmentManager.findFragmentById(R.id.play_scene) as PlaySceneFragment).initializePlayer(songId)
    }
    override fun play(){
        playSceneFragment.play()
    }
    override fun pause(){
        playSceneFragment.pause()
    }

    override fun mediaPlayerPlaying(): Boolean {
        if(playSceneFragment.mediaPlayer == null) return false
        return playSceneFragment.mediaPlayer!!.isPlaying
    }
    private fun selectScene(newScene: View) {
        selectedScene?.visibility = View.GONE
        selectedScene = newScene
        selectedScene?.visibility = View.VISIBLE
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(librarySceneFragment, "Библиотека")
        adapter.addFragment(playSceneFragment, "Трек")
        adapter.addFragment(playlistFragment, "Плейлист")
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }

    fun hideBottomNavigationView() {
        bottomNavigationView.clearAnimation()
        bottomNavigationView.animate().translationY(bottomNavigationView.height.toFloat()).duration = 300
    }

    fun showBottomNavigationView() {
        bottomNavigationView.clearAnimation()
        bottomNavigationView.animate().translationY(0f).duration = 300
    }
}
