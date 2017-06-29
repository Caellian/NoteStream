package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by caellyan on 16/06/17.
 */

public class FragmentBarPlayer extends Fragment implements Playable.ControlListener, Playable.ProgressListener{

    View view;
    PlayerService.PlayerServiceBinder psb = NoteStream.getInstance().getPlayerServiceBinder();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bar_playing, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ActivityPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        final Button buttonPrevious = (Button) view.findViewById(R.id.buttonPrevious);
        final Button buttonNext = (Button) view.findViewById(R.id.buttonNext);

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psb.switchPrevious();
            }
        });

        buttonPrevious.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Timer holdTimer = new Timer();
                holdTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (buttonPrevious.isPressed()) {
                            psb.setProgress(Math.max(psb.getProgress() - 5000, 0));
                        } else {
                            holdTimer.cancel();
                        }
                    }
                }, 1000, 500);
                return true;
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psb.switchNext();
            }
        });

        buttonNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Timer holdTimer = new Timer();
                holdTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (buttonNext.isPressed()) {
                            psb.setProgress(Math.min(psb.getProgress() + 5000,
                                    psb.getCurrentPlayable().getMetadata().getLength()));
                        } else {
                            holdTimer.cancel();
                        }
                    }
                }, 1000, 500);
                return true;
            }
        });

        view.findViewById(R.id.buttonTogglePlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psb.togglePlay();
            }
        });

        if(psb == null || psb.isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            onProgressChanged(psb.getProgress());
            onPlayableChanged(psb.getCurrentPlayable());
            onPlayStatusChanged(psb.isPlaying());
        }

        NoteStream.registerControlListener(this);
        NoteStream.registerProgressListener(this);
        NoteStream.registerPlayerServiceListener(new NoteStream.PlayerServiceListener() {
            @Override
            public void onPlayerServiceConnected(PlayerService.PlayerServiceBinder psb) {
                FragmentBarPlayer.this.psb = psb;
                if (!psb.isEmpty() && psb.getCurrentPlayable() != null) {
                    FragmentBarPlayer.this.onProgressChanged(psb.getProgress());
                    FragmentBarPlayer.this.onPlayableChanged(psb.getCurrentPlayable());
                    FragmentBarPlayer.this.onPlayStatusChanged(psb.isPlaying());
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerServiceDisconnected() {
                view.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onProgressChanged(int progress) {
        ((ProgressBar) view.findViewById(R.id.songProgressBar)).setProgress(progress);
    }

    @Override
    public void onPlayableChanged(@NonNull Playable current) {
        ((TextView) view.findViewById(R.id.labelSongTitle)).setText(current.getMetadata().getTitle());
        ((TextView) view.findViewById(R.id.labelSongAuthor)).setText(current.getMetadata().getAuthor());
        ((ProgressBar) view.findViewById(R.id.songProgressBar)).setMax(current.getMetadata().getLength());
        onProgressChanged(0);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayStatusChanged(boolean playing) {
        if (!playing) {
            view.findViewById(R.id.buttonTogglePlay).setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_play_circle));
        } else {
            view.findViewById(R.id.buttonTogglePlay).setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_pause_circle));
        }
    }

    @Override
    public void onShuffleStateChanged(boolean currentState) {

    }

    @Override
    public void onRepeatStateChanged(RepeatState currentState) {

    }
}
