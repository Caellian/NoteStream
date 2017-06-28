package hr.caellian.notestream.util;

import android.Manifest;

import java.io.Serializable;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;

/**
 * Created by caellyan on 27/06/17.
 */
public enum RequiredPermission implements Serializable {
    READ_EXTERNAL(Manifest.permission.READ_EXTERNAL_STORAGE,
            NoteStream.getInstance().getString(R.string.permission_read_external)),
    WRITE_EXTERNAL(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            NoteStream.getInstance().getString(R.string.permission_write_external)),
    ;

    String id;
    String title;

    RequiredPermission(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
