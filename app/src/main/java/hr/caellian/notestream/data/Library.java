package hr.caellian.notestream.data;

import java.util.ArrayList;
import java.util.HashMap;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.playable.Playable;
import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * Created by caellyan on 17/06/17.
 */

public class Library {
    private final Book book;

    public ArrayList<String> playlists = new ArrayList<>();

    public Playlist localMusic = new Playlist("localMusic");
    public Playlist savedMusic = new Playlist("savedMusic");
    public Playlist hiddenMusic = new Playlist("hiddenMusic");
    public Playlist favoriteMusic = new Playlist("favoriteMusic")
            .setLabel(NoteStream.getInstance().getString(R.string.label_favorites));

    public Playlist lastListened = new Playlist("lastListened", null, new ArrayList<Playable>(0), 20);

    HashMap<String, Long> addedTimestamps;

    private static final ArrayList<LibraryListener> libraryListeners = new ArrayList<>();

    public Library() {
        book = Paper.book("library");

        playlists.add("favoriteMusic");
        playlists = book.read("playlists", new ArrayList<String>());
        addedTimestamps = book.read("addedTimestamps", new HashMap<String, Long>());
    }

    public Playlist createPlaylist(String id) {
        Playlist created = new Playlist(id);
        playlists.add(id);
        book.write("playlists", playlists);
        return created;
    }

    public void deletePlaylist(String id) {
        Playlist deleted = new Playlist(id);
        deleted.clear();
        playlists.remove(id);
        book.write("playlists", playlists);
    }

    public boolean savePlayable(Playable playable) {
        boolean result = !savedMusic.getPlaylist().contains(playable);
        if (!result) return false;

        savedMusic.add(playable);
        addedTimestamps.put(playable.getPlayableId(), System.currentTimeMillis() / 1000);
        book.write("addedTimestamps", addedTimestamps);
        if (playable instanceof PlayableLocal) {
            hiddenMusic.remove(playable);
        }
        return true;
    }

    public boolean removePlayable(Playable playable) {
        boolean result = savedMusic.getPlaylist().contains(playable);
        if (!result) return false;

        savedMusic.remove(playable);
        addedTimestamps.remove(playable.getPlayableId());
        book.write("addedTimestamps", addedTimestamps);
        if (playable instanceof PlayableLocal) {
            hiddenMusic.add(playable);
        }
        return true;
    }

    public boolean isSaved(Playable playable) {
        return savedMusic.getPlaylist().contains(playable);
    }

    public long getTimestampAdded(Playable playable) {
        if (addedTimestamps.containsKey(playable.getPlayableId())) {
            return addedTimestamps.get(playable.getPlayableId());
        }
        return 0;
    }

    public void registerLibraryListener(LibraryListener listener) {
        libraryListeners.add(listener);
    }

    /**
     * Called when a new playable has been added to a playlist.
     * @param playable playable added to playlist.
     * @param playlist affected playlist.
     */
    public void onPlayableAddedToPlaylist(Playable playable, Playlist playlist) {
        for (LibraryListener libraryListener : libraryListeners) {
            libraryListener.onPlayableAddedToPlaylist(playable, playlist);
        }
    }

    /**
     * Called when a playable has been removed from a playlist.
     * @param playable playable removed from playlist.
     * @param playlist affected playlist.
     */
    public void onPlayableRemovedFromPlaylist(Playable playable, Playlist playlist) {
        for (LibraryListener libraryListener : libraryListeners) {
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
