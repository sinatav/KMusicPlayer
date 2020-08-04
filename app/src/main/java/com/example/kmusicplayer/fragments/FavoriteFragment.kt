package com.example.kmusicplayer.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kmusicplayer.R
import com.example.kmusicplayer.Songs
import com.example.kmusicplayer.activities.KDatabase

class FavoriteFragment : Fragment() {


    var myActivity: Activity? = null

    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: KDatabase? = null

    var rlist: ArrayList<Songs>? = null
    var getListFromDatabase: ArrayList<Songs>? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        setHasOptionsMenu(true)
        activity!!.title = "Now Playing"
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)
        //these are the song details on the favorite part

        noFavorites = view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        return view
    }


    @SuppressLint("Recycle")
    private fun getSongsFromPhone(): ArrayList<Songs> {
        val arrayList = ArrayList<Songs>()
        val contentResolver = myActivity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        // media store is used to support different media types on phone

        val songCursor = contentResolver?.query(songUri, null, null,
            null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)


            // the song cursor
            // moveCursor(songCursor, arrayList)
            while (songCursor.moveToNext()) {

                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)

                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)


                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData,
                    currentDate))
            }
        }
        return arrayList
    }

    private fun moveCursor(songCursor: Activity?, arrayList: ArrayList<Songs>) {
//        while (songCursor.moveToNext()) {
//            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
//            val currentId = songCursor.getLong(songId)
//            val currentTitle = songCursor.getString(songTitle)
//            val currentArtist = songCursor.getString(songArtist)
//            val currentData = songCursor.getString(songData)
//            val currentDate = songCursor.getLong(dateIndex)
//            arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData,
//                    currentDate))
    }

    private fun songDataAdd(arrayList: ArrayList<Songs>) {

    }


    private fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
                SongPlayingFragment.Staticated.onSongComplete()
            }

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("SongId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar", "success")
            songPlayingFragment.arguments = args       // linking songPlayingFragment to Bundle obj

            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment, songPlayingFragment)
                ?.addToBackStack("SongPlayingFragment")
                ?.commit()

        }

        playPauseButton?.setOnClickListener {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }

    private fun showSongsOnFav() {
        var size: Int
        size = favoriteContent?.checkSize() as Int
        if (size > 0) {
            rlist = ArrayList()
            getListFromDatabase = favoriteContent?.queryDBList()
            val fetchListFromDevice = getSongsFromPhone()
            for (i in 0 until fetchListFromDevice.size) {
                for (j in 0 until getListFromDatabase?.size as Int) {

                    if (getListFromDatabase?.get(j)?.songId ===
                        (fetchListFromDevice.get(i)?.songId)) {
                        rlist?.add((getListFromDatabase as ArrayList<Songs>)[j])
                    }
                }
            }

            if (rlist == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
//                val favoriteAdapter = FavoriteAdapter(rlist as ArrayList<Songs>,
//                    myActivity as Context
//                )
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
//                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = context as Activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favoriteContent = KDatabase(myActivity)
        showSongsOnFav()
        bottomBarSetup()

    }
}