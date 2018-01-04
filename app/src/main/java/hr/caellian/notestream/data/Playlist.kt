package hr.caellian.notestream.data

import android.graphics.Bitmap

import java.io.Serializable
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.database.PlaylistOpenHelper
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 16/04/17.
 */

class Playlist private constructor(val id: String) : Iterable<Playable>, Serializable {

    val playlist = ArrayList<Playable>(0)
    val shuffledPlaylist = ArrayList<Playable>(0)
    @Transient private val dbHelper: PlaylistOpenHelper?

    private var label: String? = null
    private var capacity = 1
    private var currentPlayable = 0
    private var shuffledCurrent = 0
    private var shuffle = false

    val firstPlayable: Playable
        get() = if (doShuffle()) shuffledPlaylist[0] else playlist[0]

    val lastPlayable: Playable
        get() = if (doShuffle()) shuffledPlaylist[shuffledPlaylist.size - 1] else playlist[playlist.size - 1]

    val isEmpty: Boolean
        get() = playlist.isEmpty()

    val coverBitmaps: Array<Bitmap>
        get() = playlist.filter { it.info.cover != PlayableInfo.DEFAULT_COVER }
                .map { it.info.cover }
                .subList(0, 4)
                .toTypedArray()

    init {
        dbHelper = PlaylistOpenHelper(NoteStream.instance!!, this, null)
    }

    fun add(other: Iterable<Playable>?): Playlist {
        if (other == null) return this

        for (playable in other) {
            playlist.add(playable)
            shuffledPlaylist.add(playable)
            addPlayableToDB(playable)
            NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)
        }

        trimToCapacity(false)
        return this
    }

    fun add(playable: Playable?): Playlist {
        if (playable == null || playlist.contains(playable)) return this

        playlist.add(playable)
        shuffledPlaylist.add(playable)
        addPlayableToDB(playable)
        NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)

        trimToCapacity(false)
        return this
    }

    fun add(index: Int, playable: Playable?): Playlist {
        if (playable == null || playlist.contains(playable)) return this


        playlist.add(index, playable)
        shuffledPlaylist.add(index, playable)
        addPlayableToDB(playable)
        NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)

        trimToCapacity(false)
        return this
    }

    fun addRelative(position: Int, playable: Playable?): Playlist {
        if (playable == null) return this

        playlist.add(currentPlayable + position, playable)
        shuffledPlaylist.add(shuffledCurrent + position, playable)
        // TODO: Handle this correctly!
        addPlayableToDB(playable)
        NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)
        trimToCapacity(true)

        return this
    }

    fun addNext(playable: Playable): Playlist {
        return addRelative(0, playable)
    }

    fun remove(playable: Playable): Playlist {
        if (playlist.contains(playable)) {
            val plpi = playlist.indexOf(playable)
            val shpi = shuffledPlaylist.indexOf(playable)

            currentPlayable -= if (plpi >= currentPlayable) 1 else 0
            shuffledCurrent -= if (shpi >= shuffledCurrent) 1 else 0

            playlist.remove(playable)
            shuffledPlaylist.remove(playable)
            NoteStream.instance?.library?.onPlayableRemovedFromPlaylist(playable, this)

            dbHelper!!.writableDatabase.execSQL("DELETE FROM " + dbHelper.databaseName + " WHERE " + Constants.TRACK_ID + "='" + playable.id + "';")
        }
        return this
    }

    fun clear(): Playlist {
        currentPlayable = 0
        shuffledCurrent = 0

        for (playable in playlist) {
            NoteStream.instance?.library?.onPlayableRemovedFromPlaylist(playable, this)
        }

        playlist.clear()
        shuffledPlaylist.clear()

        if (dbHelper != null && dbHelper.databaseName != null)
            dbHelper.writableDatabase.execSQL("DELETE FROM " + dbHelper.databaseName)
        return this
    }

    fun size(): Int {
        return playlist.size
    }

    fun skipTo(playable: Playable): Boolean {
        if (shuffle) {
            for (iterated in shuffledPlaylist) {
                if (iterated == playable) {
                    shuffledCurrent = shuffledPlaylist.indexOf(iterated)
                    return true
                }
            }
        } else {
            for (iterated in playlist) {
                if (iterated == playable) {
                    currentPlayable = playlist.indexOf(iterated)
                    return true
                }
            }
        }

        return false
    }

    fun getCurrentPlayable(): Playable? {
        if (shuffle && shuffledCurrent < shuffledPlaylist.size) {
            return shuffledPlaylist[shuffledCurrent]
        } else if (currentPlayable < playlist.size) {
            return playlist[currentPlayable]
        }
        return null
    }

    fun setShuffle(shuffle: Boolean) {
        if (shuffle) {
            Collections.shuffle(shuffledPlaylist)
            shuffledCurrent = playlist.indexOf(getCurrentPlayable())
        } else {
            currentPlayable = playlist.indexOf(getCurrentPlayable())
        }
        this.shuffle = shuffle
    }

    fun switchPrevious(): Playable? {
        if (shuffle) {
            if (--shuffledCurrent < 0) {
                shuffledCurrent = shuffledPlaylist.size - 1
            }
        } else {
            if (--currentPlayable < 0) {
                currentPlayable = playlist.size - 1
            }
        }

        return if (getCurrentPlayable() is PlayableRemote && (getCurrentPlayable() as PlayableRemote?)?.available?.not() == true) {
            switchPrevious()
        } else {
            getCurrentPlayable()
        }
    }

    fun switchNext(): Playable? {
        if (shuffle) {
            if (++shuffledCurrent >= shuffledPlaylist.size) {
                Collections.shuffle(shuffledPlaylist)
                shuffledCurrent = 0
            }
        } else {
            if (++currentPlayable >= playlist.size) {
                currentPlayable = 0
            }
        }

        return if (getCurrentPlayable() is PlayableRemote && (getCurrentPlayable() as PlayableRemote?)?.available?.not() == true) {
            switchNext()
        } else {
            getCurrentPlayable()
        }
    }

    fun getPlayable(id: String): Playable? {
        for (playable in playlist) {
            if (playable.id == id) return playable
        }
        return null
    }

    fun doShuffle(): Boolean {
        return shuffle
    }

    fun getLabel(): String? {
        return label
    }

    fun setLabel(label: String): Playlist {
        this.label = label
        return this
    }

    protected fun addPlayableToDB(playable: Playable) {
//        object : AsyncTask<Void, Void, Void>() {
//            override fun doInBackground(vararg params: Void): Void? {
//                try {
//                    if (dbHelper!!.databaseName == null) return null
//                    var dbStatement = "INSERT OR REPLACE INTO " + dbHelper.databaseName + " ("
//                    var values = ""
//                    for ((key) in PlayableInfo.properties) {
//                        dbStatement += key + ", "
//                        values += "?, "
//                    }
//                    values = values.substring(0, values.length - 2)
//                    dbStatement = dbStatement.substring(0, dbStatement.length - 2) + ") VALUES (" + values + ");"
//                    val statement = dbHelper.writableDatabase.compileStatement(dbStatement)
//
//                    var current = 1
//                    for ((key, value1) in PlayableInfo.properties) {
//                        val value = playable.info[key]
//                        if (value == null) {
//                            statement.bindNull(current++)
//                            continue
//                        }
//                        when (value1) {
//                            Constants.SQL_TYPE_NULL -> statement.bindNull(current++)
//                            Constants.SQL_TYPE_INTEGER ->
//                                statement.bindLong(current++, java.lang.Long.valueOf((value as Int).toLong())!!)
//                            Constants.SQL_TYPE_REAL -> statement.bindDouble(current++, value as Double)
//                            Constants.SQL_TYPE_TEXT -> statement.bindString(current++, value as String?)
//                            Constants.SQL_TYPE_BLOB -> statement.bindBlob(current++, value as ByteArray?)
//                        }
//                    }
//                    statement.execute()
//                } catch (e: NullPointerException) {
//                    Log.e(TAG, "add: Unable to add playable to DB!", e)
//                } catch (e: ClassCastException) {
//                    Log.e(TAG, "add: Unable to add playable to DB!", e)
//                }
//
//                return null
//            }
//        }.execute()
    }

    protected fun trimToCapacity(fromEnd: Boolean) {
        if (fromEnd) {
            while (playlist.size >= capacity) {
                val removed = playlist.removeAt(playlist.size - 1)
                shuffledPlaylist.remove(removed)
                NoteStream.instance?.library?.onPlayableRemovedFromPlaylist(removed, this)
            }
        } else {
            while (playlist.size >= capacity) {
                val removed = playlist.removeAt(0)
                shuffledPlaylist.remove(removed)
                NoteStream.instance?.library?.onPlayableRemovedFromPlaylist(removed, this)
            }
        }
    }

    fun filtered(filter: String): Playlist {
        val result = Playlist(TEMPORARY_PREFIX + FILTERED_PREFIX + id)
        result.clear()

        for (playable in this) {
            if (playable.info.title?.toLowerCase()?.contains(filter.toLowerCase()) == true) result.add(playable)
            if (playable.info.author?.toLowerCase()?.contains(filter.toLowerCase()) == true) result.add(playable)
        }

        return result
    }

    @JvmOverloads
    fun sort(order: Int, ascending: Boolean = true): Playlist {
        when (order) {
            SORT_BY_TITLE -> Collections.sort(playlist) { o1, o2 ->
                if (ascending) {
                    o1.info.title?.compareTo(o2.info.title.orEmpty()) ?: 0
                } else {
                    o2.info.title?.compareTo(o1.info.title.orEmpty()) ?: 0
                }
            }
            SORT_BY_AUTHOR -> Collections.sort(playlist) { o1, o2 ->
                if (ascending) {
                    o1.info.author?.compareTo(o2.info.author.orEmpty()) ?: 0
                } else {
                    o2.info.author?.compareTo(o1.info.author.orEmpty()) ?: 0
                }
            }
            SORT_BY_DATE -> Collections.sort(playlist) { o1, o2 ->
                val o1t = NoteStream.instance?.library?.getTimestampAdded(o1)
                val o2t = NoteStream.instance?.library?.getTimestampAdded(o2)

                if (o1t != null && o2t != null) {
                    if (ascending) {
                        (o1t - o2t).toInt()
                    } else {
                        (o2t - o1t).toInt()
                    }
                } else {
                    0
                }
            }
            else -> throw IllegalArgumentException("Unsupported order argument!")
        }

        return this
    }

    override fun iterator(): Iterator<Playable> {
        return playlist.iterator()
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj !is Playlist) return false
        val other = obj as Playlist?
        return this.id == other!!.id
    }

    companion object {
        private val TAG = Playlist::class.java.simpleName
        private val initialized = HashMap<String, Playlist>()

        val SORT_BY_TITLE = 1
        val SORT_BY_AUTHOR = 2
        val SORT_BY_DATE = 3

        val TEMPORARY_PREFIX = "temporary_"
        val FILTERED_PREFIX = "filtered_"
        val GENRE_PREFIX = "genre_"
        val ARTIST_PREFIX = "author_"
        val ALBUM_PREFIX = "album_"

        fun get(id: String, data: Iterable<Playable> = ArrayList(), capacity: Int = 512): Playlist {
            if (initialized.containsKey(id)) {
                return initialized[id]!!
            } else {
                val result = Playlist(id)

//                if (!id.startsWith(TEMPORARY_PREFIX) && result.dbHelper != null) {
//                    val db = result.dbHelper.readableDatabase
//                    val c = db.rawQuery("SELECT * FROM " + result.dbHelper.databaseName, null)
//                    var row = 0
//                    while (c.moveToPosition(row++)) {
//                        try {
//                            // TODO: For some reason file path is stored in field for source type tag.
//                            val sourcePos = result.dbHelper.tableColumns[PlayableInfo.SOURCE] ?: continue
//                            val source = PlayableSource.getByID(c.getString(sourcePos))
//                            if (source != null) {
//                                val toAdd = source.construct(c.getString(result.dbHelper.tableColumns[PlayableInfo.PATH]!!))
//                                if (toAdd != null) {
//                                    toAdd.info.setFromDatabase(result.dbHelper, c)
//                                    result.playlist.add(toAdd)
//                                }
//                            }
//                        } catch (e: IllegalAccessException) {
//                            Log.w(TAG, "Ignoring corrupted playable!", e)
//                            e.printStackTrace()
//                        } catch (e: InvocationTargetException) {
//                            Log.w(TAG, "Ignoring corrupted playable!", e)
//                            e.printStackTrace()
//                        } catch (e: InstantiationException) {
//                            Log.w(TAG, "Ignoring corrupted playable!", e)
//                            e.printStackTrace()
//                        }
//
//                    }
//
//                    c.close()
//                }

                if (result.playlist.isEmpty()) {
                    result.add(data)
                }
                result.shuffledPlaylist.addAll(result.playlist)
                result.capacity = Math.max(1, capacity)

                initialized.put(id, result)
                return result
            }
        }
    }
}
