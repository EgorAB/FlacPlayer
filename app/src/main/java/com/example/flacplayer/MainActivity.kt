package com.example.flacplayer

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.more_layout.*
import kotlinx.android.synthetic.main.player_tabs.*
import kotlinx.android.synthetic.main.player_tabs.view.*


class MainActivity : FragmentActivity() {

    private var selectedScene: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectedScene = player_tabs
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
        moreListView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
            arrayOf("Настройки",
                    "О приложении"))
    }

    private fun selectScene(newScene: View) {
        selectedScene?.visibility = View.GONE
        selectedScene = newScene
        selectedScene?.visibility = View.VISIBLE
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(LibrarySceneFragment(), "Библиотека")
        adapter.addFragment(PlaySceneFragment(), "Трек")
        adapter.addFragment(PlaylistSceneFragment(), "Плейлист")
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }
}
