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

package hr.caellian.notestream.data.playlist

import android.graphics.Bitmap
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.PlayableInfo
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.database.NoteStreamDB
import hr.caellian.notestream.lib.Constants
import java.util.*
import kotlin.concurrent.thread

class Playlist private constructor(val id: String, author: String = "", label: String = "", capacity: Int = 512, data: List<Playable> = mutableListOf()): PlaylistCore {

    override val source = PlayableSource.LOCAL
    override val path: String = id

    override fun get(): Playlist = this

    val playlist = data as MutableList

    override var label: String = label
        set(value) {
            field = value
            thread {
                NoteStreamDB.updatePlaylist(this)
            }
        }
    override var author: String = author
        set(value) {
            field = value
            thread {
                NoteStreamDB.updatePlaylist(this)
            }
        }
    var capacity = capacity
        private set(value) {
            field = value
            thread {
                NoteStreamDB.updatePlaylist(this)
            }
        }

    val isEmpty: Boolean
        get() = playlist.isEmpty()

    val timestamps = mutableMapOf<Playable, Long>()

    override var coverBitmaps: MutableList<Bitmap>
        set(value) {}
        get() = playlist
                .filter { it.info.cover !== PlayableInfo.DEFAULT_COVER }
                .map { it.info.cover }
                .toMutableList()

    fun add(other: Playlist): Playlist {
        return add(other.playlist)
    }

    fun add(other: Collection<Playable>): Playlist {
        (playlist as ArrayList).ensureCapacity(playlist.size + other.size)

        for (playable in other) {
            playlist.add(playable)

            timestamps[playable] = System.currentTimeMillis()
            NoteStreamDB.addToPlaylist(playable, this)
            NoteStream.instance.data.onPlayableAddedToPlaylist(playable, this)
        }

        trimToCapacity()
        return this
    }

    fun add(playable: Playable): Playlist {
        if (playlist.contains(playable)) return this

        playlist.add(playable)
        timestamps[playable] = System.currentTimeMillis()
        NoteStreamDB.addToPlaylist(playable, this)
        try {
            NoteStream.instance.data.onPlayableAddedToPlaylist(playable, this)
        } catch (e: UninitializedPropertyAccessException) {
            // Perfectly normal for library init.
        }


        trimToCapacity()
        return this
    }

    fun remove(playable: Playable): Playlist {
        if (playlist.contains(playable)) {
            playlist.remove(playable)
            timestamps.remove(playable)
            NoteStreamDB.removeFromPlaylist(playable, this)
            NoteStream.instance.data.onPlayableRemovedFromPlaylist(playable, this)
        }
        return this
    }

    fun clear(): Playlist {
        for (playable in playlist) {
            NoteStreamDB.removeFromPlaylist(playable, this)
            NoteStream.instance.data.onPlayableRemovedFromPlaylist(playable, this)
        }
//        NoteStreamDB.writableDatabase.close()

        playlist.clear()
        timestamps.clear()

        NoteStreamDB.clearPlaylist(this)
        return this
    }

    fun size() = playlist.size

    fun getPlayable(id: String): Playable? {
        return playlist.firstOrNull { it.id == id }
    }

    private fun trimToCapacity(fromEnd: Boolean = false) {
        if (fromEnd) {
            while (playlist.size >= capacity) {
                NoteStream.instance.data.onPlayableRemovedFromPlaylist(playlist.removeAt(playlist.size - 1), this)
            }
        } else {
            while (playlist.size >= capacity) {
                NoteStream.instance.data.onPlayableRemovedFromPlaylist(playlist.removeAt(0), this)
            }
        }
    }

    fun filtered(filter: String): MutableList<Playable> {
        val result = mutableListOf<Playable>()
        for (playable in playlist) {
            if (playable.info.title?.toLowerCase()?.contains(filter.toLowerCase()) == true) result.add(playable)
            if (playable.info.author?.toLowerCase()?.contains(filter.toLowerCase()) == true) result.add(playable)
        }
        return result
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Playlist) return false
        val o = other as Playlist?
        return this.id == o!!.id
    }

    companion object {
        private val Initialized = HashMap<String, Playlist>()

        val Empty = Playlist(Constants.PLAYLIST_EMPTY_ID, capacity = 0)

        fun get(id: String, author: String = "", label: String = "", capacity: Int = 512, data: List<Playable> = mutableListOf()): Playlist {
            return when {
                id == Constants.PLAYLIST_EMPTY_ID -> Empty
                Initialized.containsKey(id) -> Initialized[id]!!
                else -> {
                    Initialized[id] = Playlist(id, author, label, capacity, data).also {
                        NoteStreamDB.syncPlaylist(it, id, author, label, capacity)
                    }
                    Initialized[id] ?: Empty
                }
            }
        }
    }
}
