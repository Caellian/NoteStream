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

package hr.caellian.notestream.lib

import android.util.Base64
import java.util.*

object Constants {
    // Application
    const val ApplicationName = "NoteStream"
    const val ApplicationVersion = "0.0.1"

    // Codes
    const val SIGN_IN_CODE = 9001
    const val APP_REQUEST_CODE = 6678
    const val APP_PSBR_CODE = 7727
    const val APP_NOTIFICATION_ID = 66786

    // Tags
    const val MEDIA_SESSION_TAG = "NSMediaSession"

    const val KA = "AIzaSyDgWMXnS"
    const val KB = "G45sdfFAr334g"
    const val KC = "imp52Pt1MM0Uw"
    const val KD = "34d2lrrt6423e"
    const val KE = "xWopVeTLSTn48"

    // Google API Scopes

    val Scopes = listOf(
            "https://www.googleapis.com/auth/youtube.readonly",
            "https://www.googleapis.com/auth/drive.file")

    // Playlist
    const val PLAYLIST_EMPTY_ID = "empty"
    const val PLAYLIST_TEMPORARY_PREFIX = "temporary_"
    const val PLAYLIST_GENRE_PREFIX = "genre_"
    const val PLAYLIST_ARTIST_PREFIX = "author_"
    const val PLAYLIST_ALBUM_PREFIX = "album_"

    // Database
    const val DB_NAME = "hr.caellian.notestream.database"
    const val DB_VERSION = 1

    const val DB_PLAYABLES_ID = "playables"
    const val DB_PLAYLIST_INFO_ID = "playlist_info"
    const val DB_PLAYLIST_PREFIX = "playlist_"

    // Database Columns
    const val TRACK_ID = "TRACK_ID"
    const val TRACK_SOURCE = "TRACK_SOURCE"
    const val TRACK_PATH = "TRACK_PATH"

    const val TRACK_TITLE = "TITLE"
    const val TRACK_AUTHOR = "AUTHOR"
    const val TRACK_ALBUM = "ALBUM"
    const val TRACK_YEAR = "YEAR"
    const val TRACK_TRACK = "TRACK"
    const val TRACK_GENRE = "GENRE"
    const val TRACK_RATING = "RATING"
    const val TRACK_LYRICS = "LYRICS"

    const val TRACK_START = "START"
    const val TRACK_END = "END"
    const val TRACK_LENGTH = "LENGTH"
    const val TRACK_COVER_PATH = "COVER_PATH"

    const val PLAYLIST_ID = "PLAYLIST_ID"
    const val PLAYLIST_LABEL = "PL_LABEL"
    const val PLAYLIST_AUTHOR = "PL_AUTHOR"
    const val PLAYLIST_CAPACITY = "PL_CAPACITY"

    const val TRACK_TIMESTAMP = "ADDED_ON"

    // SQL
    const val SQL_TYPE_NULL = "NULL"
    const val SQL_TYPE_INTEGER = "INTEGER"
    const val SQL_TYPE_REAL = "REAL"
    const val SQL_TYPE_TEXT = "TEXT"
    const val SQL_TYPE_BLOB = "BLOB"

    const val SQL_NOT_NULL = "NOT NULL"
    const val SQL_PRIMARY_KEY = "PRIMARY KEY"

    // Actions
    const val ACTION_INIT = "hr.caellian.notestream.action.INIT"
    const val ACTION_PLAY = "hr.caellian.notestream.action.PLAY"
    const val ACTION_PAUSE = "hr.caellian.notestream.action.PAUSE"
    const val ACTION_TOGGLE_PLAY = "hr.caellian.notestream.action.TOGGLE_PLAY"
    const val ACTION_STOP = "hr.caellian.notestream.action.STOP"
    const val ACTION_SWITCH_PREVIOUS = "hr.caellian.notestream.action.SWITCH_PREVIOUS"
    const val ACTION_SWITCH_NEXT = "hr.caellian.notestream.action.SWITCH_NEXT"
    const val ACTION_TOGGLE_REPEAT = "hr.caellian.notestream.action.TOGGLE_REPEAT"
    const val ACTION_TOGGLE_SHUFFLE = "hr.caellian.notestream.action.TOGGLE_SHUFFLE"

    // Extras
    const val EXTRA_ID = "hr.caellian.notestream.EXTRA_ID"
    const val EXTRA_LABEL = "hr.caellian.notestream.EXTRA_LABEL"
    const val EXTRA_LABEL_DESCRIPTION = "hr.caellian.notestream.EXTRA_LABEL_DESCRIPTION"
    const val EXTRA_ICON = "hr.caellian.notestream.EXTRA_ICON"
    const val EXTRA_TILES = "hr.caellian.notestream.EXTRA_BACKGROUND_TILES"
    const val EXTRA_BACKGROUND = "hr.caellian.notestream.EXTRA_BACKGROUND"

    const val EXTRA_DATA = "hr.caellian.notestream.EXTRA_DATA" // Represents all NoteStream data
    const val EXTRA_PLAYLIST = "hr.caellian.notestream.EXTRA_PLAYLIST"
    const val EXTRA_ITERATOR = "hr.caellian.notestream.EXTRA_ITERATOR"
    const val EXTRA_ITERATOR_ASCENDING = "hr.caellian.notestream.EXTRA_ITERATOR_ASCENDING"
    const val EXTRA_PLAYABLE = "hr.caellian.notestream.EXTRA_PLAYABLE"

    // Arguments
    const val ARGUMENT_ICON = "hr.caellian.notestream.ARGUMENT_ICON"
    const val ARGUMENT_TITLE = "hr.caellian.notestream.ARGUMENT_TITLE"
    const val ARGUMENT_DESCRIPTION = "hr.caellian.notestream.ARGUMENT_DESCRIPTION"
    const val ARGUMENT_PERMISSION = "hr.caellian.notestream.ARGUMENT_PERMISSION"

    // Bundle data
    const val BUNDLE_LYRICS_VISIBLE = "hr.caellian.notestream.BUNDLE_LYRICS_VISIBLE"

    // Preferences
    const val CHECK_GOOGLE_SIGN_IN = "hr.caellian.notestream.SHOULD_SIGN_IN"
    const val CHECK_PERMISSION = "hr.caellian.notestream.SHOULD_"
    const val PERMISSION_PREFIX = "android.permission."

    // Iterators
    const val ITERATOR_DEFAULT_ID = "hr.caellian.notestream.ITERATOR_DEFAULT"
    const val ITERATOR_RANDOM_ID = "hr.caellian.notestream.ITERATOR_RANDOM"
    const val ITERATOR_TITLE_ID = "hr.caellian.notestream.ITERATOR_TITLE"
    const val ITERATOR_AUTHOR_ID = "hr.caellian.notestream.ITERATOR_AUTHOR"
    const val ITERATOR_TIME_ID = "hr.caellian.notestream.ITERATOR_TIME"
}
