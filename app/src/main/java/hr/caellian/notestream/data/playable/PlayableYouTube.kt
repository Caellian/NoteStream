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

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.SparseArray
import android.widget.Toast
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayableInfo
import hr.caellian.notestream.data.youtube.ThumbnailDecoder
import hr.caellian.notestream.data.youtube.YouTubeFetcher
import hr.caellian.notestream.lib.Constants
import java.io.IOException

class PlayableYouTube(val youtubeID: String,
                      override var title: String = NoteStream.instance.getString(R.string.unknown_title),
                      override var author: String = NoteStream.instance.getString(R.string.unknown_artist),
                      data: Map<String, Any>? = null) : PlayableDownloadable() {

    override val id: String = data?.get(Constants.TRACK_ID)?.toString() ?: getId(youtubeID)
    override val path: String = youtubeID

    private var downloadURL: String? = null
    private var extension: String? = null

    override val info: PlayableInfo = PlayableInfo(this, data)

    override val playableSource: PlayableSource = PlayableSource.YOUTUBE

    internal class Extractor(private val parent: PlayableYouTube, private val info: PlayableInfo) : YouTubeExtractor(NoteStream.instance) {
        companion object {
            var working = false
        }

        override fun onPreExecute() {
            if (working) {
                cancel(true)
            } else {
                working = true
            }
        }

        public override fun onExtractionComplete(files: SparseArray<YtFile>?, videoMeta: VideoMeta) {
            if (files != null) {
                val itag = 140
                parent.downloadURL = files.get(itag).url
                parent.extension = files.get(itag).format.ext
                parent.available = true

                parent.title = videoMeta.title
                parent.author = videoMeta.author

                info.title = info.title ?: videoMeta.title
                info.author = info.author ?: videoMeta.author

                info.length = videoMeta.videoLength.toInt()
                if (info.end != 0) {
                    info.end = info.length
                }

                if (info.cover == PlayableInfo.DEFAULT_COVER) {
                    val decoder = ThumbnailDecoder({ result ->
                        info.cover = result
                    })
                    decoder.execute(videoMeta)
                }

                working = false
            }
        }
    }

    override fun makeAvailable() {
        if (!available && !Extractor.working) {
            Extractor(this, info).execute(youtubeID)
        }
    }

    override fun prepare(mp: MediaPlayer): Boolean {
        mp.reset()
        try {
            downloadURL?.also {
                mp.setDataSource(it)
                mp.setAudioAttributes(AudioAttributes.Builder().setFlags(AudioAttributes.CONTENT_TYPE_MUSIC or AudioAttributes.USAGE_MEDIA).build())
                mp.prepare()
                mp.seekTo(info.start)
            }
        } catch (e: IOException) {
            return false
        }

        return true
    }

    override fun skipTo(mp: MediaPlayer, ms: Int): Boolean {
        mp.seekTo(Math.max(0, Math.min(ms, info.length)))
        return true
    }

    override fun download(): PlayableLocal? {
        val writeExternal = NoteStream.instance.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (writeExternal != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(NoteStream.instance, R.string.permission_access_external_fail, Toast.LENGTH_LONG).show()
            return null
        }

        val uri = Uri.parse(downloadURL)
        val request = DownloadManager.Request(uri)
        request.setTitle("${NoteStream.instance.getString(R.string.app_name) ?: "NoteStream"} ${NoteStream.instance.getString(R.string.label_download)
                ?: "Download"}: $title")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$title.$extension")

        val manager = NoteStream.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        manager?.enqueue(request)

        // TODO: Return PlayableLocal
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is PlayableYouTube) return false

        return path == other.path
    }

    companion object {
        private const val ID_PREFIX = "ns-playable-youtube-"

        fun getId(youtubeID: String): String {
            return ID_PREFIX + youtubeID
        }

        fun getSearch(data: String): PlayableYouTube? {
            return YouTubeFetcher.searchVideos(data).firstOrNull()
        }
    }
}
