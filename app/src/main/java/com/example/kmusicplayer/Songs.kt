package com.example.kmusicplayer

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.Comparator

class Songs(var songId: Long, var songTitle: String, var artist: String, var songData: String,
            var dateAdded: Long) : Parcelable{
//    constructor(parcel: Parcel) : this(
//        parcel.readLong(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readLong()
//    ) {
//    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeLong(songId)
//        parcel.writeString(songTitle)
//        parcel.writeString(artist)
//        parcel.writeString(songData)
//        parcel.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

//    companion object CREATOR : Parcelable.Creator<Songs> {
//        override fun createFromParcel(parcel: Parcel): Songs {
//            return Songs(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Songs?> {
//            return arrayOfNulls(size)
//        }
//    }

    object Statified{
        var nameComparator: Comparator<Songs> = Comparator<Songs>{song1, song2 ->
            val songOne = song1.songTitle.toUpperCase(Locale.ROOT)
            val songTwo = song2.songTitle.toUpperCase(Locale.ROOT)
            songOne.compareTo(songTwo)
        }
        var dateComparator: Comparator<Songs> = Comparator<Songs>{song1, song2 ->
            val dateOne = song1.dateAdded.toDouble()
            val dateTwo = song2.dateAdded.toDouble()
            dateOne.compareTo(dateTwo)
        }
    }
}