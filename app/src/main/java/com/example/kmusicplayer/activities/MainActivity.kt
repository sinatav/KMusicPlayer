package com.example.kmusicplayer.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.widget.Toolbar
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.recyclerview.widget.DefaultItemAnimator
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
import com.example.kmusicplayer.R
import com.example.kmusicplayer.adapters.NavigationDrawerAdapter
import com.example.kmusicplayer.fragments.MainScreenFragment
import com.example.kmusicplayer.fragments.SongPlayingFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView



class MainActivity : AppCompatActivity() {

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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Statified.drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("")

        val toggle = ActionBarDrawerToggle(
            this@MainActivity, Statified.drawerLayout,
            toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        Statified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
            .commit()

        val navigationAdapterObj = NavigationDrawerAdapter(
            navigationDrawerIconsList,
            navDrawerIcons, this
        )
        navigationAdapterObj.notifyDataSetChanged()

        val navigationRecyclerView = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigationRecyclerView.layoutManager = LinearLayoutManager(this)
        navigationRecyclerView.itemAnimator = DefaultItemAnimator()
        navigationRecyclerView.adapter = navigationAdapterObj
        navigationRecyclerView.setHasFixedSize(true)

        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val preIntent = PendingIntent.getActivity(
            this@MainActivity,
            System.currentTimeMillis().toInt() as Int, intent, 0
        )

        trackNotificationBuilder = Notification.Builder(this)
            .setContentTitle("KMP is playing music")
            .setSmallIcon(R.drawable.echo_logo)
            .setContentIntent(preIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        Statified.notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        try {
            Statified.notificationManager?.cancel(1111)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onResume()
    }

    override fun onStop() {
        try {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.notificationManager?.notify(1111, trackNotificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onStop()
    }

    override fun onDestroy() {
        try {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.notificationManager?.notify(1111, trackNotificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

}
