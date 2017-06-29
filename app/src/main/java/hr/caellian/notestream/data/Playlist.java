package hr.caellian.notestream.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.playable.PlayableRemote;
import hr.caellian.notestream.data.playable.PlayableSource;
import hr.caellian.notestream.database.PlaylistOpenHelper;
import hr.caellian.notestream.lib.Constants;

/**
 * Created by caellyan on 16/04/17.
 */

public class Playlist implements Iterable<Playable>, Serializable {
    private static final String TAG = Playlist.class.getSimpleName();
    private static final HashMap<String, Playlist> initialized = new HashMap<>();

    public static final int SORT_BY_TITLE = 1;
    public static final int SORT_BY_AUTHOR = 2;
    public static final int SORT_BY_DATE = 3;

    public static final String TEMPORARY_PREFIX = "temporary_";
    public static final String FILTERED_PREFIX = "filtered_";
    public static final String GENRE_PREFIX = "genre_";
    public static final String ARTIST_PREFIX = "author_";
    public static final String ALBUM_PREFIX = "album_";

    private ArrayList<Playable> playlist = new ArrayList<>(0);
    private ArrayList<Playable> shuffled = new ArrayList<>(0);

    private final String id;
    private transient PlaylistOpenHelper dbHelper;

    private String label;
    private int capacity = 1;
    private int currentPlayable = 0;
    private int shuffledCurrent = 0;
    private boolean shuffle = false;

//    public Playlist(String youtubeID) {
//      TODO: Write this.
//    }

    private Playlist(String id) {
        this.id = id;
        dbHelper = new PlaylistOpenHelper(NoteStream.getInstance(), this, null);
    }

    public static Playlist get(String id) {
        return get(id, new ArrayList<Playable>(), 512);
    }

    public static Playlist get(String id, Iterable<Playable> data) {
        return get(id, data, 512);
    }

    public static Playlist get(String id, Iterable<Playable> data, int capacity) {
        if (initialized.containsKey(id)) {
            return initialized.get(id);
        } else {
            Playlist result = new Playlist(id);

            if (!id.startsWith(TEMPORARY_PREFIX) && result.dbHelper != null) {
                SQLiteDatabase db = result.dbHelper.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT * FROM " + result.dbHelper.getDatabaseName(), null);
                int row = 0;
                while (c.moveToPosition(row++)) {
                    try {
                        // TODO: For some reason file path is stored in field for source type tag.
                        Integer sourcePos = result.dbHelper.getTableColumns().get(MutableMediaMetadata.SOURCE);
                        if (sourcePos == null) continue;
                        PlayableSource source = PlayableSource.getByID(c.getString(sourcePos));
                        if (source != null) {
                            Playable toAdd = source.construct(c.getString(result.dbHelper.getTableColumns().get(MutableMediaMetadata.PATH)));
                            if (toAdd != null) {
                                toAdd.getMetadata().setFromDatabase(result.dbHelper, c);
                                result.playlist.add(toAdd);
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        Log.w(TAG, "Ignoring corrupted playable!", e);
                        e.printStackTrace();
                    }
                }

                c.close();
            }

            if(result.playlist.isEmpty()) {
                result.add(data);
            }
            result.shuffled.addAll(result.playlist);
            result.capacity = Math.max(1, capacity);

            initialized.put(id, result);
            return result;
        }
    }

    public Playlist add(Iterable<Playable> other) {
        if (other == null) return this;

        for (Playable playable : other) {
            playlist.add(playable);
            shuffled.add(playable);
            addPlayableToDB(playable);
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);
        }

        trimToCapacity(false);
        return this;
    }

    public Playlist add(Playable playable) {
        if (playable == null || playlist.contains(playable)) return this;

        playlist.add(playable);
        shuffled.add(playable);
        addPlayableToDB(playable);
        if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);

        trimToCapacity(false);
        return this;
    }

    public Playlist add(int index, Playable playable) {
        if (playable == null || playlist.contains(playable)) return this;


        playlist.add(index, playable);
        shuffled.add(index, playable);
        addPlayableToDB(playable);
        if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);

        trimToCapacity(false);
        return this;
    }

    public Playlist addRelative(int position, Playable playable) {
        if (playable == null) return this;

        playlist.add(currentPlayable + position, playable);
        shuffled.add(shuffledCurrent + position, playable);
        // TODO: Handle this correctly!
        addPlayableToDB(playable);
        if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableAddedToPlaylist(playable, this);
        trimToCapacity(true);

        return this;
    }

    public Playlist addNext(Playable playable) {
        return addRelative(0, playable);
    }

    public Playlist remove(Playable playable) {
        if (playlist.contains(playable)) {
            int plpi = playlist.indexOf(playable);
            int shpi = shuffled.indexOf(playable);

            currentPlayable -= plpi >= currentPlayable ? 1 : 0;
            shuffledCurrent -= shpi >= shuffledCurrent ? 1 : 0;

            playlist.remove(playable);
            shuffled.remove(playable);
            if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(playable, this);

            dbHelper.getWritableDatabase().execSQL("DELETE FROM " + dbHelper.getDatabaseName() + " WHERE " + MutableMediaMetadata.ID + "='" + playable.getID() + "';");
        }
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

        if (dbHelper != null && dbHelper.getDatabaseName() != null)
            dbHelper.getWritableDatabase().execSQL("DELETE FROM " + dbHelper.getDatabaseName());
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

        if (getCurrentPlayable() instanceof PlayableRemote && !((PlayableRemote) getCurrentPlayable()).isAvailable()) {
            return switchPrevious();
        } else {
            return getCurrentPlayable();
        }
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

        if (getCurrentPlayable() instanceof PlayableRemote && !((PlayableRemote) getCurrentPlayable()).isAvailable()) {
            return switchNext();
        } else {
            return getCurrentPlayable();
        }
    }

    public Playable getPlayable(String id) {
        for (Playable playable : playlist) {
            if (Objects.equals(playable.getID(), id)) return playable;
        }
        return null;
    }

    public String getID() {
        return id;
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

    protected void addPlayableToDB(final Playable playable) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (dbHelper.getDatabaseName() == null) return null;
                    String dbStatement = "INSERT OR REPLACE INTO " + dbHelper.getDatabaseName() + " (";
                    String values = "";
                    for (Map.Entry<String, String> entry : MutableMediaMetadata.properties.entrySet()) {
                        dbStatement += entry.getKey() + ", ";
                        values += "?, ";
                    }
                    values = values.substring(0, values.length() - 2);
                    dbStatement = dbStatement.substring(0, dbStatement.length() - 2) + ") VALUES (" + values + ");";
                    SQLiteStatement statement = dbHelper.getWritableDatabase().compileStatement(dbStatement);

                    int current = 1;
                    for (Map.Entry<String, String> entry : MutableMediaMetadata.properties.entrySet()) {
                        Object value = playable.getMetadata().get(entry.getKey());
                        if (value == null) {
                            statement.bindNull(current++);
                            continue;
                        }
                        switch (entry.getValue()) {
                            case Constants.SQL_TYPE_NULL:
                                statement.bindNull(current++);
                                break;
                            case Constants.SQL_TYPE_INTEGER:
                                //noinspection UnnecessaryBoxing
                                statement.bindLong(current++, Long.valueOf((int) value));
                                break;
                            case Constants.SQL_TYPE_REAL:
                                statement.bindDouble(current++, (double) value);
                                break;
                            case Constants.SQL_TYPE_TEXT:
                                statement.bindString(current++, (String) value);
                                break;
                            case Constants.SQL_TYPE_BLOB:
                                statement.bindBlob(current++, (byte[]) value);
                                break;
                        }
                    }
                    statement.execute();
                } catch (NullPointerException | ClassCastException e) {
                    Log.e(TAG, "add: Unable to add playable to DB!", e);
                }
                return null;
            }
        }.execute();
    }

    protected void trimToCapacity(boolean fromEnd) {
        if (fromEnd) {
            while (playlist.size() >= capacity) {
                Playable removed = playlist.remove(playlist.size() - 1);
                shuffled.remove(removed);
                if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(removed, this);
            }
        } else {
            while (playlist.size() >= capacity) {
                Playable removed = playlist.remove(0);
                shuffled.remove(removed);
                if (NoteStream.getInstance().library != null) NoteStream.getInstance().library.onPlayableRemovedFromPlaylist(removed, this);
            }
        }
    }

    public Playlist filtered(String filter) {
        Playlist result = new Playlist(TEMPORARY_PREFIX + FILTERED_PREFIX + id);
        result.clear();

        for (Playable playable : this) {
            if (playable.getMetadata().getTitle().toLowerCase().contains(filter.toLowerCase())) result.add(playable);
            if (playable.getMetadata().getAuthor().toLowerCase().contains(filter.toLowerCase())) result.add(playable);
        }

        return result;
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

        return this;
    }

    public Bitmap[] getCoverBitmaps() {
        Bitmap[] sources = new Bitmap[4];
        int cntr = 0;
        for (Playable playable : playlist) {
            if (cntr >= 4) break;
            Bitmap cover = playable.getMetadata().getCover();
            if (cover != null) {
                sources[cntr++] = cover;
            }
        }
        return sources;
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
