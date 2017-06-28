package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.data.playable.Playable;

/**
 * Created by caellyan on 21/06/17.
 */

public class FragmentPlayableTile extends Fragment implements Library.LibraryListener{

    public Playlist playlist;
    public Playable playable;

    View view;

    public static FragmentPlayableTile newInstance(Playable playable, Playlist playlist) {
        FragmentPlayableTile fragment = new FragmentPlayableTile();
        fragment.playlist = playlist;
        fragment.playable = playable;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.playable_tile, container, false);
        if (playable != null) {

            // TODO: Change background properly!

            ((TextView) view.findViewById(R.id.labelSongTitle)).setText(playable.getMetadata().getTitle());
            ((TextView) view.findViewById(R.id.labelSongAuthor)).setText(playable.getMetadata().getAuthor());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            NoteStream.getInstance().library.registerLibraryListener(this);
        }

        return view;
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
