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

package hr.caellian.notestream.gui.fragments

import android.content.Intent
import android.os.Bundle

import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.gui.ActivityPlaylist
import hr.caellian.notestream.lib.Constants

class FragmentPlaylistTile : FragmentTile() {
    override fun onClickListener(data: String) {
        val intent = Intent(view.context, ActivityPlaylist::class.java)
        intent.putExtra(Constants.EXTRA_PLAYLIST, data)
        startActivity(intent)
    }

    companion object {
        fun create(playlist: Playlist, iconID: Int, tiledDisplay: Boolean = true): FragmentPlaylistTile {
            val fragment = FragmentPlaylistTile()

            val args = Bundle()
            args.putBoolean(Constants.EXTRA_TILES, tiledDisplay)
            args.putInt(Constants.EXTRA_ICON, iconID)
            args.putString(Constants.EXTRA_LABEL, playlist.label)
            args.putString(Constants.EXTRA_LABEL_DESCRIPTION, playlist.author)
            args.putString(Constants.EXTRA_DATA, playlist.id)
            fragment.arguments = args
            return fragment
        }
    }
}
