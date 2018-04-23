/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.gui

import android.app.*
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.gui.views.StatusBar
import hr.caellian.notestream.gui.views.StatusBarLarge
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.RepeatState

class Notification(internal var context: Context, internal var parentService: Service) : Playable.ControlListener {

    var notification: Notification? = null
        private set

    internal var statusBar: StatusBar
    internal var statusBarLarge: StatusBarLarge

    internal var playing: Boolean = false

    internal var nm: NotificationManager
    private val pendingIntent: PendingIntent
    internal var builder: Notification.Builder

    init {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else {
            "notestream_service"
        }

        val packageName = context.packageName
        this.nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        statusBar = StatusBar(context, packageName)
        statusBarLarge = StatusBarLarge(context, packageName)

        val nsIntent = Intent(context, ActivityLibrary::class.java)
        pendingIntent = PendingIntent.getActivity(context, Constants.APP_NOTIFICATION_ID, nsIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        builder.setContentTitle(context.getString(R.string.app_name))

        builder.setCustomContentView(statusBar)
        builder.setCustomBigContentView(statusBarLarge)

        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.ic_play_arrow)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_play_arrow))

        NoteStream.registerControlListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "notestream_service"
        val channelName = "NoteStream Service"
        val channel = NotificationChannel (channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = NoteStream.instance?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    fun rebuild(): hr.caellian.notestream.gui.Notification {
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
