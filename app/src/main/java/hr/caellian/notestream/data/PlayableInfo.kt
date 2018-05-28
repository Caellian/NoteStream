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

package hr.caellian.notestream.data

import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.database.NoteStreamDB
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.Util
import kotlin.concurrent.thread

data class PlayableInfo(var parent: Playable, private val data: Map<String, Any>? = null) {
    var title: String? = data?.get(Constants.TRACK_TITLE)?.toString()
    var author: String? = data?.get(Constants.TRACK_AUTHOR)?.toString()

    var album: String? = data?.get(Constants.TRACK_ALBUM)?.toString()
    var year: Int? = data?.get(Constants.TRACK_YEAR) as? Int
    var track: Int? = data?.get(Constants.TRACK_TRACK) as? Int
    var genre: String? = data?.get(Constants.TRACK_GENRE)?.toString()
    var rating: Int = data?.get(Constants.TRACK_RATING) as? Int ?: 5
    var lyrics: String? = data?.get(Constants.TRACK_LYRICS)?.toString()

    var start: Int = data?.get(Constants.TRACK_START) as? Int ?: 0
    var end: Int = data?.get(Constants.TRACK_END) as? Int ?: 0
        get() {
            if (field == 0 && length != 0) field = length
            return field
        }
    var length: Int = data?.get(Constants.TRACK_LENGTH) as? Int ?: 0
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
        if (data == null) setFromDatabase()
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
            (data[Constants.TRACK_COVER_PATH] as? String?)?.also { coverPath = it }
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
