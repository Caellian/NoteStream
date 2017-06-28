package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by caellyan on 16/06/17.
 */

public class FragmentItemPlayable extends Fragment implements Playable.ControlListener, Library.LibraryListener {

    View view;
    private int defaultColor;

    public Playlist playlist;
    public Playable playable;

    public static FragmentItemPlayable newInstance(Playable playable, Playlist playlist) {
        FragmentItemPlayable fragment = new FragmentItemPlayable();
        fragment.playlist = playlist;
        fragment.playable = playable;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.playable_item, container, false);

        ((TextView) view.findViewById(R.id.songTitle)).setText(playable.getMetadata().getTitle());
        ((TextView) view.findViewById(R.id.songAuthor)).setText(playable.getMetadata().getAuthor());

        view.findViewById(R.id.songTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Don't do anything if not available.
                PlayerService.PlayerServiceBinder psb = NoteStream.getInstance().getPlayerServiceBinder();
                if (psb.getCurrentPlayable() != playable) {
                    psb.playAt(playlist, playable);
                } else {
                    psb.setPlaylist(playlist);
                }

                Intent intent = new Intent(view.getContext(), ActivityPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        defaultColor = ((TextView) view.findViewById(R.id.songTitle)).getCurrentTextColor();

        view.findViewById(R.id.buttonSongOptions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO: Show dropdown menu...
                    - Add/Remove to/from library
                    - Show author
                    - Show album
                    (-) Download
                    - Tag music
                */
            }
        });

        NoteStream.registerControlListener(this);
        NoteStream.getInstance().library.registerLibraryListener(this);
        return view;
    }

    public boolean filter(String filter) {
        if (!playable.getMetadata().getTitle().toLowerCase().contains(filter.toLowerCase()) &&
                !playable.getMetadata().getAuthor().toLowerCase().contains(filter.toLowerCase())) {
            view.setVisibility(View.GONE);
            return false;
        }
        view.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onPlayableChanged(Playable current) {
        if (current == playable) {
            ((TextView) view.findViewById(R.id.songTitle)).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        } else {
            ((TextView) view.findViewById(R.id.songTitle)).setTextColor(defaultColor);
        }
    }

    @Override
    public void onPlayStatusChanged(boolean playing) {

    }

    @Override
    public void onShuffleStateChanged(boolean currentState) {

    }

    @Override
    public void onRepeatStateChanged(RepeatState currentState) {

    }

    @Override
    public void onPlayableAddedToPlaylist(Playable playable, Playlist playlist) {
        if (playable == this.playable && playlist == this.playlist) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayableRemovedFromPlaylist(Playable playable, Playlist playlist) {
        if (playable == this.playable && playlist == this.playlist) {
            view.setVisibility(View.GONE);
        }
    }
}
