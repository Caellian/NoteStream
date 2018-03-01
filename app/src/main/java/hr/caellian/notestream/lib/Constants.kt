package hr.caellian.notestream.lib

/**
 * Created by caellyan on 21/06/17.
 */

object Constants {
    const val SIGN_IN_CODE = 9001
    const val APP_REQUEST_CODE = 6678
    const val APP_PSBR_CODE = 7727

    const val APP_NOTIFICATION_ID = 66786

    const val MEDIA_SESSION_TAG = "NSMediaSession"

    // Playlist
    const val PLAYLIST_TEMPORARY_PREFIX = "temporary_"
    const val PLAYLIST_FILTERED_PREFIX = "filtered_"
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

    const val TRACK_PRIORITY = "PRIORITY"
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
    const val EXTRA_ICON = "hr.caellian.notestream.EXTRA_ICON"
    const val EXTRA_TILES = "hr.caellian.notestream.EXTRA_BACKGROUND_TILES"
    const val EXTRA_BACKGROUND = "hr.caellian.notestream.EXTRA_BACKGROUND"
    const val EXTRA_PLAYLIST = "hr.caellian.notestream.EXTRA_PLAYLIST"
    const val EXTRA_PLAYABLE = "hr.caellian.notestream.EXTRA_PLAYABLE"
}
