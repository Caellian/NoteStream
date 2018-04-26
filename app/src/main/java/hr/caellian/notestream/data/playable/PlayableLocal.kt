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

package hr.caellian.notestream.data.playable

import android.media.MediaPlayer
import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayableInfo
import hr.caellian.notestream.lib.Constants
import java.io.IOException

class PlayableLocal(override val path: String,
                    override var title: String = NoteStream.instance.getString(R.string.unknown_title),
                    override var author: String = NoteStream.instance.getString(R.string.unknown_artist),
                    data: Map<String, Any>? = null) : Playable {

    override var id: String = data?.get(Constants.TRACK_ID)?.toString() ?: getId(path)
        private set

    @Transient
    override val info: PlayableInfo = PlayableInfo(this, data)

    override val playableSource: PlayableSource = PlayableSource.LOCAL

    override fun prepare(mp: MediaPlayer): Boolean {
        mp.reset()
        try {
            mp.setDataSource(path)
            mp.prepare()
            mp.seekTo(this.info.start)
        } catch (e: IllegalStateException) {
            Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e)
            return false
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e)
            return false
        } catch (e: IOException) {
            Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e)
            return false
        }

        return true
    }

    override fun skipTo(mp: MediaPlayer, ms: Int): Boolean {
        mp.seekTo(Math.max(0, Math.min(ms, info.length)))
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is PlayableLocal) return false

        return path == other.path
    }

    companion object {
        val TAG: String = PlayableLocal::class.java.simpleName

        private const val ID_PREFIX = "ns-playable-local-"

        fun getId(path: String): String {
            return ID_PREFIX + path.replace("[^A-Za-z0-9]".toRegex(), "")
        }
    }
}
