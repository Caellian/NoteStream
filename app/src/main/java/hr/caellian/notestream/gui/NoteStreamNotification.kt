package hr.caellian.notestream.gui

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.gui.views.StatusBar
import hr.caellian.notestream.gui.views.StatusBarLarge
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.RepeatState

/**
 * Created by caellyan on 24/06/17.
 */

class NoteStreamNotification(internal var context: Context, internal var parentService: Service) : Playable.ControlListener {

    var notification: Notification? = null
        private set

    internal var statusBar: StatusBar
    internal var statusBarLarge: StatusBarLarge

    internal var playing: Boolean = false

    internal var nm: NotificationManager
    private val pendingIntent: PendingIntent
    internal var builder: Notification.Builder

    init {
        val packageName = context.packageName
        this.nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        statusBar = StatusBar(context, packageName)
        statusBarLarge = StatusBarLarge(context, packageName)

        val nsIntent = Intent(context, ActivityLibrary::class.java)
        pendingIntent = PendingIntent.getActivity(context, Constants.APP_NOTIFICATION_ID, nsIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = Notification.Builder(context)
        builder.setContentTitle(context.getString(R.string.app_name))

        builder.setCustomContentView(statusBar)
        builder.setCustomBigContentView(statusBarLarge)

        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.ic_service_icon)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_service_icon))

        NoteStream.registerControlListener(this)
    }

    fun rebuild(): NoteStreamNotification {
        notification = builder.build()
        parentService.startForeground(Constants.APP_NOTIFICATION_ID, notification)
        return this
    }

    fun cancelNotification() {
        NoteStream.CONTROL_LISTENERS.remove(this)
        parentService.stopForeground(true)
    }

    override fun onPlayableChanged(current: Playable?) {
        statusBar.onPlayableChanged(current)
        statusBarLarge.onPlayableChanged(current)
        rebuild()
    }

    override fun onPlayStatusChanged(playing: Boolean) {
        this.playing = playing
        statusBar.onPlayStatusChanged(playing)
        statusBarLarge.onPlayStatusChanged(playing)
        rebuild()
    }

    override fun onShuffleStateChanged(currentState: Boolean) {
        statusBarLarge.onShuffleStateChanged(currentState)
        rebuild()
    }

    override fun onRepeatStateChanged(currentState: RepeatState) {
        statusBarLarge.onRepeatStateChanged(currentState)
        rebuild()
    }
}
