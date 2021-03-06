package com.example.kmusicplayer.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Switch
//import androidx.fragment.app.Fragment
import com.example.kmusicplayer.R
import android.support.v4.app.Fragment


class SettingsFragment : Fragment() {

    var myActivity: Activity? = null
    var shakeSwitch: Switch? = null

    object Statified {
        const val preferencesName = "ShakeFeature"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        activity?.title ?: "Settings"

        shakeSwitch = view?.findViewById(R.id.switchShake)
        return view
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
        val sharedPreferences =
            myActivity?.getSharedPreferences(Statified.preferencesName, Context.MODE_PRIVATE)
        val isAllowed = sharedPreferences?.getBoolean("feature", false)

        shakeSwitch?.isChecked = isAllowed as Boolean

        shakeSwitch?.setOnCheckedChangeListener { _, flag ->
            if (flag) {
                val editor =
                    myActivity?.getSharedPreferences(
                        Statified.preferencesName,
                        Context.MODE_PRIVATE
                    )
                        ?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                val editor =
                    myActivity?.getSharedPreferences(
                        Statified.preferencesName,
                        Context.MODE_PRIVATE
                    )
                        ?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_blank, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
