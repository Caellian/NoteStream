package hr.caellian.notestream.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 17/06/17.
 */

class Library {
    private val pref: SharedPreferences? = NoteStream.instance?.getSharedPreferences("library", Context.MODE_PRIVATE)

    var playlists = HashSet<String>()

    var albums = HashMap<String, Playlist>()
    var artists = HashMap<String, Playlist>()
    var genres = HashMap<String, Playlist>()

    var localMusic = Playlist.get("localMusic")
    var savedMusic = Playlist.get("savedMusic")
    var hiddenMusic = Playlist.get("hiddenMusic")
    var favoriteMusic = Playlist.get("favoriteMusic")
            .also { it.label = NoteStream.instance?.getString(R.string.label_favorites) ?: "Favorites" }

    var lastListened = Playlist.get("lastListened", capacity = 20)

    init {

        if (pref?.contains("playlists") == false) {
            playlists.add("favoriteMusic")
            pref.edit().putStringSet("playlist", playlists).apply()
        } else {
            playlists = pref?.getStringSet("playlists", playlists) as HashSet<String>
        }

        for (playable in savedMusic) {
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
        pref?.edit()?.putStringSet("playlist", playlists)?.apply()
        return created
    }

    fun deletePlaylist(id: String) {
        val deleted = Playlist.get(id)
        deleted.clear()
        playlists.remove(id)
        pref?.edit()?.putStringSet("playlist", playlists)?.apply()
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
     * Called when a new playable has been added to a playlist.
     * @param playable playable added to playlist.
     * @param playlist affected playlist.
     */
    fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist) {
        for (libraryListener in NoteStream.LIBRARY_LISTENERS) {
            libraryListener.onPlayableAddedToPlaylist(playable, playlist)
        }
    }

    /**
     * Called when a playable has been removed from a playlist.
     * @param playable playable removed from playlist.
     * @param playlist affected playlist.
     */
    fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {
        for (libraryListener in NoteStream.LIBRARY_LISTENERS) {
            libraryListener.onPlayableRemovedFromPlaylist(playable, playlist)
        }
    }

    interface LibraryListener {
        /**
         * Called when a new playable has been added to a playlist.
         * @param playable playable added to playlist.
         * @param playlist affected playlist.
         */
        fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist)

        /**
         * Called when a playable has been removed from a playlist.
         * @param playable playable removed from playlist.
         * @param playlist affected playlist.
         */
        fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist)
    }

    companion object {
        private val TAG = Library::class.java.simpleName
    }
}
