package hr.caellian.notestream.data.playable

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.SparseArray
import android.widget.Toast

import java.io.IOException
import java.util.HashMap

import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayableInfo
import hr.caellian.notestream.data.youtube.ThumbnailDecoder

/**
 * Created by caellyan on 22/06/17.
 */

class PlayableYouTube(youtubeID: String) : PlayableDownloadable() {

    override val id: String = getId(youtubeID)
    override val path: String = youtubeID

    protected var youtubeURL: String? = null
    protected var extension: String? = null

    override val info: PlayableInfo = PlayableInfo(this)

    override val playableSource: PlayableSource = PlayableSource.YOUTUBE
    override val location: String = playableSource.localizedDisplayName()

    internal class Extractor(private val parent: PlayableYouTube) : YouTubeExtractor(NoteStream.instance) {
        public override fun onExtractionComplete(files: SparseArray<YtFile>?, videoMeta: VideoMeta) {
            if (files != null) {
                val itag = 140
                parent.youtubeURL = files.get(itag).url
                parent.extension = files.get(itag).format.ext

                val info = parent.info
                if (info.title == null) {
                    info.title = videoMeta.title
                }
                if (info.author == null) {
                    info.author = videoMeta.author
                }
                if (info.length == null) {
                    info.length = videoMeta.videoLength.toInt() * 1000
                }
                if (info.end == null) {
                    info.end = info.length
                }

                val decoder = ThumbnailDecoder({ result ->
                    if (info.cover == PlayableInfo.DEFAULT_COVER) {
                        info.cover = result
                    }
                })
                decoder.execute(videoMeta)

                parent.available = true
            }
        }
    }

    init {
        val ytExtractor = Extractor(this)
        ytExtractor.execute(youtubeID)
    }

    override fun prepare(mp: MediaPlayer): Boolean {
        mp.reset()
        try {
            youtubeURL?.also {
                mp.setDataSource(it)
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mp.prepare()
                mp.seekTo(info.start ?: 0)
            }
        } catch (e: IOException) {
            return false
        }

        return true
    }

    override fun skipTo(mp: MediaPlayer, ms: Int): Boolean {
        mp.seekTo(Math.max(0, Math.min(ms, info.length ?: 0)))
        return true
    }

    //    public ArrayList<PlayableYouTube> getSuggestions() {
    //        ArrayList<PlayableYouTube> result = new ArrayList<>();
    //
    //        for (String id : YouTubeFetcher.getSuggestionsFor(youtubeID)) {
    //            result.add(new PlayableYouTube(id));
    //        }
    //
    //        return result;
    //    }

    override fun download(): Boolean {
        val writeExternal = NoteStream.instance?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (writeExternal != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(NoteStream.instance, "Write External Storage permission not granted!", Toast.LENGTH_LONG).show()
            return false
        }

        val uri = Uri.parse(youtubeURL)
        val request = DownloadManager.Request(uri)
        request.setTitle("${NoteStream.instance?.getString(R.string.app_name) ?: "NoteStream"} " +
                "${NoteStream.instance?.getString(R.string.label_download) ?: "Download"}: " +
                (info.title ?: "<unknown>"))

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, info.title + "." + extension)

        val manager = NoteStream.instance?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        manager?.enqueue(request)
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is PlayableYouTube) return false

        return path == other.path
    }

    companion object {
        private val ID_PREFIX = "playable-youtube-"

        fun getId(youtubeID: String): String {
            return ID_PREFIX + youtubeID
        }
    }
}
