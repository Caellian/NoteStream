package hr.caellian.notestream.gui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.PlayableRemote;
import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.gui.PlayablePopupMenu;
import hr.caellian.notestream.lib.Constants;

/**
 * Created by caellyan on 16/06/17.
 */

public class FragmentItemPlayable extends FragmentPlayableMediator {

    public static FragmentItemPlayable newInstance(Playable playable, Playlist playlist) {
        FragmentItemPlayable fragment = new FragmentItemPlayable();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_PLAYLIST, playlist.getID());
        args.putString(Constants.EXTRA_PLAYABLE, playable.getID());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.playable_item, container, false);
        defaultTextColor = ((TextView) view.findViewById(R.id.labelSongTitle)).getCurrentTextColor();

        Playable playable = getPlayable();

        if (playable instanceof PlayableRemote && !((PlayableRemote) playable).isAvailable()) {
            NoteStream.registerAvailabilityListener(this);
            view.setVisibility(View.GONE);
        } else {
            updateView();
        }

        return view;
    }

    protected void updateView() {
        final Playlist playlist = getPlaylist();
        final Playable playable = getPlayable();

        if (playable == null) {
            return;
        }

        ((TextView) view.findViewById(R.id.labelSongTitle)).setText(playable.getMetadata().getTitle());
        ((TextView) view.findViewById(R.id.labelSongAuthor)).setText(playable.getMetadata().getAuthor());

        view.findViewById(R.id.labelSongTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playable instanceof PlayableRemote && !((PlayableRemote) playable).isAvailable()) {
                    return;
                }

                PlayerService.PlayerServiceBinder psb = NoteStream.getInstance().getPlayerServiceBinder();
                if (psb.getCurrentPlayable() != playable) {
                    psb.playAt(playlist, playable);
                } else {
                    psb.setPlaylist(playlist);
                }

                Intent intent = new Intent(view.getContext(), ActivityPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(Constants.EXTRA_PLAYLIST, playlist.getID());
                intent.putExtra(Constants.EXTRA_PLAYABLE, playable.getID());
                startActivity(intent);
            }
        });

        view.findViewById(R.id.buttonSongOptions).setOnClickListener(new View.OnClickListener() {
            PlayablePopupMenu playablePopupMenu = new PlayablePopupMenu(view.getContext(), view, playable);
            @Override
            public void onClick(View v) {
                playablePopupMenu.show();
            }
        });

        NoteStream.registerControlListener(this);
        NoteStream.registerLibraryListener(this);
    }

    public boolean filter(String filter) {
        final Playable playable = getPlayable();
        if (playable == null) {
            view.setVisibility(View.GONE);
            return false;
        }
        if (!playable.getMetadata().getTitle().toLowerCase().contains(filter.toLowerCase()) &&
                !playable.getMetadata().getAuthor().toLowerCase().contains(filter.toLowerCase())) {
            view.setVisibility(View.GONE);
            return false;
        }
        view.setVisibility(View.VISIBLE);
        return true;
    }
}
