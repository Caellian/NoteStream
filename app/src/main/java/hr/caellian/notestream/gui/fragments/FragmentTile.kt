package hr.caellian.notestream.gui.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import hr.caellian.notestream.R
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.Util

/**
 * Created by caellian on 08/03/18.
 */
abstract class FragmentTile : Fragment() {

    abstract fun onClickListener(data: String)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_tile, container, false)

        val useTiledDisplay = arguments.getBoolean(Constants.EXTRA_TILES, false)
        val data = arguments.getString(Constants.EXTRA_DATA)!!

        val background = Playlist.get(data).coverBitmaps

        val iconRes = arguments.getInt(Constants.EXTRA_ICON, 0)
        val label = arguments.getString(Constants.EXTRA_LABEL, "")
        val labelDescription = arguments.getString(Constants.EXTRA_LABEL_DESCRIPTION, "")

        if (background.isNotEmpty() && !useTiledDisplay) {
            view.findViewById<ImageView>(R.id.imageTileBackground)?.setImageBitmap(background[0])
        } else if (background.isNotEmpty()) {
            view.findViewById<ImageView>(R.id.imageTileBackgroundA)?.setImageBitmap(background[0])
            if (background.size > 1) {
                view.findViewById<ImageView>(R.id.imageTileBackgroundB)?.setImageBitmap(background[1])
                if (background.size > 2) {
                    view.findViewById<ImageView>(R.id.imageTileBackgroundC)?.setImageBitmap(background[2])
                    if (background.size > 3) {
                        view.findViewById<ImageView>(R.id.imageTileBackgroundD)?.setImageBitmap(background[3])
                    }
                }
            }
        }

        if (iconRes != 0) {
            val icon = Util.drawableToBitmap(arguments.getInt(Constants.EXTRA_ICON), 256, 256)
            view.findViewById<ImageView>(R.id.imageTileIcon)?.setImageBitmap(icon)
        }

        view.findViewById<TextView>(R.id.labelTileTitle)?.text = label
        view.findViewById<TextView>(R.id.labelTileDescription)?.text = labelDescription

        view.setOnClickListener {
            onClickListener(data)
        }

        return view
    }
}