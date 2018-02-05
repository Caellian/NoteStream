package hr.caellian.notestream.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.lib.Constants

/**
 * Created by tinsv on 30/06/2017.
 */

class PlaylistOpenHelper(context: Context, internal var parent: Playlist, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, if (parent.id.startsWith(Constants.PLAYLIST_TEMPORARY_PREFIX)) null else PLAYLIST_TABLE_PREFIX + parent.id, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //        String createQuery = "CREATE TABLE " + PLAYLIST_TABLE_PREFIX + parent.getId() + " (";
        //
        //        // Store serialization data and playable metadata here:
        //        for (Map.Entry<String, String> entry : PlayableInfo.properties.entrySet()) {
        ////            if (Objects.equals(entry.getKey(), PlayableInfo.id)) {
        ////                createQuery += entry.getKey() + " PRIMARY KEY " + entry.getValue() + ", ";
        ////                continue;
        ////            }
        //            createQuery += entry.getKey() + " " + entry.getValue() + ", ";
        //        }
        //
        //        createQuery = createQuery.substring(0, createQuery.length() - 2) + ");";
        //        db.execSQL(createQuery);
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //        HashMap<String, String> remaining = new HashMap<>(PlayableInfo.properties);
        //
        //        // Get info about table columns
        //        Cursor c = db.rawQuery("PRAGMA table_info(" + PLAYLIST_TABLE_PREFIX + parent.getId() + ");", null);
        //        int row = 0;
        //        while (c.moveToPosition(row++)) {
        //            // Remove all existing table columns from remaining map
        //            String name = c.getString(1);
        //            remaining.remove(name);
        //        }
        //        c.close();
        //
        //        String extendTableQuerry = "";
        //
        //        for (Map.Entry<String, String> entry : remaining.entrySet()) {
        //            // Add missing registered playable metadata columns
        //            extendTableQuerry += "ALTER TABLE " + PLAYLIST_TABLE_PREFIX + parent.getId() + " ADD COLUMN " + entry.getKey() + " " + entry.getValue() + " NULL; ";
        //        }
        //
        //        db.execSQL(extendTableQuerry);
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val PLAYLIST_TABLE_PREFIX = "playlist_"
    }
}
