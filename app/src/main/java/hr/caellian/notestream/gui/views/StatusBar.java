package hr.caellian.notestream.gui.views;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by caellyan on 23/06/17.
 */

public class StatusBar extends RemoteViews implements Playable.ControlListener {

    public StatusBar(Context context, String packageName) {
        super(packageName, R.layout.status_bar);

        Intent playerIntent = new Intent(context, ActivityPlayer.class);
        playerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent playerPending = PendingIntent.getActivity(context, Constants.APP_REQUEST_CODE, playerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonShowPlayer, playerPending);

        Intent serviceIntent = new Intent(context, PlayerService.class);
        serviceIntent.setAction(Constants.ACTION_SWITCH_PREVIOUS);
        PendingIntent servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonPrevious, servicePending);

        serviceIntent = new Intent(context, PlayerService.class);
        serviceIntent.setAction(Constants.ACTION_SWITCH_NEXT);
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonNext, servicePending);

        serviceIntent = new Intent(context, PlayerService.class);
        serviceIntent.setAction(Constants.ACTION_TOGGLE_PLAY);
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonTogglePlay, servicePending);
    }

    @Override
    public void onPlayableChanged(Playable current) {
        setTextViewText(R.id.labelSongTitle, current.getMetadata().getTitle());
        setTextViewText(R.id.labelSongAuthor, current.getMetadata().getAuthor());
    }

    @Override
    public void onPlayStatusChanged(boolean playing) {
        if (playing) {
            setInt(R.id.buttonTogglePlay, "setBackgroundResource", R.drawable.ic_pause_circle);
        } else {
            setInt(R.id.buttonTogglePlay, "setBackgroundResource", R.drawable.ic_play_circle);
        }
    }

    @Override
    public void onShuffleStateChanged(boolean currentState) {

    }

    @Override
    public void onRepeatStateChanged(RepeatState currentState) {

    }
}
