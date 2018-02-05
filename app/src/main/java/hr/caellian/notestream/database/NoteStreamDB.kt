package hr.caellian.notestream.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.lib.Constants.DB_PLAYABLES_ID
import hr.caellian.notestream.lib.Constants.DB_PLAYLIST_INFO_ID

import hr.caellian.notestream.lib.Constants.TRACK_ID
import hr.caellian.notestream.lib.Constants.TRACK_TIMESTAMP
import hr.caellian.notestream.lib.Constants.TRACK_PRIORITY

import hr.caellian.notestream.lib.Constants.DB_PLAYLIST_PREFIX
import hr.caellian.notestream.lib.Constants.PLAYLIST_AUTHOR
import hr.caellian.notestream.lib.Constants.PLAYLIST_CAPACITY
import hr.caellian.notestream.lib.Constants.PLAYLIST_ID
import hr.caellian.notestream.lib.Constants.PLAYLIST_LABEL

import hr.caellian.notestream.lib.Constants.SQL_TYPE_INTEGER
import hr.caellian.notestream.lib.Constants.SQL_TYPE_TEXT
import hr.caellian.notestream.lib.Constants.SQL_NOT_NULL
import hr.caellian.notestream.lib.Constants.SQL_PRIMARY_KEY
import hr.caellian.notestream.lib.Constants.TRACK_ALBUM
import hr.caellian.notestream.lib.Constants.TRACK_AUTHOR
import hr.caellian.notestream.lib.Constants.TRACK_COVER_PATH
import hr.caellian.notestream.lib.Constants.TRACK_END
import hr.caellian.notestream.lib.Constants.TRACK_GENRE
import hr.caellian.notestream.lib.Constants.TRACK_LENGTH
import hr.caellian.notestream.lib.Constants.TRACK_LYRICS
import hr.caellian.notestream.lib.Constants.TRACK_PATH
import hr.caellian.notestream.lib.Constants.TRACK_RATING
import hr.caellian.notestream.lib.Constants.TRACK_SOURCE
import hr.caellian.notestream.lib.Constants.TRACK_START
import hr.caellian.notestream.lib.Constants.TRACK_TITLE
import hr.caellian.notestream.lib.Constants.TRACK_TRACK
import hr.caellian.notestream.lib.Constants.TRACK_YEAR

/**
 * Created by caellian on 02/01/18.
 */
class NoteStreamDB : SQLiteOpenHelper(NoteStream.instance, Constants.DB_NAME, null, Constants.DB_VERSION) {

    fun createPlayablesTable() {
        val statement = "CREATE TABLE IF NOT EXISTS $DB_PLAYABLES_ID (" +
                "$TRACK_ID $SQL_TYPE_TEXT $SQL_PRIMARY_KEY, " +
                "$TRACK_SOURCE $SQL_TYPE_TEXT $SQL_NOT_NULL, " +
                "$TRACK_PATH $SQL_TYPE_TEXT $SQL_NOT_NULL, " +

                "$TRACK_TITLE $SQL_TYPE_TEXT $SQL_NOT_NULL, " +
                "$TRACK_AUTHOR $SQL_TYPE_TEXT, " +
                "$TRACK_ALBUM $SQL_TYPE_TEXT, " +
                "$TRACK_YEAR $SQL_TYPE_INTEGER, " +
                "$TRACK_TRACK $SQL_TYPE_INTEGER, " +
                "$TRACK_GENRE $SQL_TYPE_TEXT, " +
                "$TRACK_RATING $SQL_TYPE_INTEGER, " +
                "$TRACK_LYRICS $SQL_TYPE_TEXT, " +

                "$TRACK_START $SQL_TYPE_INTEGER, " +
                "$TRACK_END $SQL_TYPE_INTEGER, " +
                "$TRACK_LENGTH $SQL_TYPE_INTEGER, " +
                "$TRACK_COVER_PATH $SQL_TYPE_TEXT" +
                ");"

        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun createPlaylistInfoTable() {
        val statement = "CREATE TABLE IF NOT EXISTS $DB_PLAYLIST_INFO_ID (" +
                "$PLAYLIST_ID $SQL_TYPE_TEXT $SQL_PRIMARY_KEY, " +
                "$PLAYLIST_AUTHOR $SQL_TYPE_TEXT, " +
                "$PLAYLIST_LABEL $SQL_TYPE_TEXT, " +
                "$PLAYLIST_CAPACITY $SQL_TYPE_TEXT, " +
                ");"

        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        createPlayablesTable()
        createPlaylistInfoTable()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var statement = "DROP TABLE IF EXISTS $DB_PLAYABLES_ID;"
        writableDatabase.execSQL(statement)
        statement = "DROP TABLE IF EXISTS $DB_PLAYLIST_INFO_ID;"
        writableDatabase.execSQL(statement)
        writableDatabase.close()

        onCreate(db)
    }

    fun registerPlaylist(pl: Playlist, id: String = "", author: String = "", label: String = "", capacity: Int = 512) {
        var statement = "CREATE TABLE IF NOT EXISTS $DB_PLAYLIST_PREFIX${pl.id} (" +
                "$TRACK_ID $SQL_TYPE_TEXT $SQL_PRIMARY_KEY, " +
                "$TRACK_PRIORITY $SQL_TYPE_INTEGER $SQL_NOT_NULL, " +
                "$TRACK_TIMESTAMP $SQL_TYPE_INTEGER $SQL_NOT_NULL" +
                ");"

        writableDatabase.execSQL(statement)

        statement = "INSERT OR IGNORE INTO TABLE IF NOT EXISTS $DB_PLAYLIST_INFO_ID " +
                "($PLAYLIST_ID, $PLAYLIST_AUTHOR, $PLAYLIST_LABEL, $PLAYLIST_CAPACITY) VALUES " +
                "(\"$id\", \"$author\", \"$label\", $capacity);"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun updatePlaylist(pl: Playlist) {
        val statement = "INSERT OR REPLACE INTO TABLE IF NOT EXISTS $DB_PLAYLIST_INFO_ID " +
                "($PLAYLIST_ID, $PLAYLIST_AUTHOR, $PLAYLIST_LABEL, $PLAYLIST_CAPACITY) VALUES " +
                "(\"${pl.id}\", \"${pl.author}\", \"${pl.label}\", ${pl.capacity});"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun addPlayable(p: Playable) {
        val pInfo = p.info
        val statement = "INSERT OR REPLACE INTO TABLE IF NOT EXISTS $DB_PLAYLIST_INFO_ID " +
                "($TRACK_ID, $TRACK_SOURCE, $TRACK_PATH, $TRACK_TITLE, $TRACK_AUTHOR, " +
                "$TRACK_ALBUM, $TRACK_YEAR, $TRACK_TRACK, $TRACK_GENRE, $TRACK_RATING, " +
                "$TRACK_LYRICS, $TRACK_START, $TRACK_END, $TRACK_LENGTH, $TRACK_COVER_PATH) VALUES " +
                "(\"${p.id}\", \"${p.playableSource}\", \"${p.path}\", \"${pInfo.title ?: ""}\", " +
                "\"${pInfo.author ?: ""}\", \"${pInfo.album ?: ""}\", ${pInfo.year ?: 0}, ${pInfo.track ?: 0}, " +
                "\"${pInfo.genre ?: ""}\", ${pInfo.rating}, \"${pInfo.lyrics ?: ""}\", ${pInfo.start ?: 0}, " +
                "${pInfo.end ?: 0}, ${pInfo.length ?: 0}, \"${pInfo.coverPath}\");"

        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun removePlayable(p: Playable) {
        val statement = "DROP FROM $DB_PLAYABLES_ID WHERE $TRACK_ID = \"${p.id}\";"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun removePlaylist(pl: Playlist) {
        var statement = "DROP TABLE IF EXISTS $DB_PLAYLIST_PREFIX${pl.id};"
        writableDatabase.execSQL(statement)

        statement = "DROP FROM $DB_PLAYLIST_INFO_ID WHERE $PLAYLIST_ID = \"${pl.id}\";"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun removePlayableFrom(p: Playable, from: Playlist) {
        val statement = "DROP FROM $DB_PLAYLIST_PREFIX${PlayerService.pl.id} WHERE $TRACK_ID = \"${p.id}\";"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun addPlayableTo(p: Playable, to: Playlist) {
        addPlayable(p)

        val statement = "INSERT OR REPLACE INTO TABLE IF NOT EXISTS $DB_PLAYLIST_INFO_ID " +
                "($TRACK_ID, $TRACK_PRIORITY, $TRACK_TIMESTAMP) VALUES " +
                "(\"${p.id}\", \"${to.size()}\", ${System.currentTimeMillis()});"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }

    fun clearPlaylist(pl: Playlist) {
        val statement = "DROP FROM $DB_PLAYLIST_PREFIX${PlayerService.pl.id};"
        writableDatabase.execSQL(statement)
        writableDatabase.close()
    }
}