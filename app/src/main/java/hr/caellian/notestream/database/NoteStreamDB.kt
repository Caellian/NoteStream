package hr.caellian.notestream.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellian on 02/01/18.
 */
class NoteStreamDB: SQLiteOpenHelper(NoteStream.instance, Constants.DB_NAME, null, Constants.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun registerPlayable(p: Playable) {
        writableDatabase.close()
    }

    fun registerPlaylist(pl: Playlist) {
        writableDatabase.execSQL("")

        writableDatabase.close()
    }
}