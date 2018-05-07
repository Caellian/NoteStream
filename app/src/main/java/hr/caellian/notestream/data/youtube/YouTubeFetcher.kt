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

package hr.caellian.notestream.data.youtube

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.services.youtube.YouTube
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.lib.Constants

import hr.caellian.notestream.lib.Constants.KA as a
import hr.caellian.notestream.lib.Constants.KC as b
import hr.caellian.notestream.lib.Constants.KE as c

object YouTubeFetcher {
    private val ResultCount: Long = 25
    private val k = "$a$b$c"

    val httpTransport = AndroidHttp.newCompatibleTransport()
    val jsonFactory = AndroidJsonFactory()

    private val youTube: YouTube =
            YouTube.Builder(httpTransport, jsonFactory, NoteStream.instance.googleAccountCredential)
            .setApplicationName(Constants.ApplicationName).build()

    fun searchVideos(query: String): List<PlayableYouTube> {
        val result = mutableListOf<PlayableYouTube>()
        val search = youTube.search().list("id,snippet")

        return result
    }

    fun searchPlaylists(query: String) {
        val search = youTube.search().list("id,snippet")

    }
}