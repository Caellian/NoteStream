package hr.caellian.notestream.data

import android.graphics.Bitmap
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.database.NoteStreamDB
import hr.caellian.notestream.lib.Constants
import java.io.Serializable
import java.util.*

/**
 * Created by caellyan on 16/04/17.
 */

class Playlist private constructor(val id: String) : Iterable<Playable>, Serializable {

    val playlist = ArrayList<Playable>(0)
    val shuffledPlaylist = ArrayList<Playable>(0)

    var label: String = ""
        set(value) {
            NoteStreamDB.updatePlaylist(this)
            field = value
        }
    var author: String = ""
        set(value) {
            NoteStreamDB.updatePlaylist(this)
            field = value
        }
    var capacity = 1
        private set(value) {
            NoteStreamDB.updatePlaylist(this)
            field = value
        }
    var currentPlayable = 0
        private set
    var shuffledCurrent = 0
        private set
    var shuffle = false
        set(value) {
            if (shuffle) {
                Collections.shuffle(shuffledPlaylist)
                shuffledCurrent = playlist.indexOf(getCurrentPlayable())
            } else {
                currentPlayable = playlist.indexOf(getCurrentPlayable())
            }
            field = shuffle
        }

    val firstPlayable: Playable
        get() = if (shuffle) shuffledPlaylist[0] else playlist[0]

    val lastPlayable: Playable
        get() = if (shuffle) shuffledPlaylist[shuffledPlaylist.size - 1] else playlist[playlist.size - 1]

    val isEmpty: Boolean
        get() = playlist.isEmpty()

    private val timestamps = mutableMapOf<Playable, Long>()

    val coverBitmaps: Array<Bitmap>
        get() = playlist.filter { it.info.cover != PlayableInfo.DEFAULT_COVER }
                .map { it.info.cover }
                .subList(0, 4)
                .toTypedArray()

    fun add(other: Iterable<Playable>?): Playlist {
        if (other == null) return this

        for (playable in other) {
            playlist.add(playable)
            shuffledPlaylist.add(playable)
            timestamps[playable] = System.currentTimeMillis()
            NoteStreamDB.addPlayableTo(playable, this)
            NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)
        }

        trimToCapacity(false)
        return this
    }

    fun add(playable: Playable?): Playlist {
        if (playable == null || playlist.contains(playable)) return this

        playlist.add(playable)
        shuffledPlaylist.add(playable)
        timestamps[playable] = System.currentTimeMillis()
        NoteStreamDB.addPlayableTo(playable, this)
        NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)

        trimToCapacity(false)
        return this
    }

    fun add(index: Int, playable: Playable?): Playlist {
        if (playable == null || playlist.contains(playable)) return this


        playlist.add(index, playable)
        shuffledPlaylist.add(index, playable)
        timestamps[playable] = System.currentTimeMillis()
        NoteStreamDB.addPlayableTo(playable, this)
        NoteStream.instance?.library?.onPlayableAddedToPlaylist(playable, this)

        trimToCapacity(false)
        return this
    }

    fun addRelative(position: Int, playable: Playable?): Playlist {
        if (playable == null) return this

        playlist.add(currentPlayable + position, playable)
        shuffledPlaylist.add(shuffledCurrent + position, playable)
        timestamps[playable] = System.currentTimeMillis()
        NoteStreamDB.addPlayableTo(playable, this)
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
            timestamps.remove(playable)
            NoteStreamDB.removePlayableFrom(playable, this)
            NoteStream.instance?.library?.onPlayableRemovedFromPlaylist(playable, this)
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
        timestamps.clear()

        NoteStreamDB.clearPlaylist(this)
        return this
    }

    fun size() = playlist.size

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
        return playlist.firstOrNull { it.id == id }
    }

    private fun trimToCapacity(fromEnd: Boolean) {
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
        val result = Playlist(Constants.PLAYLIST_TEMPORARY_PREFIX + Constants.PLAYLIST_FILTERED_PREFIX + id)
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
                val o1t = timestamps[o1]
                val o2t = timestamps[o2]

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

        const val SORT_BY_TITLE = 1
        const val SORT_BY_AUTHOR = 2
        const val SORT_BY_DATE = 3

        fun get(id: String, author: String = "", label: String = "", capacity: Int = 512, data: Iterable<Playable> = ArrayList()): Playlist {
            if (initialized.containsKey(id)) {
                return initialized[id]!!
            } else {
                val result = Playlist(id)

                NoteStreamDB.registerPlaylist(result, id, author, label, capacity)

                if (result.playlist.isEmpty()) {
                    result.add(data)
                }
                result.shuffledPlaylist.addAll(result.playlist)
                result.capacity = Math.max(1, capacity)

                initialized[id] = result
                return result
            }
        }
    }
}
