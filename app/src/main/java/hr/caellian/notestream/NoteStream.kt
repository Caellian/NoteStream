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

package hr.caellian.notestream

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.provider.MediaStore
import hr.caellian.notestream.data.Library
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.lib.Constants
import java.util.*

class NoteStream : Application() {
    var psb: PlayerService.PlayerServiceBinder? = null
    var library: Library? = null

    override fun onCreate() {
        instance = this

        //Initialise player service for later use.
        val psi = Intent(this, PlayerService::class.java)
        psi.action = Constants.ACTION_INIT

        bindService(psi, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                psb = service as PlayerService.PlayerServiceBinder
                for (psbListener in PSB_LISTENERS) {
                    psbListener.onPlayerServiceConnected(psb!!)
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                psb = null
            }
        }, Context.BIND_ABOVE_CLIENT)
        startService(psi)

        super.onCreate()
    }

    fun populateLibrary(activity: Activity) {
        val storageCheck = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (storageCheck == PackageManager.PERMISSION_GRANTED) {
            library = Library()
            addLocalContentToLibrary(activity, library!!)
        }
    }

    abstract class PlayerServiceListener {
        abstract fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder)
        open fun onPlayerServiceDisconnected() {}
    }

    companion object {
        var instance: NoteStream? = null

        val PSB_LISTENERS = ArrayList<PlayerServiceListener>()
        val PROGRESS_LISTENERS = ArrayList<Playable.ProgressListener>()
        val CONTROL_LISTENERS = ArrayList<Playable.ControlListener>()
        val LIBRARY_LISTENERS = ArrayList<Library.LibraryListener>()
        val AVAILABILITY_LISTENERS = ArrayList<PlayableRemote.AvailabilityListener>()

        fun registerPlayerServiceListener(listener: PlayerServiceListener) {
            if (instance?.psb == null) {
                PSB_LISTENERS.add(listener)
            } else {
                listener.onPlayerServiceConnected(instance?.psb!!)
            }
        }

        fun registerProgressListener(listener: Playable.ProgressListener) {
            PROGRESS_LISTENERS.add(listener)
        }

        fun registerControlListener(listener: Playable.ControlListener) {
            CONTROL_LISTENERS.add(listener)
        }

        fun registerLibraryListener(listener: Library.LibraryListener) {
            LIBRARY_LISTENERS.add(listener)
        }

        fun registerAvailabilityListener(listener: PlayableRemote.AvailabilityListener) {
            AVAILABILITY_LISTENERS.add(listener)
        }

        private fun addLocalContentToLibrary(context: Context, library: Library) {
            val extUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
            val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

            val cur = context.contentResolver.query(extUri, null, selection, null, sortOrder)

            if (cur != null) {
                if (cur.count > 0) {
                    OuterLoop@ while (cur.moveToNext()) {
                        val path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))

                        if (library.localMusic.playlist.any { it.id == PlayableLocal.getId(path) }) continue@OuterLoop

                        val playable = PlayableLocal(path)
                        if (!playable.info.setFromDatabase()) {
                            playable.info.setFromSource(path)
                        }
                        library.localMusic.add(playable)
                        library.savedMusic.add(playable)
                    }
                }
                cur.close()
            }
        }
    }
}
