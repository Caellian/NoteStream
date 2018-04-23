/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.database

import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.lib.Constants.DB_PLAYABLES_ID
import hr.caellian.notestream.lib.Constants.DB_PLAYLIST_INFO_ID
import hr.caellian.notestream.lib.Constants.DB_PLAYLIST_PREFIX
import hr.caellian.notestream.lib.Constants.PLAYLIST_AUTHOR
import hr.caellian.notestream.lib.Constants.PLAYLIST_CAPACITY
import hr.caellian.notestream.lib.Constants.PLAYLIST_ID
import hr.caellian.notestream.lib.Constants.PLAYLIST_LABEL
import hr.caellian.notestream.lib.Constants.SQL_NOT_NULL
import hr.caellian.notestream.lib.Constants.SQL_PRIMARY_KEY
import hr.caellian.notestream.lib.Constants.SQL_TYPE_INTEGER
import hr.caellian.notestream.lib.Constants.SQL_TYPE_TEXT
import hr.caellian.notestream.lib.Constants.TRACK_ALBUM
import hr.caellian.notestream.lib.Constants.TRACK_AUTHOR
import hr.caellian.notestream.lib.Constants.TRACK_COVER_PATH
import hr.caellian.notestream.lib.Constants.TRACK_END
import hr.caellian.notestream.lib.Constants.TRACK_GENRE
import hr.caellian.notestream.lib.Constants.TRACK_ID
import hr.caellian.notestream.lib.Constants.TRACK_LENGTH
import hr.caellian.notestream.lib.Constants.TRACK_LYRICS
import hr.caellian.notestream.lib.Constants.TRACK_PATH
import hr.caellian.notestream.lib.Constants.TRACK_RATING
import hr.caellian.notestream.lib.Constants.TRACK_SOURCE
import hr.caellian.notestream.lib.Constants.TRACK_START
import hr.caellian.notestream.lib.Constants.TRACK_TIMESTAMP
import hr.caellian.notestream.lib.Constants.TRACK_TITLE
import hr.caellian.notestream.lib.Constants.TRACK_TRACK
import hr.caellian.notestream.lib.Constants.TRACK_YEAR

object NoteStreamDB : SQLiteOpenHelper(NoteStream.instance, Constants.DB_NAME, null, Constants.DB_VERSION) {

    fun createPlayablesTable(db: SQLiteDatabase) {
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

        db.execSQL(statement)
    }

    fun createPlaylistInfoTable(db: SQLiteDatabase) {
        val statement = "CREATE TABLE IF NOT EXISTS $DB_PLAYLIST_INFO_ID (" +
                "$PLAYLIST_ID $SQL_TYPE_TEXT $SQL_PRIMARY_KEY, " +
                "$PLAYLIST_AUTHOR $SQL_TYPE_TEXT, " +
                "$PLAYLIST_LABEL $SQL_TYPE_TEXT, " +
                "$PLAYLIST_CAPACITY $SQL_TYPE_TEXT" +
                ");"

        db.execSQL(statement)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createPlayablesTable(db)
        createPlaylistInfoTable(db)
//        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            var statement = "DROP TABLE IF EXISTS $DB_PLAYABLES_ID;"
            writableDatabase.execSQL(statement)
            statement = "DROP TABLE IF EXISTS $DB_PLAYLIST_INFO_ID;"
            writableDatabase.execSQL(statement)

            onCreate(db)
        }
    }

    fun syncPlaylist(pl: Playlist, id: String = "", author: String = "", label: String = "", capacity: Int = 512, close: Boolean = true) {
        var statement = "CREATE TABLE IF NOT EXISTS $DB_PLAYLIST_PREFIX${pl.id} (" +
                "$TRACK_ID $SQL_TYPE_TEXT $SQL_PRIMARY_KEY, " +
                "$TRACK_TIMESTAMP $SQL_TYPE_INTEGER $SQL_NOT_NULL" +
                ");"
        writableDatabase.execSQL(statement)

        val list = pl.playlist

        list.forEach {
            statement = "INSERT OR IGNORE INTO $DB_PLAYLIST_PREFIX${pl.id}" +
                    "($TRACK_ID, $TRACK_TIMESTAMP) VALUES " +
                    "(\"${it.id}\", ${pl.timestamps[it]});"
            writableDatabase.execSQL(statement)
        }

        val columns = arrayOf(TRACK_ID, TRACK_TIMESTAMP)
        (writableDatabase.query("$DB_PLAYLIST_PREFIX${pl.id}", columns, null, null, null, null, null))
                ?.also { c ->
                    c.moveToFirst()
                    if (c.count > 0) {
                        val itId = c.getString(0)
                        if (!list.map { it.id }.contains(itId)) {
                            getPlayable(itId)?.apply {
                                pl.add(this)
                            }
                        }
                    }
                    c.close()
                }

        writableDatabase.execSQL(statement)

        statement = "INSERT OR IGNORE INTO $DB_PLAYLIST_INFO_ID " +
                "($PLAYLIST_ID, $PLAYLIST_AUTHOR, $PLAYLIST_LABEL, $PLAYLIST_CAPACITY) VALUES " +
                "(\"$id\", \"$author\", \"$label\", $capacity);"

        writableDatabase.execSQL(statement)
//        if (close) writableDatabase.close()
    }

    fun updatePlaylist(pl: Playlist, close: Boolean = true) {
        val statement = "INSERT OR REPLACE INTO $DB_PLAYLIST_INFO_ID " +
                "($PLAYLIST_ID, $PLAYLIST_AUTHOR, $PLAYLIST_LABEL, $PLAYLIST_CAPACITY) VALUES " +
                "(\"${pl.id}\", \"${pl.author}\", \"${pl.label}\", ${pl.capacity});"

        writableDatabase.execSQL(statement)
//        if (close) writableDatabase.close()
    }

    fun getPlayable(id: String, close: Boolean = true): Playable? {
        val data = getPlayableData(id, close)

        return when (data[TRACK_SOURCE]) {
            PlayableSource.LOCAL.id -> {
                PlayableLocal(data[TRACK_PATH].toString(), data[TRACK_TITLE].toString(), data[TRACK_AUTHOR].toString(), data)
            }
            PlayableSource.YOUTUBE.id -> {
                PlayableYouTube(data[TRACK_PATH].toString(), data[TRACK_TITLE].toString(), data[TRACK_AUTHOR].toString(), data)
            }
            else -> {
                Log.e("Database", "Unable to deserialize '${data[TRACK_ID]}'. Unknown source!")
                null
            }
        }
    }

    fun getPlayableData(p: Playable, close: Boolean = true): Map<String, Any> {
        return getPlayableData(p.id, close)
    }

    fun getPlayableData(id: String, close: Boolean = true): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        val columns = arrayOf(TRACK_ID, TRACK_SOURCE, TRACK_PATH, TRACK_TITLE, TRACK_AUTHOR,
                TRACK_ALBUM, TRACK_YEAR, TRACK_TRACK, TRACK_GENRE, TRACK_RATING, TRACK_LYRICS,
                TRACK_START, TRACK_END, TRACK_LENGTH, TRACK_COVER_PATH)

        try {
            readableDatabase.query(Constants.DB_PLAYABLES_ID, columns, "$TRACK_ID=?", arrayOf(id), null, null, null)
                    ?.also {c ->
                        c.moveToFirst()
                        result[TRACK_ID] = c.getString(0)
                        result[TRACK_SOURCE] = c.getString(1)
                        result[TRACK_PATH] = c.getString(2)

                        result[TRACK_TITLE] = c.getString(3)
                        result[TRACK_AUTHOR] = c.getString(4)
                        result[TRACK_ALBUM] = c.getString(5)
                        result[TRACK_YEAR] = c.getInt(6)
                        result[TRACK_TRACK] = c.getInt(7)
                        result[TRACK_GENRE] = c.getString(8)
                        result[TRACK_RATING] = c.getInt(9)
                        result[TRACK_LYRICS] = c.getString(10)

                        result[TRACK_START] = c.getInt(11)
                        result[TRACK_END] = c.getInt(12)
                        result[TRACK_LENGTH] = c.getInt(13)
                        result[TRACK_COVER_PATH] = c.getString(14)
                        c.close()
                    }
//            if (close) writableDatabase.close()
        } catch (e: CursorIndexOutOfBoundsException) {
            Log.e("Database", "CursorIndexOutOfBoundsException for '$id'.")
        }

        return result
    }

    fun addPlayable(p: Playable, close: Boolean = true) {
        val pInfo = p.info
        val statement = "INSERT OR REPLACE INTO $DB_PLAYABLES_ID " +
                "($TRACK_ID, $TRACK_SOURCE, $TRACK_PATH, $TRACK_TITLE, $TRACK_AUTHOR, " +
                "$TRACK_ALBUM, $TRACK_YEAR, $TRACK_TRACK, $TRACK_GENRE, $TRACK_RATING, " +
                "$TRACK_LYRICS, $TRACK_START, $TRACK_END, $TRACK_LENGTH, $TRACK_COVER_PATH) VALUES " +
                "(\"${p.id}\", \"${p.playableSource}\", \"${p.path}\", \"${pInfo.title ?: ""}\", " +
                "\"${pInfo.author ?: ""}\", \"${pInfo.album ?: ""}\", ${pInfo.year
                        ?: 0}, ${pInfo.track ?: 0}, " +
                "\"${pInfo.genre ?: ""}\", ${pInfo.rating}, \"${pInfo.lyrics
                        ?: ""}\", ${pInfo.start}, " +
                "${pInfo.end}, ${pInfo.length}, \"${pInfo.coverPath}\");"

        try {
            writableDatabase.execSQL(statement)
//            if (close) writableDatabase.close()
        } catch (e: SQLiteException) {
            Log.w("Database", "Unable execute SQL statement: $statement")
        }
    }

    fun removePlayable(p: Playable, close: Boolean = true) {
        val statement = "DELETE FROM $DB_PLAYABLES_ID WHERE $TRACK_ID = \"${p.id}\";"
        writableDatabase.execSQL(statement)

        NoteStream.instance?.library?.playlists?.forEach {
            removeFromPlaylist(p, it, false)
        }

//        if (close) writableDatabase.close()
    }

    fun removeFromPlaylist(p: Playable, playlist: String, close: Boolean = true) {
        val statement = "DELETE FROM $DB_PLAYLIST_PREFIX$playlist WHERE $TRACK_ID = \"${p.id}\";"

        writableDatabase.execSQL(statement)
//        if (close) writableDatabase.close()
    }

    fun removeFromPlaylist(p: Playable, from: Playlist, close: Boolean = true) {
        val statement = "DELETE FROM $DB_PLAYLIST_PREFIX${from.id} WHERE $TRACK_ID = \"${p.id}\";"

        writableDatabase.execSQL(statement)
//        if (close) writableDatabase.close()
    }

    fun removePlaylist(pl: Playlist, close: Boolean = true) {
        var statement = "DROP TABLE IF EXISTS $DB_PLAYLIST_PREFIX${pl.id};"
        writableDatabase.execSQL(statement)

        statement = "DELETE FROM $DB_PLAYLIST_INFO_ID WHERE $PLAYLIST_ID = \"${pl.id}\";"
        writableDatabase.execSQL(statement)

//        if (close) writableDatabase.close()
    }

    fun addToPlaylist(p: Playable, to: Playlist, close: Boolean = true) {
        addPlayable(p)

        val statement = "INSERT OR REPLACE INTO $DB_PLAYLIST_PREFIX${to.id} " +
                "($TRACK_ID, $TRACK_TIMESTAMP) VALUES " +
                "(\"${p.id}\", ${System.currentTimeMillis()});"

        writableDatabase.execSQL(statement)
//        if (close) writableDatabase.close()
    }

    fun clearPlaylist(pl: Playlist, close: Boolean = true) {
        val statement = "DELETE FROM $DB_PLAYLIST_PREFIX${PlayerService.pl.id};"

        writableDatabase.execSQL(statement)
//        if (close) writableDatabase.close()
    }
}