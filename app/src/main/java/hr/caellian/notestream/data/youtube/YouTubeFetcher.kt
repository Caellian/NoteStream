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

package hr.caellian.notestream.data.youtube

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.services.youtube.YouTube
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.data.playlist.PlaylistYouTube
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
        val search = youTube.search().list("id,snippet").apply {
            key = k
            maxResults = ResultCount
            q = query
            type = "video"
            fields = "items(id(videoId),snippet(channelTitle,thumbnails,title))"
        }

        val response = search.execute()

        response.items.forEach {
            val videoId = it.id.videoId

            val title = it.snippet.title
            val author = it.snippet.channelTitle

//            val def = it.snippet.thumbnails.default.url
//            val med = it.snippet.thumbnails.medium.url
            val high = it.snippet.thumbnails.high.url
//            val maxres = "http://i.ytimg.com/vi/$videoId/maxresdefault.jpg"

            val playable = PlayableYouTube(videoId, title, author)

            val decoder = ThumbnailDecoder({result  ->
                playable.info.cover = result
            })
            decoder.execute(high)

            result += playable
        }

        return result
    }

    fun searchPlaylists(query: String): List<PlaylistYouTube> {
        val result = mutableListOf<PlaylistYouTube>()

        val search = youTube.search().list("id,snippet").apply {
            key = k
            maxResults = ResultCount
            q = query
            type = "playlist"
            fields = "items(id/playlistId,snippet(channelTitle,thumbnails,title))"
        }

        val response = search.execute()

        response.items.forEach {
            val playlistId = it.id.playlistId

            val high = it.snippet?.thumbnails?.high?.url ?: "https://raw.githubusercontent.com/Caellian/NoteStream/master/resources/youtube-missing.png"

            val playlistYouTube = PlaylistYouTube(playlistId).apply {
                label = it.snippet.title
                author = it.snippet.channelTitle
            }

            playlistYouTube.cover = high

            result += playlistYouTube
        }

        return result
    }

    fun loadPlaylist(playlistID: String): MutableList<PlayableYouTube> {
        val result = mutableListOf<PlayableYouTube>()

        val search = youTube.playlistItems().list("id,snippet").apply {
            key = k
            maxResults = ResultCount
            playlistId = playlistID
            fields = "items(snippet(channelTitle,resourceId/videoId,thumbnails,title))"
        }

        val response = search.execute()

        response.items.forEach {
            val videoId = it.snippet.resourceId.videoId

            val title = it.snippet.title
            val author = it.snippet.channelTitle

//            val def = it.snippet.thumbnails.default.url
//            val med = it.snippet.thumbnails.medium.url
            val high = it.snippet.thumbnails.high.url
//            val maxres = "http://i.ytimg.com/vi/$videoId/maxresdefault.jpg"

            val playable = PlayableYouTube(videoId, title, author)

            val decoder = ThumbnailDecoder({result  ->
                playable.info.cover = result
            })
            decoder.execute(high)

            result += playable
        }

        return result
    }
}