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

package hr.caellian.notestream.gui

import android.content.Context
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableDownloadable
import hr.caellian.notestream.data.playable.PlayableLocal
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.lib.Constants

class PlayablePopupMenu(internal var parentContext: Context, anchor: View, internal var playable: Playable) : PopupMenu(parentContext, anchor), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    init {
        menuInflater.inflate(R.menu.menu_playable, menu)

        if (playable is PlayableLocal) {
            menuInflater.inflate(R.menu.menu_playable_local, menu)
        }
        if (playable is PlayableDownloadable) {
            menuInflater.inflate(R.menu.menu_playable_downloadable, menu)
        }
        if (playable is PlayableYouTube) {
            menuInflater.inflate(R.menu.menu_playable_youtube, menu)
        }

        setOnMenuItemClickListener(this)
    }

    override fun onClick(view: View) {
        show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val psb = NoteStream.instance?.psb

        when (item.itemId) {
            R.id.add_library -> NoteStream.instance?.library?.savePlayable(playable)
            R.id.add_playlist -> {
                // TODO: Open argumentPlaylist list
            }
            R.id.add_favorites -> NoteStream.instance?.library?.favoriteMusic?.add(playable)
            R.id.download -> (playable as PlayableDownloadable).download()
            R.id.play_next -> psb?.playNext(playable)
            R.id.add_queue -> psb?.addToQueue(playable)
            R.id.show_album -> {
                val albumIntent = Intent(parentContext, ActivityPlaylist::class.java)
                val albumID = NoteStream.instance?.library?.albums?.get(playable.info.album)?.id
                albumIntent.putExtra(Constants.EXTRA_PLAYLIST, albumID)
                parentContext.startActivity(albumIntent)
            }
            R.id.show_artist -> {
                val artistIntent = Intent(parentContext, ActivityPlaylist::class.java)
                val artistID = NoteStream.instance?.library?.artists?.get(playable.info.author)?.id
                artistIntent.putExtra(Constants.EXTRA_PLAYLIST, artistID)
                parentContext.startActivity(artistIntent)
            }
            R.id.share -> if (playable is PlayableLocal) {
                val localPlayable = playable as PlayableLocal

                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, localPlayable.path)
                sendIntent.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(localPlayable.path.substring(localPlayable.path.lastIndexOf(".") + 1)))
                parentContext.startActivity(Intent.createChooser(sendIntent, "Share Music"))
            } else if (playable is PlayableYouTube) {
                val localPlayable = playable as PlayableYouTube

                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, "http://youtube.com/watch?v=" + localPlayable.path)
                sendIntent.type = "text/plain"
                parentContext.startActivity(Intent.createChooser(sendIntent, "Share Video URL"))
            }
//            R.id.edit_tags -> {
//                val editorIntent = Intent(parentContext, ActivityTagEdit::class.java)
//                editorIntent.putExtra(Constants.EXTRA_PLAYABLE, argumentPlayable.id)
//                parentContext.startActivity(editorIntent)
//            }
            R.id.delete -> {

                // TODO: Delete file
            }
            R.id.suggestions -> {
                // TODO: Show YT suggestions
            }
        }
        // TODO: Share argumentPlayable
        return true
    }
}
