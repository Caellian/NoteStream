/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.gui.fragments.welcome

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.NoteStreamData
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.database.NoteStreamDB
import hr.caellian.notestream.gui.ActivityLibrary

import kotlinx.android.synthetic.main.content_loading.*

class FragmentLoadingScreen : Fragment() {
    var ready = false
    var loaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_loading, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (ready) {
            load()
        }
    }

    override fun onResume() {
        super.onResume()
        if (loaded) {
            activity?.finish()
        }
    }

    fun whenScrolledTo() {
        if (!ready) {
            // This is unexpected behaviour due to use of LockableViewPager.
            // It's still here in case the lock is removed in future.
            Toast.makeText(activity, R.string.welcome_loading_not_ready, Toast.LENGTH_LONG).show()
        } else {
            load()
        }
    }

    private fun load() {
        val data = NoteStreamData()

        NoteStreamDB.vacuum()

        if (NoteStream.instance.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val extUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
            val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

            val cur = NoteStream.instance.contentResolver.query(extUri, null, selection, null, sortOrder)
            if (cur != null) {
                if (cur.count > 0) {
                    progressBarLoading.max = cur.count.toFloat()
                    var counter = 1
                    OuterLoop@ while (cur.moveToNext()) {
                        progressBarLoading.progress = counter++.toFloat()
                        val path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))

                        textViewLoading.text = NoteStream.instance.getString(R.string.checking_pointer, path)

                        if (data.localMusic.playlist.any { it.id == PlayableLocal.getId(path) }) continue@OuterLoop

                        textViewLoading.text = NoteStream.instance.getString(R.string.loading_pointer, path)

                        val playable = PlayableLocal(path)
                        if (!playable.info.setFromDatabase()) {
                            playable.info.setFromSource(path)
                        }

                        data.localMusic.add(playable)
                        data.savedMusic.add(playable)
                    }
                }
                cur.close()
            }
        }

        NoteStream.instance.data = data

        val intent = Intent(NoteStream.instance, ActivityLibrary::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        loaded = true
        startActivity(intent)
    }
}