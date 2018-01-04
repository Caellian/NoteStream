package hr.caellian.notestream.data

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log

import java.util.HashMap
import java.util.LinkedHashMap

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.database.PlaylistOpenHelper
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.Util

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
    var lyrics: String? = null

    var start: Int? = null
    var end: Int? = null
    var length: Int? = null
    var cover: Bitmap = DEFAULT_COVER

    init {
        setFromDatabase()
    }

    fun setFromDatabase() {
        if (parent is PlayableLocal) {
            setFromSource(parent.path)
        }
    }

    fun setFromSource(sourceLocation: String) {
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
            author = nAuthor
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

            mmr.embeddedPicture?.also {
                cover = BitmapFactory.decodeByteArray(it, 0, it.size)
            }
            mmr.release()
        } catch (e: IllegalArgumentException) {
            mmr.release()
        }

    }

    companion object {
        val DEFAULT_COVER: Bitmap = Util.drawableToBitmap(R.drawable.ic_song, 512, 512)
    }
}
