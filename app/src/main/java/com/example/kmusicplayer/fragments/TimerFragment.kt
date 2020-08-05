package com.example.kmusicplayer.fragments

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.kmusicplayer.R

class TimerFragment : Fragment() {

    var myActivity: Activity? = null

    var showTimeCD: TextView? = null
    var timeInSleep: EditText? = null
    var recyclerView: RecyclerView? = null
    var sleepButton: Button? = null
    var setButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        activity.title = "Now Playing"
        showTimeCD = view?.findViewById(R.id.timeCD)
        timeInSleep = view?.findViewById(R.id.edit_text_input)
        sleepButton = view?.findViewById(R.id.angry_btn)
        setButton = view?.findViewById(R.id.button_set)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }




}