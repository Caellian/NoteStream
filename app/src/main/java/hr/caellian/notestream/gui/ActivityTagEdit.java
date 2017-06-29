package hr.caellian.notestream.gui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.lib.Constants;

/**
 * Created by caellyan on 27/06/17.
 */

public class ActivityTagEdit extends AppCompatActivity {
    Playable edited;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);

        edited = NoteStream.getInstance().library.savedMusic.getPlayable(getIntent().getStringExtra(Constants.EXTRA_PLAYABLE));
        if (edited == null) {
            edited = NoteStream.getInstance().library.localMusic.getPlayable(getIntent().getStringExtra(Constants.EXTRA_PLAYABLE));
        }

        if (edited == null) {
            Toast.makeText(ActivityTagEdit.this, getString(R.string.edit_unsaved), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
