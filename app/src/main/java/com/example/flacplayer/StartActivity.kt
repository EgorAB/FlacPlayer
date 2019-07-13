package com.example.flacplayer

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.start_layout.*
import org.telegram.passport.*

class StartActivity : Activity() {
    private val TG_PASSPORT_RESULT = 352
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestCode: Int = 1
        if(ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                requestCode)
        setContentView(R.layout.start_layout)
        startButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        telegramButton.setOnClickListener {
            try {
                val tgRequest = TelegramPassport.AuthRequest()
                tgRequest.botID = 794156716
                tgRequest.publicKey = "AAHwYavVNRwQ7AKveiqSp5I3epvf2BAOtdM"
                tgRequest.nonce = "Hello"
                tgRequest.scope = PassportScope(
                    PassportScopeElementOneOfSeveral(PassportScope.PASSPORT, PassportScope.IDENTITY_CARD).withSelfie(),
                    PassportScopeElementOne(PassportScope.PERSONAL_DETAILS).withNativeNames(),
                    PassportScope.DRIVER_LICENSE,
                    PassportScope.ADDRESS,
                    PassportScope.ADDRESS_DOCUMENT,
                    PassportScope.PHONE_NUMBER
                )
                TelegramPassport.request(this, tgRequest, TG_PASSPORT_RESULT)
            } catch (e: Exception) {

            }
        }
    }
}