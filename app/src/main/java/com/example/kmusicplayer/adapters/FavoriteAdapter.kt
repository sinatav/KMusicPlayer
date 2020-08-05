package com.example.kmusicplayer.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
//import androidx.fragment.app.FragmentActivity
//import androidx.recyclerview.widget.RecyclerView
import com.example.kmusicplayer.R
import com.example.kmusicplayer.Songs
import com.example.kmusicplayer.fragments.SongPlayingFragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView


class FavoriteAdapter(_songDetails: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist
        holder.contentHolder?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("SongId", songObject?.songId?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)

            songPlayingFragment.arguments = args


            if (SongPlayingFragment.Statified.mediaPlayer != null &&
                SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                SongPlayingFragment.Statified.mediaPlayer?.release()
            }

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment, "SongPlayingFragment")
                .addToBackStack("SongPlayingFragmentFav")
                .commit()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails == null)
            return 0

        return (songDetails as ArrayList<Songs>).size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById(R.id.trackTitle)
            trackArtist = view.findViewById(R.id.trackArtist)
            contentHolder = view.findViewById(R.id.contentRow)
        }
    }
}