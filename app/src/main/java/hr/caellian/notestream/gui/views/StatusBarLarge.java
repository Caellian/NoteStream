package hr.caellian.notestream.gui.views;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import hr.caellian.notestream.R;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.gui.ActivityPlayer;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by caellyan on 24/06/17.
 */

public class StatusBarLarge extends RemoteViews implements Playable.ControlListener {

    public StatusBarLarge(Context context, String packageName) {
        super(packageName, R.layout.status_bar_expanded);

        Intent playerIntent = new Intent(context, ActivityPlayer.class);
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

        serviceIntent = new Intent(context, PlayerService.class);
        serviceIntent.setAction(Constants.ACTION_TOGGLE_SHUFFLE);
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonShuffle, servicePending);

        serviceIntent = new Intent(context, PlayerService.class);
        serviceIntent.setAction(Constants.ACTION_TOGGLE_REPEAT);
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonRepeat, servicePending);

        serviceIntent = new Intent(context, PlayerService.class);
        serviceIntent.setAction(Constants.ACTION_STOP);
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.buttonDismiss, servicePending);
    }

    @Override
    public void onPlayableChanged(Playable current) {
        setImageViewBitmap(R.id.albumImage, current.getMetadata().getCover());
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
        if (currentState) {
            setInt(R.id.buttonShuffle, "setBackgroundResource", R.drawable.ic_shuffle_on);
        } else {
            setInt(R.id.buttonShuffle, "setBackgroundResource", R.drawable.ic_shuffle);
        }
    }

    @Override
    public void onRepeatStateChanged(RepeatState currentState) {
        switch (currentState) {
            case NONE:
                setInt(R.id.buttonRepeat, "setBackgroundResource", R.drawable.ic_repeat);
                break;
            case ALL:
                setInt(R.id.buttonRepeat, "setBackgroundResource", R.drawable.ic_repeat_on);
                break;
            case ONE:
                setInt(R.id.buttonRepeat, "setBackgroundResource", R.drawable.ic_repeat_one);
                break;
        }
    }
}
