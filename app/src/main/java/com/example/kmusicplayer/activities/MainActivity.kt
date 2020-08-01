package com.example.kmusicplayer.activities

import android.app.Notification
import android.app.NotificationManager
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.kmusicplayer.R

class MainActivity : AppCompatActivity() {

    var audioManager: AudioManager? = null

    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    var navDrawerIcons: IntArray = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notificationManager: NotificationManager? = null
    }

    var trackNotificationBuilder: Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        try {
            Statified.notificationManager?.cancel(1111)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onStart()
    }

    override fun onResume() {
        try{
            Statified.notificationManager?.cancel(1111)
        }catch (e : Exception){
            e.printStackTrace()
        }
        super.onResume()
    }

}
