package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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

    PlayerService.PlayerServiceBinder psb = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bar_playing, container, false);

        view.findViewById(R.id.buttonShowPlayer).setOnClickListener(new View.OnClickListener() {
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
                            psb.setProgress(Math.max(psb.getCurrentPosition() - 5000, 0));
                        } else {
                            holdTimer.cancel();
                        }
                    }
                }, 1000, 1000);
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
                            psb.setProgress(Math.min(psb.getCurrentPosition() + 5000,
                                    psb.getCurrentPlayable().getMetadata().getLength()));
                        } else {
                            holdTimer.cancel();
                        }
                    }
                }, 1000, 1000);
                return true;
            }
        });

        view.findViewById(R.id.buttonTogglePlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psb.isPlaying()) {
                    psb.pause();
                } else {
                    psb.play();
                }
            }
        });

        if(psb == null || psb.isEmpty()) {
            view.setVisibility(View.GONE);
        }

        NoteStream.registerControlListener(this);
        NoteStream.registerProgressListener(this);

        final Timer bindTimer = new Timer();
        bindTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (NoteStream.getInstance().getPlayerServiceBinder() != null) {
                    psb = NoteStream.getInstance().getPlayerServiceBinder();
                    bindTimer.cancel();
                }
            }
        }, 1000, 1000);
        return view;
    }

    @Override
    public void onProgressChanged(int progress) {
        ((ProgressBar) view.findViewById(R.id.songProgressBar)).setProgress(progress);
    }

    @Override
    public void onPlayableChanged(Playable current) {
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