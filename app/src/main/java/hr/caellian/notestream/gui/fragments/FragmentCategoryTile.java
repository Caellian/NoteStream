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

/**
 * Created by caellyan on 26/06/17.
 */

public class FragmentCategoryTile extends Fragment {

    Bitmap background;
    Bitmap icon;
    String label;
    Playlist playlist;

    View view;

    public static FragmentCategoryTile newInstance(Bitmap background, Bitmap icon, String label, Playlist playlist) {
        FragmentCategoryTile fragment = new FragmentCategoryTile();
        fragment.background = background;
        fragment.icon = icon;
        fragment.label = label;
        fragment.playlist = playlist;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_tile, container, false);

        if (background != null) ((ImageView) view.findViewById(R.id.imageTileBackground)).setImageBitmap(background);
        if (icon != null) ((ImageView) view.findViewById(R.id.imageTileIcon)).setImageBitmap(icon);

        ((TextView) view.findViewById(R.id.labelTileCategory)).setText(label);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ActivityPlayer.class);
                intent.putExtra("playlist", playlist);
                startActivity(intent);
            }
        });

        return view;
    }
}
