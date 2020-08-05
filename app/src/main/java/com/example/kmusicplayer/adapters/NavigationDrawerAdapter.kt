package com.example.kmusicplayer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
import com.example.kmusicplayer.R
import com.example.kmusicplayer.activities.MainActivity
import com.example.kmusicplayer.fragments.FavoriteFragment
import com.example.kmusicplayer.fragments.MainScreenFragment
import com.example.kmusicplayer.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList:ArrayList<String>, _getImages: IntArray, _context: Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){

    var contentList: ArrayList<String>? =null
    var getImages: IntArray? =null
    var musicContext: Context? =null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.musicContext = _context
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder.text_GET?.text = contentList?.get(position)


        holder.contentHolder?.setOnClickListener {

            if (position == 0){
                val mainScreenFragment = MainScreenFragment()
                (musicContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, mainScreenFragment)
                    .addToBackStack("MainFrag")
                    .commit()
            }
            else if(position == 2){
                val settingsFragment = SettingsFragment()
                (musicContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, settingsFragment)
                    .addToBackStack("SettingsFrag")
                    .commit()
            }
            else if(position == 1){
                val favoriteFragment = FavoriteFragment()
                (musicContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, favoriteFragment)
                    .addToBackStack("FavFrag")
                    .commit()
            }

            MainActivity.Statified.drawerLayout?.closeDrawers()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_navigationdrawer,parent,false)
        return NavViewHolder(
            itemView
        )
    }
    override fun getItemCount(): Int {
        return 4
    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var icon_GET : ImageView? = null
        var text_GET : TextView? = null
        var contentHolder: RelativeLayout? = null
        init{
            icon_GET =itemView?.findViewById(R.id.icon_navdrawer)
            text_GET = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}