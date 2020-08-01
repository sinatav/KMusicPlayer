package com.example.kmusicplayer.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kmusicplayer.Songs

class KDatabase : SQLiteOpenHelper {
    private var _songList = ArrayList<Songs>()

    object Description {
        const val DB_Version = 1
        const val DB_Name = "FavoritesDatabase"
        const val tableName = "FavoritesTable"
        const val cID = "SongID"
        const val songTitle = "SongTitle"
        const val songArtist = "SongArtist"
        const val songPath = "SongPath"
    }

    // create table query in database
    @SuppressLint("SQLiteString")
    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL(
            "CREATE TABLE " + Description.tableName + " ( " +
                    Description.cID + " INTEGER, " + Description.songArtist + " STRING, " +
                    Description.songTitle + " STRING, " + Description.songPath +
                    " STRING);"
        )
    }

    // automatically added
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File
        // | Settings | File Templates.
    }

    constructor(
        context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : super(
        context, name, factory, version
    )

    constructor(context: Context?) : super(
        context, Description.DB_Name, null,
        Description.DB_Version
    )

    // inserting in database
    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Description.cID, id)
        contentValues.put(Description.songArtist, artist)
        contentValues.put(Description.songTitle, songTitle)
        contentValues.put(Description.songPath, path)
        db.insert(Description.tableName, null, contentValues)
        db.close()
    }

    @SuppressLint("Recycle")
    fun queryDBList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            val queryParams = "select * from " + Description.tableName
            val cSor = db.rawQuery(queryParams, null)

            if (cSor.moveToFirst()) {
                do {
                    val id = cSor.getInt(cSor.getColumnIndexOrThrow(Description.cID))
                    val artist = cSor.getString(cSor.getColumnIndexOrThrow(Description.songArtist))
                    val title = cSor.getString(cSor.getColumnIndexOrThrow(Description.songTitle))
                    val path = cSor.getString(cSor.getColumnIndexOrThrow(Description.songPath))

                    _songList.add(Songs(id.toLong(), title, artist, path, 0))

                } while (cSor.moveToNext())
            } else {
                return null
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return _songList
    }

    @SuppressLint("Recycle")
    fun checkIfIdExists(_id: Int): Boolean {
        var storeId = -1090
        val db = this.readableDatabase
        val queryParams = "SELECT * FROM " + Description.tableName + " WHERE SongId = '$_id'"
        val cSor = db.rawQuery(queryParams, null)
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Description.cID))
            } while (cSor.moveToNext())
        } else {
            return false
        }
        return storeId != -1090
    }

    fun deleteFavorite(_id: Int) {
        val db = this.writableDatabase
        db.delete(Description.tableName, Description.cID + " = " + _id, null)
        db.close()
    }

    @SuppressLint("Recycle")
    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        val queryParams = "SELECT * FROM " + Description.tableName
        val cSor = db.rawQuery(queryParams, null)
        if (cSor.moveToFirst()) {
            do {
                counter++
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }
}