package com.example.kmusicplayer.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.kmusicplayer.R
import com.example.kmusicplayer.SongDescription
import com.example.kmusicplayer.Songs
import com.example.kmusicplayer.activities.KDatabase
import com.example.kmusicplayer.activities.SeekBarController
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat


class SongPlayingFragment : Fragment() {

    object Statified {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButtom: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: SongDescription? = null
        var audioVisualisation: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var favoriteContent: KDatabase? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME: String = "ShakeFeature"

        var updateSongTime = object : Runnable {
            override fun run() {
                try {
                    val getCurrent = mediaPlayer?.currentPosition
                    val tempSecs = (TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                            TimeUnit.MILLISECONDS.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong())
                            )) % 60
                    if (tempSecs < 1) {
                        startTimeText?.text = String.format(
                            "%d:00",
                            TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong())
                        )
                    } else if (tempSecs < 10) {
                        startTimeText?.text = String.format(
                            "%d:0%d",
                            TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()),
                            tempSecs
                        )
                    } else {
                        startTimeText?.text = String.format(
                            "%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()),
                            tempSecs
                        )
                    }
                    seekbar?.progress = getCurrent.toInt()
                    Handler().postDelayed(this, 1000)
                } catch (e: Exception) {
                    // how to catch?
                }
            }
        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)

                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songId = nextSong?.songId as Long
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition

                    updateTextViews(
                        Statified.currentSongHelper?.songTitle as String,
                        Statified.currentSongHelper?.songArtist as String
                    )

                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.myActivity?.let {
                            Statified.mediaPlayer?.setDataSource(
                                it,
                                Uri.parse(Statified.currentSongHelper?.songPath)
                            )
                        }
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if (Statified.favoriteContent?.checkIfIdExists(
                    Statified.currentSongHelper?.songId?.toInt() as Int
                ) as Boolean
            ) {
                Statified.fab?.setImageDrawable(
                    Statified.myActivity?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.favorite_on
                        )
                    })
            } else {
                Statified.fab?.setImageDrawable(Statified.myActivity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.favorite_off
                    )
                })
            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            var songArtistUpdated = songArtist
            if (songTitle.equals("<unknown>", true)) {
                songTitleUpdated = "Unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "Unknown"
            }
            Statified.songTitleView?.text = songTitleUpdated
            Statified.songArtistView?.text = songArtistUpdated
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition

            Statified.seekbar?.max = finalTime

            val tempSec = (TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                    ) % 60)
            if (tempSec < 1) {
                Statified.startTimeText?.text = String.format(
                    "%d:00",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                )
            } else if (tempSec < 10) {
                Statified.startTimeText?.text = String.format(
                    "%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    (TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - tempSec)
                )
            } else {
                Statified.startTimeText?.text = String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    (TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                            ) % 60)
                )
            }

            Statified.endTimeText?.text = String.format(
                "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())
                        )
            )

            Statified.seekbar?.progress = startTime

            Handler().postDelayed(Statified.updateSongTime, 1000)
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition = Statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                val randomObject = Random()
                val randomPosition =
                    randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition

            }
            if (Statified.currentPosition == Statified.fetchSongs?.size)
                Statified.currentPosition = 0

            Statified.currentSongHelper?.isLoop = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songId as Long
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )

            Statified.mediaPlayer?.reset()
            try {
                Statified.myActivity?.let {
                    Statified.mediaPlayer?.setDataSource(
                        it,
                        Uri.parse(Statified.currentSongHelper?.songPath)
                    )
                }
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (Statified.favoriteContent?.checkIfIdExists(
                    Statified.currentSongHelper?.songId?.toInt() as Int
                ) as Boolean
            ) {
                Statified.fab?.setImageDrawable(Statified.myActivity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.favorite_on
                    )
                })
            } else {
                Statified.fab?.setImageDrawable(Statified.myActivity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.favorite_off
                    )
                })
            }
        }

        fun playPrevious(check: String) {
            if (check.equals("PlayNextNormal", true))
                Statified.currentPosition = Statified.currentPosition - 1
            else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                val randomObject = Random()
                val randomPosition =
                    randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition
            }

            if (Statified.currentPosition == -1)
                Statified.currentPosition = (Statified.fetchSongs?.size as Int) - 1

            Statified.currentSongHelper?.isLoop = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songId as Long
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )

            Statified.mediaPlayer?.reset()
            try {
                Statified.myActivity?.let {
                    Statified.mediaPlayer?.setDataSource(
                        it,
                        Uri.parse(Statified.currentSongHelper?.songPath)
                    )
                }
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (Statified.favoriteContent?.checkIfIdExists(
                    Statified.currentSongHelper?.songId?.toInt() as Int
                ) as Boolean
            ) {
                Statified.fab?.setImageDrawable(Statified.myActivity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.favorite_on
                    )
                })
            } else {
                Statified.fab?.setImageDrawable(Statified.myActivity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.favorite_off
                    )
                })
            }
        }
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_playing, container, false)

        setHasOptionsMenu(true)

        activity!!.title = "Now Playing"

        Statified.seekbar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButtom = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favoriteButton)

        Statified.songTitleView?.isSelected
        Statified.songArtistView?.isSelected

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Statified.audioVisualisation = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualisation?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
            Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Statified.audioVisualisation?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Statified.audioVisualisation?.release()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.currentSongHelper = SongDescription()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false

        Statified.favoriteContent =
            KDatabase(Statified.myActivity)

        var path: String? = null
        var songTitle: String? = null
        var songArtist: String? = null
        var songId: Long = 0

        try {
            path = arguments?.getString("path")
            songTitle = arguments?.getString("songTitle")
            songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("SongId")!!.toLong()
            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = songTitle
            Statified.currentSongHelper?.songArtist = songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        val fromMainScreenBottomBar = arguments?.get("MainScreenBottomBar") as? String
        if (fromFavBottomBar != null) {
            Statified.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
        } else if (fromMainScreenBottomBar != null) {
            Statified.mediaPlayer = MainScreenFragment.Statified.mediaPlayer
        } else {


            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                Statified.myActivity?.let { Statified.mediaPlayer?.setDataSource(it, Uri.parse(path)) }
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaPlayer?.start()
        }
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

        if (Statified.mediaPlayer?.isPlaying as Boolean) {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }

        clickHandler()

        val visualizationHandler =
            DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context,
                0)
        Statified.audioVisualisation?.linkTo(visualizationHandler)

        val prefsForShuffle =
            Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,
                Context.MODE_PRIVATE)
        val isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        val prefsForLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
            Context.MODE_PRIVATE)
        val isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            Statified.currentSongHelper?.isLoop = true
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            Statified.currentSongHelper?.isLoop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

        if (Statified.favoriteContent?.checkIfIdExists(Statified.
                currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(
                Statified.myActivity!!,
                R.drawable.favorite_on))
        } else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(
                Statified.myActivity!!,
                R.drawable.favorite_off))
        }

        seekBarHandler()
    }

    private fun clickHandler() {

        Statified.fab?.setOnClickListener {
            //            Log.i("Inside Click Handler", "-----------------Favorite Button Clicked---------------------")
            if (Statified.favoriteContent?.checkIfIdExists(Statified.
                    currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(Statified.myActivity?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1,
                        R.drawable.favorite_off)
                })
                Statified.favoriteContent?.deleteFavorite(Statified.
                    currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity, "Removed from Favorites",
                    Toast.LENGTH_SHORT).show()
            } else {
                Statified.favoriteContent?.storeAsFavorite(Statified.
                    currentSongHelper?.songId?.toInt(), Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle,
                    Statified.currentSongHelper?.songPath)
                Statified.fab?.setImageDrawable(Statified.myActivity?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1,
                        R.drawable.favorite_on)
                })
                Toast.makeText(Statified.myActivity, "Added to Favorites",
                    Toast.LENGTH_SHORT).show()
            }
        }

        Statified.shuffleImageButton?.setOnClickListener {

            val editorShuffle =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,
                    Context.MODE_PRIVATE)?.edit()
            val editorLoop =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                    Context.MODE_PRIVATE)?.edit()
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        }

        Statified.nextImageButton?.setOnClickListener {
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        }

        Statified.previousImageButtom?.setOnClickListener {
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playPrevious("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playPrevious("PlayNextNormal")
            }
        }

        Statified.loopImageButton?.setOnClickListener {

            val editorShuffle =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,
                    Context.MODE_PRIVATE)?.edit()
            val editorLoop =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                    Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        }

        Statified.playpauseImageButton?.setOnClickListener {


            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()

                Statified.currentSongHelper?.isPlaying = false
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaPlayer?.start()

                Statified.currentSongHelper?.isPlaying = true
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

    }

    private fun seekBarHandler() {
        val seekBarListener =
            SeekBarController()
        Statified.seekbar?.setOnSeekBarChangeListener(seekBarListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager =
            Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH

        bindShakeListener()
    }

    private fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]

                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if (mAcceleration > 12) {
                    val prefs =
                        Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,
                            Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)

                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val item: MenuItem? = menu.findItem(R.id.action_redirect)
        item?.isVisible = true

        val item2: MenuItem? = menu.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

}
