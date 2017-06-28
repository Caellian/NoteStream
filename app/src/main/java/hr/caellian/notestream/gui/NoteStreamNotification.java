package hr.caellian.notestream.gui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import hr.caellian.notestream.gui.views.StatusBar;
import hr.caellian.notestream.gui.views.StatusBarLarge;
import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by caellyan on 24/06/17.
 */

public class NoteStreamNotification implements Playable.ControlListener {

    private Notification notification;

    StatusBar statusBar;
    StatusBarLarge statusBarLarge;

    Service parentService;
    Context context;

    boolean playing;

    NotificationManager nm;
    private final PendingIntent pendingIntent;
    Notification.Builder builder;

    public NoteStreamNotification(Context context, Service parentService) {
        String packageName = context.getPackageName();

        this.parentService = parentService;
        this.context = context;
        this.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        statusBar = new StatusBar(context, packageName);
        statusBarLarge = new StatusBarLarge(context, packageName);

        Intent nsIntent = new Intent(context, ActivityLibrary.class);
        pendingIntent = PendingIntent.getActivity(context, Constants.APP_NOTIFICATION_ID, nsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(statusBar);
            builder.setCustomBigContentView(statusBarLarge);
        } else {
            builder.setContent(statusBar);
        }

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_service_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_service_icon));

        NoteStream.registerControlListener(this);
    }

    public NoteStreamNotification rebuild() {
        notification = builder.build();
        if (playing) notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        if (playing) {
            parentService.startForeground(Constants.APP_NOTIFICATION_ID, notification);
        } else {
            parentService.stopForeground(false);
            nm.notify(Constants.APP_NOTIFICATION_ID, notification);
        }
        return this;
    }

    public Notification getNotification() {
        return notification;
    }

    @Override
    public void onPlayableChanged(Playable current) {
        statusBar.onPlayableChanged(current);
        statusBarLarge.onPlayableChanged(current);
        rebuild();
    }

    @Override
    public void onPlayStatusChanged(boolean playing) {
        this.playing = playing;
        statusBar.onPlayStatusChanged(playing);
        statusBarLarge.onPlayStatusChanged(playing);
        rebuild();
    }

    @Override
    public void onShuffleStateChanged(boolean currentState) {
        statusBarLarge.onShuffleStateChanged(currentState);
        rebuild();
    }

    @Override
    public void onRepeatStateChanged(RepeatState currentState) {
        statusBarLarge.onRepeatStateChanged(currentState);
        rebuild();
    }
}
