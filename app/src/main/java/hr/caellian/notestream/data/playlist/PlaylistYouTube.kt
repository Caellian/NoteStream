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

package hr.caellian.notestream.data.playlist

import android.graphics.Bitmap
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.data.youtube.YouTubeFetcher

class PlaylistYouTube(override val path: String) : PlaylistCore {
    override val source: PlayableSource = PlayableSource.YOUTUBE

    override var label: String = ""
    override var author: String = ""
    var cover = ""

    override var coverBitmaps = mutableListOf<Bitmap>()

    val playableIDs = mutableListOf<String>()

    override fun get(): Playlist {
        val playables = YouTubeFetcher.loadPlaylist(path)
        return Playlist.get(getId(), author, label, 512, playables)
    }

    fun getId(): String {
        return "${source.id}-$path"
    }
}