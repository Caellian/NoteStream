package hr.caellian.notestream.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.util.ObjectSerializer;

/**
 * Created by caellyan on 17/06/17.
 */

public class Library {
    private static final String TAG = Library.class.getSimpleName();
    private final SharedPreferences pref;

    public HashSet<String> playlists = new HashSet<>();

    public HashMap<String, Playlist> albums = new HashMap<>();
    public HashMap<String, Playlist> artists = new HashMap<>();
    public HashMap<String, Playlist> genres = new HashMap<>();

    public Playlist localMusic = Playlist.get("localMusic");
    public Playlist savedMusic = Playlist.get("savedMusic");
    public Playlist hiddenMusic = Playlist.get("hiddenMusic");
    public Playlist favoriteMusic = Playlist.get("favoriteMusic")
            .setLabel(NoteStream.getInstance().getString(R.string.label_favorites));

    public Playlist lastListened = Playlist.get("lastListened", new ArrayList<Playable>(21), 20);

    protected HashMap<String, Long> addedTimestamps = new HashMap<>();

    public Library() {
        pref = NoteStream.getInstance().getSharedPreferences("library", Context.MODE_PRIVATE);

        if (!pref.contains("playlists")) {
            playlists.add("favoriteMusic");
            pref.edit().putStringSet("playlist", playlists).apply();
        } else {
            playlists = (HashSet<String>) pref.getStringSet("playlists", playlists);
        }

        if (pref.contains("addedTimestamps")) {
            try {
                //noinspection unchecked
                addedTimestamps = (HashMap<String, Long>) ObjectSerializer.deserialize(pref.getString("addedTimestamps", ""));
            } catch (IOException | ClassCastException | NullPointerException e) {
                addedTimestamps = new HashMap<>();
            }
        }

        for (Playable playable : savedMusic) {
            MutableMediaMetadata metadata = playable.getMetadata();
            String album = metadata.getAlbum();
            String artist = metadata.getAuthor();
            String genre = metadata.getGenre();

            if (!albums.containsKey(album)) {
                albums.put(album, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.ALBUM_PREFIX + album));
            }
            albums.get(album).add(playable);

            if (!artists.containsKey(artist)) {
                artists.put(artist, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.ARTIST_PREFIX + artist));
            }
            artists.get(artist).add(playable);

            if (!genres.containsKey(genre)) {
                genres.put(genre, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.GENRE_PREFIX + genre));
            }
            genres.get(genre).add(playable);
        }
    }

    public Playlist createPlaylist(String id) {
        Playlist created = Playlist.get(id);
        playlists.add(id);
        pref.edit().putStringSet("playlist", playlists).apply();
        return created;
    }

    public void deletePlaylist(String id) {
        Playlist deleted = Playlist.get(id);
        deleted.clear();
        playlists.remove(id);
        pref.edit().putStringSet("playlist", playlists).apply();
    }

    public boolean savePlayable(Playable playable) {
        boolean result = !savedMusic.getPlaylist().contains(playable);
        if (!result) return false;

        savedMusic.add(playable);
        addedTimestamps.put(playable.getID(), System.currentTimeMillis() / 1000);
        try {
            pref.edit().putString("addedTimestamps", ObjectSerializer.serialize(addedTimestamps)).apply();
        } catch (IOException e) {
            Log.e(TAG, "savePlayable: unable to update 'addedTimestamps' preference!", e);
        }
        if (playable instanceof PlayableLocal) {
            hiddenMusic.remove(playable);
        }
        return true;
    }

    public boolean removePlayable(Playable playable) {
        boolean result = savedMusic.getPlaylist().contains(playable);
        if (!result) return false;

        savedMusic.remove(playable);
        addedTimestamps.remove(playable.getID());
        try {
            pref.edit().putString("addedTimestamps", ObjectSerializer.serialize(addedTimestamps)).apply();
        } catch (IOException e) {
            Log.e(TAG, "removePlayable: unable to update 'addedTimestamps' preference!", e);
        }
        if (playable instanceof PlayableLocal) {
            hiddenMusic.add(playable);
        }
        return true;
    }

    public boolean isSaved(Playable playable) {
        return savedMusic.getPlaylist().contains(playable);
    }

    public long getTimestampAdded(Playable playable) {
        if (addedTimestamps.containsKey(playable.getID())) {
            return addedTimestamps.get(playable.getID());
        }
        return 0;
    }

    /**
     * Called when a new playable has been added to a playlist.
     * @param playable playable added to playlist.
     * @param playlist affected playlist.
     */
    public void onPlayableAddedToPlaylist(Playable playable, Playlist playlist) {
        for (LibraryListener libraryListener : NoteStream.LIBRARY_LISTENERS) {
            libraryListener.onPlayableAddedToPlaylist(playable, playlist);
        }
    }

    /**
     * Called when a playable has been removed from a playlist.
     * @param playable playable removed from playlist.
     * @param playlist affected playlist.
     */
    public void onPlayableRemovedFromPlaylist(Playable playable, Playlist playlist) {
        for (LibraryListener libraryListener : NoteStream.LIBRARY_LISTENERS) {
            libraryListener.onPlayableRemovedFromPlaylist(playable, playlist);
        }
    }

    public interface LibraryListener {
        /**
         * Called when a new playable has been added to a playlist.
         * @param playable playable added to playlist.
         * @param playlist affected playlist.
         */
        void onPlayableAddedToPlaylist(Playable playable, Playlist playlist);

        /**
         * Called when a playable has been removed from a playlist.
         * @param playable playable removed from playlist.
         * @param playlist affected playlist.
         */
        void onPlayableRemovedFromPlaylist(Playable playable, Playlist playlist);
    }
}
