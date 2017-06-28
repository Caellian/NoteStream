package hr.caellian.notestream.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.data.playable.Playable;
import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * Created by caellyan on 16/04/17.
 */

public class Playlist implements Iterable<Playable>, Serializable {

    public static final int SORT_BY_TITLE = 1;
    public static final int SORT_BY_AUTHOR = 2;
    public static final int SORT_BY_DATE = 3;

    private ArrayList<Playable> playlist = new ArrayList<>();
    private ArrayList<Playable> shuffled = new ArrayList<>();

    private final String id;
    private transient Book book;

    private String label;
    private int capacity = 1;
    private int currentPlayable = 0;
    private int shuffledCurrent = 0;
    private boolean shuffle = false;

    public Playlist(String id) {
        this(id, new ArrayList<Playable>());
    }

    public Playlist(String id, Iterable<Playable> playables) {
        this(id, null, playables);
    }

    public Playlist(String id, Playlist parent, Iterable<Playable> playables) {
        this(id, parent, playables, 512);
    }

    public Playlist(String id, Playlist parent, Iterable<Playable> playables, int capacity) {
        this.id = id;
        this.book = Paper.book(id);

        playlist = book.read("playlist", new ArrayList<Playable>());
        if(playlist.isEmpty()) {
            for (Playable playable : playables) {
                playlist.add(playable);
            }
        }
        shuffled.addAll(playlist);

        this.capacity = Math.max(1, capacity);
    }

    public Playlist add(Playlist other) {
        if (other == null) return this;

        while (playlist.size() + other.size() - 1 >= capacity) {
            Playable removed = playlist.remove(0);
            shuffled.remove(removed);
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(removed, this);
        }

        for (Playable playable : other) {
            playlist.add(playable);
            shuffled.add(playable);
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);
        }

        savePlaylist();
        return this;
    }

    public Playlist add(Playable playable) {
        return add(playlist.size(), playable);
    }

    public Playlist add(int index, Playable playable) {
        if (playable == null || playlist.contains(playable)) return this;

        while (playlist.size() >= capacity) {
            Playable removed = playlist.remove(0);
            shuffled.remove(removed);
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(removed, this);
        }

        playlist.add(index, playable);
        shuffled.add(index, playable);
        if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);

        savePlaylist();
        return this;
    }

    public Playlist addRelative(int position, Playable playable) {
        if (playable == null) return this;

        while (playlist.size() >= capacity) {
            Playable removed = playlist.remove(playlist.size() - 1);
            shuffled.remove(removed);
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(removed, this);
        }

        playlist.add(currentPlayable + position, playable);
        shuffled.add(shuffledCurrent + position, playable);
        if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);

        savePlaylist();
        return this;
    }

    public Playlist addNext(Playable playable) {
        return addRelative(0, playable);
    }

    public Playlist remove(Playable playable) {
        int plpi = playlist.indexOf(playable);
        int shpi = shuffled.indexOf(playable);

        currentPlayable -= plpi >= currentPlayable ? 1 : 0;
        shuffledCurrent -= shpi >= shuffledCurrent ? 1 : 0;

        playlist.remove(playable);
        shuffled.remove(playable);
        if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(playable, this);

        savePlaylist();
        return this;
    }

    public Playlist clear() {
        currentPlayable = 0;
        shuffledCurrent = 0;

        for (Playable playable : playlist) {
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(playable, this);
        }

        playlist.clear();
        shuffled.clear();

        (book != null ? book : (book = Paper.book(id))).destroy();
        return this;
    }

    public int size() {
        return playlist.size();
    }

    public boolean skipTo(Playable playable) {
        if (shuffle) {
            for (Playable iterated: shuffled) {
                if (iterated == playable) {
                    shuffledCurrent = shuffled.indexOf(iterated);
                    return true;
                }
            }
        } else {
            for (Playable iterated: playlist) {
                if (iterated == playable) {
                    currentPlayable = playlist.indexOf(iterated);
                    return true;
                }
            }
        }

        return false;
    }

    public Playable getFirstPlayable() {
        return doShuffle() ? shuffled.get(0) : playlist.get(0);
    }

    public Playable getLastPlayable() {
        return doShuffle() ? shuffled.get(shuffled.size() - 1) : playlist.get(playlist.size() - 1);
    }

    public Playable getCurrentPlayable() {
        if (shuffle && shuffledCurrent < shuffled.size()) {
            return shuffled.get(shuffledCurrent);
        } else if (currentPlayable < playlist.size()) {
            return playlist.get(currentPlayable);
        }
        return null;
    }

    public void setShuffle(boolean shuffle) {
        if (shuffle) {
            Collections.shuffle(shuffled);
            shuffledCurrent = playlist.indexOf(getCurrentPlayable());
        } else {
            currentPlayable = playlist.indexOf(getCurrentPlayable());
        }
        this.shuffle = shuffle;
    }

    public Playable switchPrevious() {
        if (shuffle) {
            if (--shuffledCurrent < 0) {
                shuffledCurrent = shuffled.size() - 1;
            }
        } else {
            if (--currentPlayable < 0) {
                currentPlayable = playlist.size() - 1;
            }
        }
        return getCurrentPlayable();
    }

    public Playable switchNext() {
        if (shuffle) {
            if (++shuffledCurrent >= shuffled.size()) {
                Collections.shuffle(shuffled);
                shuffledCurrent = 0;
            }
        } else {
            if (++currentPlayable >= playlist.size()) {
                currentPlayable = 0;
            }
        }
        return getCurrentPlayable();
    }

    public ArrayList<Playable> getPlaylist() {
        return playlist;
    }

    public ArrayList<Playable> getShuffledPlaylist() {
        return shuffled;
    }

    public boolean doShuffle() {
        return shuffle;
    }

    public boolean isEmpty() {
        return playlist.isEmpty();
    }

    public String getLabel() {
        return label;
    }

    public Playlist setLabel(String label) {
        this.label = label;
        return this;
    }

    protected boolean savePlaylist() {
        if (id.startsWith("temporary")) return false;
        (book != null ? book : (book = Paper.book(id))).write("playlist", playlist);
        return true;
    }

    public Playlist filtered(String filter) {
        ArrayList<Playable> remaining = new ArrayList<>();

        for (Playable playable : this) {
            if (playable.getMetadata().getTitle().toLowerCase().contains(filter.toLowerCase())) remaining.add(playable);
            if (playable.getMetadata().getAuthor().toLowerCase().contains(filter.toLowerCase())) remaining.add(playable);
        }

        return new Playlist(id, this, remaining);
    }

    public Playlist sort(int order) {
        return sort(order, true);
    }

    public Playlist sort(int order, final boolean ascending) {
        switch (order) {
            case SORT_BY_TITLE:
                Collections.sort(playlist, new Comparator<Playable>() {
                    @Override
                    public int compare(Playable o1, Playable o2) {
                        if (ascending) {
                            return o1.getMetadata().getTitle().compareTo(o2.getMetadata().getTitle());
                        } else {
                            return o2.getMetadata().getTitle().compareTo(o1.getMetadata().getTitle());
                        }
                    }
                });
                break;
            case SORT_BY_AUTHOR:
                Collections.sort(playlist, new Comparator<Playable>() {
                    @Override
                    public int compare(Playable o1, Playable o2) {
                        if (ascending) {
                            return o1.getMetadata().getAuthor().compareTo(o2.getMetadata().getAuthor());
                        } else {
                            return o2.getMetadata().getAuthor().compareTo(o1.getMetadata().getAuthor());
                        }
                    }
                });
                break;
            case SORT_BY_DATE:
                Collections.sort(playlist, new Comparator<Playable>() {
                    @Override
                    public int compare(Playable o1, Playable o2) {
                        long o1t = NoteStream.getInstance().library.getTimestampAdded(o1);
                        long o2t = NoteStream.getInstance().library.getTimestampAdded(o2);

                        if (ascending) {
                            return (int) (o1t - o2t);
                        } else {
                            return (int) (o2t - o1t);
                        }
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Unsupported order argument!");
        }

        (book != null ? book : (book = Paper.book(id))).write("playlist", playlist);
        return this;
    }

    @Override
    public Iterator<Playable> iterator() {
        return playlist.iterator();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Playlist)) return false;
        Playlist other = (Playlist) obj;
        return this.id.equals(other.id);
    }
}
