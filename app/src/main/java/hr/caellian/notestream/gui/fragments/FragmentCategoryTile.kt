package hr.caellian.notestream.gui.fragments

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import hr.caellian.notestream.R
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.ActivityPlaylist
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.Util

/**
 * Created by caellyan on 26/06/17.
 */

class FragmentCategoryTile : Fragment() {
    internal var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.category_tile, container, false)

        val useTiledDisplay = arguments.getBoolean(Constants.EXTRA_TILES)
        val playlist = arguments.getString(Constants.EXTRA_PLAYLIST)

        val background = Playlist.get(playlist).coverBitmaps

        val icon = Util.drawableToBitmap(arguments.getInt(Constants.EXTRA_ICON), 256, 256)
        val label = arguments.getString(Constants.EXTRA_LABEL)

        if (background.isNotEmpty() && !useTiledDisplay) {
            view?.findViewById<ImageView>(R.id.imageTileBackground)?.setImageBitmap(background[0])
        } else if (background.isNotEmpty()) {
            view?.findViewById<ImageView>(R.id.imageTileBackgroundA)?.setImageBitmap(background[0])
            if (background.size > 1) {
                view?.findViewById<ImageView>(R.id.imageTileBackgroundB)?.setImageBitmap(background[1])
                if (background.size > 2) {
                    view?.findViewById<ImageView>(R.id.imageTileBackgroundC)?.setImageBitmap(background[2])
                    if (background.size > 3) {
                        view?.findViewById<ImageView>(R.id.imageTileBackgroundD)?.setImageBitmap(background[3])
                    }
                }
            }
        }

        view?.findViewById<ImageView>(R.id.imageTileIcon)?.setImageBitmap(icon)

        view?.findViewById<TextView>(R.id.labelTileCategory)?.text = label

        view?.setOnClickListener {
            val intent = Intent(view?.context, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, playlist)
            startActivity(intent)
        }

        return view
    }

    companion object {
        fun newInstance(tiledDisplay: Boolean, iconID: Int, label: String, playlist: Playlist): FragmentCategoryTile {
            val fragment = FragmentCategoryTile()

            val args = Bundle()
            args.putBoolean(Constants.EXTRA_TILES, tiledDisplay)
            args.putInt(Constants.EXTRA_ICON, iconID)
            args.putString(Constants.EXTRA_LABEL, label)
            args.putString(Constants.EXTRA_PLAYLIST, playlist.id)
            fragment.arguments = args
            return fragment
        }
    }
}
