package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.playable.PlayableRemote;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by tinsv on 30/06/2017.
 */

public abstract class FragmentPlayableMediator extends Fragment implements Playable.ControlListener, Library.LibraryListener, PlayableRemote.AvailabilityListener {
    protected View view;
    protected int defaultTextColor;

    public Playlist getPlaylist() {
        return Playlist.get(getArguments().getString(Constants.EXTRA_PLAYLIST));
    }

    public Playable getPlayable() {
        Playlist playlist = Playlist.get(getArguments().getString(Constants.EXTRA_PLAYLIST));
        return playlist.getPlayable(getArguments().getString(Constants.EXTRA_PLAYABLE));
    }

    protected abstract void updateView();

    @Override
    public void onPlayableChanged(Playable current) {
        final Playable playable = getPlayable();
        if (current == playable) {
            ((TextView) view.findViewById(R.id.labelSongTitle)).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        } else {
            ((TextView) view.findViewById(R.id.labelSongTitle)).setTextColor(defaultTextColor);
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
    public void onPlayableAddedToPlaylist(Playable argPlayable, Playlist argPlaylist) {
        final Playlist playlist = getPlaylist();
        final Playable playable = getPlayable();

        if (argPlayable == playable && argPlaylist == playlist) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayableRemovedFromPlaylist(Playable argPlayable, Playlist argPlaylist) {
        final Playlist playlist = getPlaylist();
        final Playable playable = getPlayable();

        if (argPlayable == playable && argPlaylist == playlist) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAvailableStateChanged(PlayableRemote argPlayable, boolean state) {
        final Playable playable = getPlayable();
        if (argPlayable == playable) {
            if (state) {
                view.setVisibility(View.VISIBLE);
                updateView();
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }
}
