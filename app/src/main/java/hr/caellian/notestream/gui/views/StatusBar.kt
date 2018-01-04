package hr.caellian.notestream.gui.views

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.RepeatState

/**
 * Created by caellyan on 23/06/17.
 */

class StatusBar(context: Context, packageName: String) : RemoteViews(packageName, R.layout.status_bar), Playable.ControlListener {

    init {

        val playerIntent = Intent(context, ActivityPlayer::class.java)
        playerIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        val playerPending = PendingIntent.getActivity(context, Constants.APP_REQUEST_CODE, playerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(R.id.buttonShowPlayer, playerPending)

        var serviceIntent = Intent(context, PlayerService::class.java)
        serviceIntent.action = Constants.ACTION_SWITCH_PREVIOUS
        var servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(R.id.buttonPrevious, servicePending)

        serviceIntent = Intent(context, PlayerService::class.java)
        serviceIntent.action = Constants.ACTION_SWITCH_NEXT
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(R.id.buttonNext, servicePending)

        serviceIntent = Intent(context, PlayerService::class.java)
        serviceIntent.action = Constants.ACTION_TOGGLE_PLAY
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(R.id.buttonTogglePlay, servicePending)

        serviceIntent = Intent(context, PlayerService::class.java)
        serviceIntent.action = Constants.ACTION_STOP
        servicePending = PendingIntent.getService(context, Constants.APP_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(R.id.buttonDismiss, servicePending)
    }

    override fun onPlayableChanged(current: Playable?) {
        setTextViewText(R.id.labelSongTitle, current?.info?.title)
        setTextViewText(R.id.labelSongAuthor, current?.info?.author)
    }

    override fun onPlayStatusChanged(playing: Boolean) {
        if (playing) {
            setInt(R.id.buttonTogglePlay, "setBackgroundResource", R.drawable.ic_pause_circle)
        } else {
            setInt(R.id.buttonTogglePlay, "setBackgroundResource", R.drawable.ic_play_circle)
        }
    }

    override fun onShuffleStateChanged(currentState: Boolean) {

    }

    override fun onRepeatStateChanged(currentState: RepeatState) {

    }
}