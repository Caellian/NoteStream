/*
 * The MIT License (MIT)
 * NoteStream, android music player and streamer
 * Copyright (c) 2018 Tin Švagelj <tin.svagelj.email@gmail.com> a.k.a. Caellian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream

import android.app.Application
import android.content.*
import android.os.IBinder
import android.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import hr.caellian.notestream.data.NoteStreamData
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.lib.Constants
import java.util.*
import kotlin.system.exitProcess

class NoteStream : Application() {
    var psb: PlayerService.PlayerServiceBinder? = null
    lateinit var data: NoteStreamData
    lateinit var preferences: SharedPreferences
        private set

    var googleAccount: GoogleSignInAccount? = null
    var googleAccountCredential: GoogleAccountCredential? = null


    override fun onCreate() {
        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(instance)

        //Initialise player service for later use.
        val psi = Intent(instance, PlayerService::class.java)
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
        try {
            startService(psi)
        } catch (e: IllegalStateException) {
            // This exception is only ever raised when the app crashes and is restarted.
            exitProcess(0)
        }

        super.onCreate()
    }

    abstract class PlayerServiceListener {
        abstract fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder)
        open fun onPlayerServiceDisconnected() {}
    }

    companion object {
        lateinit var instance: NoteStream

        val PSB_LISTENERS = ArrayList<PlayerServiceListener>()
        val PROGRESS_LISTENERS = ArrayList<Playable.ProgressListener>()
        val CONTROL_LISTENERS = ArrayList<Playable.ControlListener>()
        val LIBRARY_LISTENERS = ArrayList<NoteStreamData.LibraryListener>()
        val AVAILABILITY_LISTENERS = ArrayList<PlayableRemote.AvailabilityListener>()

        fun registerPlayerServiceListener(listener: PlayerServiceListener) {
            if (instance.psb == null) {
                PSB_LISTENERS.add(listener)
            } else {
                listener.onPlayerServiceConnected(instance.psb!!)
            }
        }

        fun registerProgressListener(listener: Playable.ProgressListener) {
            PROGRESS_LISTENERS.add(listener)
        }

        fun registerControlListener(listener: Playable.ControlListener) {
            CONTROL_LISTENERS.add(listener)
        }

        fun registerLibraryListener(listener: NoteStreamData.LibraryListener) {
            LIBRARY_LISTENERS.add(listener)
        }

        fun registerAvailabilityListener(listener: PlayableRemote.AvailabilityListener) {
            AVAILABILITY_LISTENERS.add(listener)
        }
    }
}
