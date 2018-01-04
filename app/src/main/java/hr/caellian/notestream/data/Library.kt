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
            .setLabel(NoteStream.instance?.getString(R.string.label_favorites) ?: "Favorites")

    var lastListened = Playlist.get("lastListened", ArrayList(21), 20)

    protected var addedTimestamps = HashMap<String, Long>()

    init {

        if (pref?.contains("playlists") == false) {
            playlists.add("favoriteMusic")
            pref.edit().putStringSet("playlist", playlists).apply()
        } else {
            playlists = pref?.getStringSet("playlists", playlists) as HashSet<String>
        }

        if (pref.contains("addedTimestamps")) {
            try {
                addedTimestamps = HashMap() // ObjectSerializer.deserialize(pref.getString("addedTimestamps", "")) as HashMap<String, Long>
            } catch (e: IOException) {
                addedTimestamps = HashMap()
            } catch (e: ClassCastException) {
                addedTimestamps = HashMap()
            } catch (e: NullPointerException) {
                addedTimestamps = HashMap()
            }

        }

        for (playable in savedMusic) {
            val metadata = playable.info
            val album = metadata.album
            val artist = metadata.author
            val genre = metadata.genre

            if (!albums.containsKey(album)) {
                albums.put(album!!, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.ALBUM_PREFIX + album))
            }
            albums[album]?.add(playable)

            if (!artists.containsKey(artist)) {
                artists.put(artist!!, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.ARTIST_PREFIX + artist))
            }
            artists[artist]?.add(playable)

            if (!genres.containsKey(genre)) {
                genres.put(genre!!, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.GENRE_PREFIX + genre))
            }
            genres[genre]?.add(playable)
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
            addedTimestamps.put(playable.id, System.currentTimeMillis() / 1000)
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
            addedTimestamps.remove(playable.id)
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

    fun getTimestampAdded(playable: Playable?): Long? {
        return if (addedTimestamps.containsKey(playable?.id)) {
            addedTimestamps[playable?.id]
        } else 0
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
