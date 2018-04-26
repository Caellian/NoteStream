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

package hr.caellian.notestream.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.lib.Constants
import java.io.IOException
import java.util.*

class NoteStreamData {
    private val pref: SharedPreferences? = NoteStream.instance.getSharedPreferences("data", Context.MODE_PRIVATE)

    var playlists = HashSet<String>()

    var albums = HashMap<String, Playlist>()
    var artists = HashMap<String, Playlist>()
    var genres = HashMap<String, Playlist>()

    var localMusic = Playlist.get("localMusic")
    var savedMusic = Playlist.get("savedMusic")
    var hiddenMusic = Playlist.get("hiddenMusic")
    var favoriteMusic = Playlist.get("favoriteMusic")
            .also { it.label = NoteStream.instance.getString(R.string.label_favorites) ?: "Favorites" }

    var lastListened = Playlist.get("lastListened", capacity = 20)

    init {

        if (pref?.contains("playlists") == false) {
            playlists.add("favoriteMusic")
            pref.edit().putStringSet("argumentPlaylist", playlists).apply()
        } else {
            playlists = pref?.getStringSet("playlists", playlists) as HashSet<String>
        }

        for (playable in PlaylistIterator(savedMusic)) {
            val metadata = playable.info

            metadata.album?.also {
                if (!albums.containsKey(it)) {
                    albums[it] = Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + Constants.PLAYLIST_ALBUM_PREFIX + it)
                }
                albums[it]?.add(playable)
            }

            metadata.author?.also {
                if (!artists.containsKey(it)) {
                    artists[it] = Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + Constants.PLAYLIST_ARTIST_PREFIX + it)
                }
                artists[it]?.add(playable)
            }

            metadata.genre?.also {
                if (!genres.containsKey(it)) {
                    genres[it] = Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + Constants.PLAYLIST_GENRE_PREFIX + it)
                }
                genres[it]?.add(playable)
            }
        }
    }

    fun createPlaylist(id: String): Playlist {
        val created = Playlist.get(id)
        playlists.add(id)
        pref?.edit()?.putStringSet("argumentPlaylist", playlists)?.apply()
        return created
    }

    fun deletePlaylist(id: String) {
        val deleted = Playlist.get(id)
        deleted.clear()
        playlists.remove(id)
        pref?.edit()?.putStringSet("argumentPlaylist", playlists)?.apply()
    }

    fun savePlayable(playable: Playable?): Boolean {
        playable?.also {
            val result = !savedMusic.playlist.contains(playable)
            if (!result) return false

            savedMusic.add(playable)
            try {
                pref?.edit()?.putString("addedTimestamps", "" /* ObjectSerializer.serialize(addedTimestamps)*/)?.apply()
            } catch (e: IOException) {
                Log.e(TAG, "savePlayable: unable to update 'addedTimestamps' preference!", e)
            }

            if (playable is PlayableLocal) {
                hiddenMusic.remove(playable)
            }
        }

        return true
    }

    fun removePlayable(playable: Playable?): Boolean {
        playable?.also {
            val result = savedMusic.playlist.contains(playable)
            if (!result) return false

            savedMusic.remove(playable)
            try {
                pref?.edit()?.putString("addedTimestamps", "" /* ObjectSerializer.serialize(addedTimestamps)*/)?.apply()
            } catch (e: IOException) {
                Log.e(TAG, "removePlayable: unable to update 'addedTimestamps' preference!", e)
            }

            if (playable is PlayableLocal) {
                hiddenMusic.add(playable)
            }
        }

        return true
    }

    fun isSaved(playable: Playable?): Boolean {
        return savedMusic.playlist.contains(playable)
    }

    /**
     * Called when a new argumentPlayable has been added to a argumentPlaylist.
     * @param playable argumentPlayable added to argumentPlaylist.
     * @param playlist affected argumentPlaylist.
     */
    fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist) {
        for (libraryListener in NoteStream.LIBRARY_LISTENERS) {
            libraryListener.onPlayableAddedToPlaylist(playable, playlist)
        }
    }

    /**
     * Called when a argumentPlayable has been removed from a argumentPlaylist.
     * @param playable argumentPlayable removed from argumentPlaylist.
     * @param playlist affected argumentPlaylist.
     */
    fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {
        for (libraryListener in NoteStream.LIBRARY_LISTENERS) {
            libraryListener.onPlayableRemovedFromPlaylist(playable, playlist)
        }
    }

    interface LibraryListener {
        /**
         * Called when a new argumentPlayable has been added to a argumentPlaylist.
         * @param playable argumentPlayable added to argumentPlaylist.
         * @param playlist affected argumentPlaylist.
         */
        fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist)

        /**
         * Called when a argumentPlayable has been removed from a argumentPlaylist.
         * @param playable argumentPlayable removed from argumentPlaylist.
         * @param playlist affected argumentPlaylist.
         */
        fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist)
    }

    companion object {
        private val TAG = NoteStreamData::class.java.simpleName
    }
}
