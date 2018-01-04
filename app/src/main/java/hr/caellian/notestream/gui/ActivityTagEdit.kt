package hr.caellian.notestream.gui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 27/06/17.
 */

class ActivityTagEdit : AppCompatActivity() {
    internal var edited: Playable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_edit)

        edited = NoteStream.instance?.library?.savedMusic?.getPlayable(intent.getStringExtra(Constants.EXTRA_PLAYABLE))
        if (edited == null) {
            edited = NoteStream.instance?.library?.localMusic?.getPlayable(intent.getStringExtra(Constants.EXTRA_PLAYABLE))
        }

        if (edited == null) {
            Toast.makeText(this@ActivityTagEdit, getString(R.string.edit_unsaved), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
