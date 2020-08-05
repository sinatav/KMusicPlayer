package com.example.kmusicplayer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
import com.example.kmusicplayer.R

@SuppressLint("Registered")
class LoadingActivity: AppCompatActivity() {

    var permissionStrings = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val myFadeInAnim = findViewById<ImageView>(R.id.logoFade)
        val fadeFade = AnimationUtils.loadAnimation(this, R.anim.fadein)
        myFadeInAnim.startAnimation(fadeFade)

        if (!hasPermission(this@LoadingActivity, *permissionStrings)) {
            ActivityCompat.requestPermissions(this@LoadingActivity, permissionStrings, 131) // Any arbitrary requestCode
        } else {
            displayLoadingScreen()
        }
    }

    private fun hasPermission(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun displayLoadingScreen() {
        Handler().postDelayed({
            val startAct = Intent(this@LoadingActivity, MainActivity::class.java)
            startActivity(startAct)
            this.finish()
        }, 1000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {
                    displayLoadingScreen()
                } else {
                    Toast.makeText(this@LoadingActivity, "Grant Permissions To Use App",
                        Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@LoadingActivity, "Error occurred!",
                    Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }
    }
}