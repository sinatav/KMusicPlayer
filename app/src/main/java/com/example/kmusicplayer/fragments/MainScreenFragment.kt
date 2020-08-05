package com.example.kmusicplayer.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.kmusicplayer.R
import com.example.kmusicplayer.Songs
import com.example.kmusicplayer.adapters.MainScreenAdapter
import java.util.*
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import java.util.*


class MainScreenFragment : Fragment() {
    private var _mainScreenAdapter: MainScreenAdapter? = null
    var positionOfTrack: Int = 0

    var myActivityObj: Activity? = null
    var visibleLayout: RelativeLayout? = null

    var listOfSongs: TextView? = null

    var lackSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null

    var getSongsList: ArrayList<Songs>? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        setHasOptionsMenu(true)
        activity!!.title = "All Songs"
        recyclerView = view?.findViewById(R.id.contentMain)
        lackSongs = view?.findViewById(R.id.noSongs)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarMainScreen)


        visibleLayout = view?.findViewById(R.id.visibleLayout)

        listOfSongs = view?.findViewById(R.id.songTitleMainScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsFromPhone()

        // sort the songs part

        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val actionSortAscending = prefs?.getString("action_sort_ascending", "true")
        val actionSortRecent = prefs?.getString("action_sort_recent", "false")

        if (getSongsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            lackSongs?.visibility = View.VISIBLE
        } else {
            _mainScreenAdapter =
                MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivityObj as Context)
            val mLayoutManager = LinearLayoutManager(myActivityObj)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }
        if (getSongsList != null) {

            if (actionSortRecent!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (actionSortAscending!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }

        bottomBarSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.clear()
        inflater.inflate(R.menu.main, menu)

//        val searchManager: SearchManager = myActivityObj?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(myActivityObj?.componentName))

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val switcher = item.itemId

        if (switcher == R.id.action_sort_recent) {
            val sortAction = myActivityObj?.getSharedPreferences(
                "action_sort",
                Context.MODE_PRIVATE
            )?.edit()
            sortAction?.putString("action_sort_ascending", "false")
            sortAction?.putString("action_sort_recent", "true")
            sortAction?.apply()

            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
            return false
        } else if (switcher == R.id.action_sort_ascending) {

            val sortAction = myActivityObj?.getSharedPreferences(
                "action_sort",
                Context.MODE_PRIVATE
            )?.edit()
            sortAction?.putString("action_sort_ascending", "true")
            sortAction?.putString("action_sort_recent", "false")
            sortAction?.apply()

            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }

            return false
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Recycle")
    fun getSongsFromPhone(): ArrayList<Songs> {
        val arrayList = ArrayList<Songs>()
        val contentResolver = myActivityObj?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

            while (songCursor.moveToNext()) {
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)

                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)


                arrayList.add(
                    Songs(
                        currentId, currentTitle, currentArtist, currentData,
                        currentDate
                    )
                )
            }
        }
        return arrayList
    }

    private fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            listOfSongs?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                listOfSongs?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
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
            args.putInt(
                "SongId",
                SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int
            )
            args.putInt(
                "songPosition",
                SongPlayingFragment.Statified.currentSongHelper?.currentPosition as Int
            )

            args.putString(
                "songArtist",
                SongPlayingFragment.Statified.currentSongHelper?.songArtist
            )

            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("MainScreenBottomBar", "success")

            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)

            songPlayingFragment.arguments = args

            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment, songPlayingFragment, "SongPlayingFragment")
                ?.addToBackStack("SongPlayingFragment")
                ?.commit()

        }

        playPauseButton?.setOnClickListener {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {

                SongPlayingFragment.Statified.mediaPlayer?.pause()
                positionOfTrack = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(positionOfTrack)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivityObj = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivityObj = activity
    }


    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }


}
