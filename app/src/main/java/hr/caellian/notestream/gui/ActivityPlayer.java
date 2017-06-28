package hr.caellian.notestream.gui;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.PlayableDownloadable;
import hr.caellian.notestream.util.RepeatState;

import static hr.caellian.notestream.util.Util.timeToString;

public class ActivityPlayer extends NavigationActivity implements NavigationView.OnNavigationItemSelectedListener, Playable.ControlListener, Playable.ProgressListener {
    // http://stackoverflow.com/questions/9481977/android-seekbar-to-control-mediaplayer-progress
    PlayerService.PlayerServiceBinder psb = null;

    NavigationView suggestionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        psb = NoteStream.getInstance().getPlayerServiceBinder();

        findViewById(R.id.labelSongTitle).setSelected(true);
        findViewById(R.id.labelSongAuthor).setSelected(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        suggestionsView = (NavigationView) findViewById(R.id.suggestions_view);
        suggestionsView.setNavigationItemSelectedListener(this);

        findViewById(R.id.buttonDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (psb.getCurrentPlayable() instanceof PlayableDownloadable) {
                    ((PlayableDownloadable) psb.getCurrentPlayable()).download();
                }
            }
        });

        final Button saveButton = (Button) findViewById(R.id.buttonLibraryAdd);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NoteStream.getInstance().library.isSaved(psb.getCurrentPlayable())) {
                    NoteStream.getInstance().library.removePlayable(psb.getCurrentPlayable());
                    saveButton.setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_add));
                } else {
                    NoteStream.getInstance().library.savePlayable(psb.getCurrentPlayable());
                    saveButton.setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_check));
                }
            }
        });

        ((SeekBar) findViewById(R.id.songProgressBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    psb.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        findViewById(R.id.buttonShuffle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psb.setShuffle(!psb.doShuffle());
            }
        });

        final Button buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
        final Button buttonNext = (Button) findViewById(R.id.buttonNext);

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

        findViewById(R.id.buttonTogglePlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psb.isPlaying()) {
                    psb.pause();
                } else {
                    psb.play();
                }
            }
        });

        findViewById(R.id.buttonRepeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psb.toggleRepeat();
            }
        });

        NoteStream.registerControlListener(this);
        NoteStream.registerProgressListener(this);

        onProgressChanged(0);

        if (psb != null) {
            onShuffleStateChanged(psb.doShuffle());
            onPlayStatusChanged(psb.isPlaying());
            onRepeatStateChanged(psb.getRepeatState());
            onPlayableChanged(psb.getCurrentPlayable());
        }
    }

    public void onProgressChanged(int progress){
        TextView source = (TextView) findViewById(R.id.labelCurrentTime);
        source.setText(timeToString(progress));
        ((SeekBar) findViewById(R.id.songProgressBar)).setProgress(progress);
    }

    public void onPlayableChanged(Playable current) {
        ((ImageView) findViewById(R.id.albumImage)).setImageBitmap(current.getMetadata().getTrackCover());

        TextView source = (TextView) findViewById(R.id.labelSource);
        source.setText(current.getLocation());

        if (current instanceof PlayableDownloadable) {
            findViewById(R.id.buttonDownload).setVisibility(View.VISIBLE);
            suggestionsView.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.buttonDownload).setVisibility(View.INVISIBLE);
            suggestionsView.setVisibility(View.GONE);
        }

        final Button saveButton = (Button) findViewById(R.id.buttonLibraryAdd);
        if (NoteStream.getInstance().library.isSaved(psb.getCurrentPlayable())) {
            saveButton.setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_check));
        } else {
            saveButton.setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_add));
        }

        TextView title = (TextView) findViewById(R.id.labelSongTitle);
        title.setText(current.getMetadata().getTitle());
        TextView author = (TextView) findViewById(R.id.labelSongAuthor);
        author.setText(current.getMetadata().getAuthor());

        //TODO: Add options menu

        ((SeekBar) findViewById(R.id.songProgressBar)).setMax(current.getMetadata().getLength());

        TextView length = (TextView) findViewById(R.id.labelLength);
        length.setText(timeToString(current.getMetadata().getLength()));
    }

    @Override
    public void onPlayStatusChanged(boolean playing) {
        if (!playing) {
            findViewById(R.id.buttonTogglePlay).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_play_circle));
        } else {
            findViewById(R.id.buttonTogglePlay).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_pause_circle));
        }
    }

    @Override
    public void onShuffleStateChanged(boolean currentState) {
        if(currentState) {
            findViewById(R.id.buttonShuffle).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_shuffle_on));
        } else {
            findViewById(R.id.buttonShuffle).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_shuffle));
        }
    }

    @Override
    public void onRepeatStateChanged(RepeatState currentState) {
        switch (currentState) {
            case NONE:
                findViewById(R.id.buttonRepeat).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_repeat));
                break;
            case ALL:
                findViewById(R.id.buttonRepeat).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_repeat_on));
                break;
            case ONE:
                findViewById(R.id.buttonRepeat).setBackground(ContextCompat.getDrawable(ActivityPlayer.this, R.drawable.ic_repeat_one));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.player_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationItemSelected(item);
        // TODO: Suggestions for online content.

        getDrawerLayout().closeDrawer(GravityCompat.END);
        return false;
    }

    @Override
    protected DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.player_layout);
    }
}