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

import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R

enum class PlayableSource(val id: String, private val displayName: Int) {
    LOCAL("local", R.string.location_local),
    YOUTUBE("remote-youtube", R.string.location_youtube),
    OTHER("unknown", R.string.location_unknown);

    fun localizedDisplayName(): String {
        return NoteStream.instance?.getString(this.displayName) ?: ""
    }

    companion object {
        fun getByID(id: String): PlayableSource? {
            values().filter { it.id == id.toLowerCase() }
                    .forEach { return it }

            Log.e(PlayableSource::class.java.simpleName, "getByID: ", IllegalArgumentException("Unsupported source id: '" + id.toLowerCase() + "'!"))
            return null
        }
    }
}
