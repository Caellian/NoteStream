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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.PlayableSource
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.data.playlist.PlaylistYouTube
import hr.caellian.notestream.data.youtube.ThumbnailDecoder
import hr.caellian.notestream.gui.ActivityPlaylist
import hr.caellian.notestream.lib.Constants
import java.util.ArrayList

class FragmentTileYouTube : FragmentPlaylistTile() {

    lateinit var id: String
    lateinit var label: String
    lateinit var author: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_tile, container, false)

        id = arguments.getString(Constants.EXTRA_PLAYLIST)!!
        label = arguments.getString(Constants.EXTRA_LABEL, "")
        author = arguments.getString(Constants.EXTRA_LABEL_DESCRIPTION, "")
        val data = arguments.getStringArrayList(Constants.EXTRA_DATA)!!

        val background = arguments.getString(Constants.EXTRA_BACKGROUND)!!

        val decoder = ThumbnailDecoder({result  ->
            view.findViewById<ImageView>(R.id.imageTileBackground)?.setImageBitmap(result)
        })
        decoder.execute(background)

        view.findViewById<ImageView>(R.id.imageTileIcon)?.visibility = View.GONE
        view.findViewById<TextView>(R.id.labelTileTitle)?.text = label
        view.findViewById<TextView>(R.id.labelSongAuthor)?.text = author

        view.setOnClickListener {
            onClickListener(data)
        }

        return view
    }

    fun onClickListener(data: ArrayList<String>) {
        val intent = Intent(view.context, ActivityPlaylist::class.java)

        val playables = data.map { PlayableYouTube.get(it) }
        val pl = Playlist.get("${PlayableSource.YOUTUBE.id}-$id", author, label, 512, playables)

        intent.putExtra(Constants.EXTRA_PLAYLIST, pl.id)
        startActivity(intent)
    }

    companion object {
        fun create(playlist: PlaylistYouTube): FragmentTileYouTube {
            val fragment = FragmentTileYouTube()

            val args = Bundle()
            args.putBoolean(Constants.EXTRA_TILES, false)
            args.putString(Constants.EXTRA_BACKGROUND, playlist.cover)
            args.putString(Constants.EXTRA_LABEL, playlist.label)
            args.putString(Constants.EXTRA_LABEL_DESCRIPTION, playlist.author)
            args.putString(Constants.EXTRA_PLAYLIST, playlist.path)
            args.putStringArrayList(Constants.EXTRA_DATA, playlist.playableIDs as ArrayList<String>)
            fragment.arguments = args
            return fragment
        }
    }
}