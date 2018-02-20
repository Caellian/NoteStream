package hr.caellian.notestream.data

import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.database.NoteStreamDB
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.Util
import kotlin.concurrent.thread


/**
 * Created by caellyan on 24/06/17.
 */

data class PlayableInfo(var parent: Playable) {
    val id: String = parent.id
    val source: PlayableSource = parent.playableSource
    val path: String = parent.path

    var title: String? = null
    var author: String? = null
    var album: String? = null
    var year: Int? = null
    var track: Int? = null
    var genre: String? = null
    var rating: Int = 5
    var lyrics: String? = null

    var start: Int = 0
    var end: Int = 0
        get() {
            if (field == 0 && length != 0) field = length
            return field
        }
    var length: Int = 0
    var coverPath: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                field = value
                cover = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(value), 512, 512)
            }
        }
    var cover: Bitmap = DEFAULT_COVER

    private var inited = false

    init {
        setFromDatabase()
    }

    fun setFromDatabase(): Boolean {
        try {
            val data = NoteStreamDB.getPlayableData(parent)
            if (data.isEmpty()) {
                inited = false
                return false
            }
            title = data[Constants.TRACK_TITLE] as? String?
            parent.title = data[Constants.TRACK_TITLE] as? String? ?: parent.title
            author = data[Constants.TRACK_AUTHOR] as? String?
            parent.author = data[Constants.TRACK_AUTHOR] as? String? ?: parent.author
            album = data[Constants.TRACK_ALBUM] as? String?
            year = data[Constants.TRACK_YEAR] as? Int?
            track = data[Constants.TRACK_TRACK] as? Int?
            genre = data[Constants.TRACK_GENRE] as? String?
            rating = data[Constants.TRACK_RATING] as? Int? ?: rating
            lyrics = data[Constants.TRACK_LYRICS] as? String?

            start = data[Constants.TRACK_START] as? Int? ?: start
            end = data[Constants.TRACK_END] as? Int? ?: end
            length = data[Constants.TRACK_LENGTH] as? Int? ?: length
            (data[Constants.TRACK_COVER_PATH] as? String?)?. also { coverPath = it }
            inited = true
        } catch (e: SQLiteException) {
            inited = false
        }

        return inited
    }

    fun setFromSource(sourceLocation: String): Boolean {
        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(sourceLocation)

            var nTitle: String? = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            var nAuthor: String? = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
            if (nAuthor == null) {
                nAuthor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            }
            if (nTitle == null) {
                nTitle = sourceLocation.substring(sourceLocation.lastIndexOf("/") + 1, if (sourceLocation.contains(".")) sourceLocation.lastIndexOf(".") else sourceLocation.length - 1)

                if (nAuthor == null && nTitle.contains("-")) {
                    nAuthor = nTitle.substring(0, nTitle.lastIndexOf("-")).trim { it <= ' ' }
                    nTitle = nTitle.substring(nTitle.lastIndexOf("-") + 1).trim { it <= ' ' }
                }
            }
            if (nAuthor == null) {
                nAuthor = "Unknown"
            }

            title = nTitle
            parent.title = title ?: parent.title
            author = nAuthor
            parent.author = author ?: parent.author
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)?.also {
                Regex.fromLiteral("\\d+").find(it)?.also { year = Integer.parseInt(it.value) }
            }
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.also {
                Regex.fromLiteral("\\d+").find(it)?.also { track = Integer.parseInt(it.value) }
            }
            genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
            length = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
            end = length

            // TODO: Read lyrics from file!

            thread {
                mmr.embeddedPicture?.also {
                    cover = BitmapFactory.decodeByteArray(it, 0, it.size)
                }
                mmr.release()
            }
        } catch (e: IllegalArgumentException) {
            mmr.release()
        }
        inited = true
        return inited
    }

    companion object {
        val DEFAULT_COVER: Bitmap = Util.drawableToBitmap(R.drawable.ic_song, 512, 512)
    }
}
