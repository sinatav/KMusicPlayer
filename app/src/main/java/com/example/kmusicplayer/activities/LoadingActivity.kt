package com.example.kmusicplayer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.kmusicplayer.R

@SuppressLint("Registered")
class LoadingActivity: AppCompatActivity() {

    var permissionStrings = arrayOf(
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val myFadeInAnim = findViewById<ImageView>(R.id.logoFade)
        val fadeFade = AnimationUtils.loadAnimation(this, R.anim.fadein)
        myFadeInAnim.startAnimation(fadeFade)
    }
}