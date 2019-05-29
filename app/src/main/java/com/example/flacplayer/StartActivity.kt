package com.example.flacplayer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.start_layout.*

class StartActivity :Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_layout)
        startButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}