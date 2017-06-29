package hr.caellian.notestream.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

import hr.caellian.notestream.data.MutableMediaMetadata;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.data.playable.PlayableSource;

/**
 * Created by tinsv on 30/06/2017.
 */

public class PlaylistOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String PLAYLIST_TABLE_PREFIX = "playlist_";
    private static HashMap<String, Integer> TABLE_COLUMNS = null;

    Playlist parent;

    public PlaylistOpenHelper(Context context, Playlist source, SQLiteDatabase.CursorFactory factory) {
        super(context, source.getID().startsWith(Playlist.TEMPORARY_PREFIX) ? null : PLAYLIST_TABLE_PREFIX + source.getID(), factory, DATABASE_VERSION);
        this.parent = source;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + PLAYLIST_TABLE_PREFIX + parent.getID() + " (";

        // Store serialization data and playable metadata here:
        for (Map.Entry<String, String> entry : MutableMediaMetadata.properties.entrySet()) {
//            if (Objects.equals(entry.getKey(), MutableMediaMetadata.ID)) {
//                createQuery += entry.getKey() + " PRIMARY KEY " + entry.getValue() + ", ";
//                continue;
//            }
            createQuery += entry.getKey() + " " + entry.getValue() + ", ";
        }

        createQuery = createQuery.substring(0, createQuery.length() - 2) + ");";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        HashMap<String, String> remaining = new HashMap<>(MutableMediaMetadata.properties);

        // Get info about table columns
        Cursor c = db.rawQuery("PRAGMA table_info(" + PLAYLIST_TABLE_PREFIX + parent.getID() + ");", null);
        int row = 0;
        while (c.moveToPosition(row++)) {
            // Remove all existing table columns from remaining map
            String name = c.getString(1);
            remaining.remove(name);
        }
        c.close();

        String extendTableQuerry = "";

        for (Map.Entry<String, String> entry : remaining.entrySet()) {
            // Add missing registered playable metadata columns
            extendTableQuerry += "ALTER TABLE " + PLAYLIST_TABLE_PREFIX + parent.getID() + " ADD COLUMN " + entry.getKey() + " " + entry.getValue() + " NULL; ";
        }

        db.execSQL(extendTableQuerry);
    }

    public HashMap<String, Integer> getTableColumns() {
        if (TABLE_COLUMNS != null) {
            return TABLE_COLUMNS;
        } else {
            TABLE_COLUMNS = new HashMap<>();

            Cursor c = getReadableDatabase().rawQuery("PRAGMA table_info(" + PLAYLIST_TABLE_PREFIX + parent.getID() + ");", null);
            int row = -1;
            while (c.moveToPosition(++row)) {
                // Remove all existing table columns from remaining map
                String name = c.getString(1);
                TABLE_COLUMNS.put(name, row);
            }
            c.close();

            return TABLE_COLUMNS;
        }
    }
}
