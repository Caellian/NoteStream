package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.gui.ActivityPlaylist;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.Util;

/**
 * Created by caellyan on 26/06/17.
 */

public class FragmentCategoryTile extends Fragment {
    View view;

    public static FragmentCategoryTile newInstance(boolean tiledDisplay, int iconID, String label, Playlist playlist) {
        FragmentCategoryTile fragment = new FragmentCategoryTile();

        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_TILES, tiledDisplay);
        args.putInt(Constants.EXTRA_ICON, iconID);
        args.putString(Constants.EXTRA_LABEL, label);
        args.putString(Constants.EXTRA_PLAYLIST, playlist.getID());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_tile, container, false);

        final boolean useTiledDisplay = getArguments().getBoolean(Constants.EXTRA_TILES);
        final String playlist = getArguments().getString(Constants.EXTRA_PLAYLIST);

        final Bitmap[] background = Playlist.get(playlist).getCoverBitmaps();

        final Bitmap icon = Util.drawableToBitmap(getArguments().getInt(Constants.EXTRA_ICON), 256, 256);
        final String label = getArguments().getString(Constants.EXTRA_LABEL);

        if (background[0] != null && !useTiledDisplay) {
            ((ImageView) view.findViewById(R.id.imageTileBackground)).setImageBitmap(background[0]);
        } else if (background[0] != null) {
            ((ImageView) view.findViewById(R.id.imageTileBackgroundA)).setImageBitmap(background[0]);
            if (background[1] != null) {
                ((ImageView) view.findViewById(R.id.imageTileBackgroundB)).setImageBitmap(background[1]);
                if (background[2] != null) {
                    ((ImageView) view.findViewById(R.id.imageTileBackgroundC)).setImageBitmap(background[2]);
                    if (background[3] != null) {
                        ((ImageView) view.findViewById(R.id.imageTileBackgroundD)).setImageBitmap(background[3]);
                    }
                }
            }
        }

        if (icon != null) ((ImageView) view.findViewById(R.id.imageTileIcon)).setImageBitmap(icon);

        ((TextView) view.findViewById(R.id.labelTileCategory)).setText(label);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ActivityPlaylist.class);
                intent.putExtra(Constants.EXTRA_PLAYLIST, playlist);
                startActivity(intent);
            }
        });

        return view;
    }
}
