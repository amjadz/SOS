package edu.uw.acevedoj.sos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import edu.uw.acevedoj.sos.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val CHANNEL_ID_TIMER = "menu_timer"
        val CHANNEL_NAME_TIMER = "Timer App Timer"
        val TIMER_ID = 0

        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var mBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID_TIMER)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Timer Expired!")
            .setContentText("Sending SOS to your emergency contact")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(notificationSound)
            .setAutoCancel(true)
            .setDefaults(0)
        val nManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_NAME_TIMER, CHANNEL_ID_TIMER)
        nManager.notify(TIMER_ID, mBuilder.build())
        // TODO: Send Intent to Emergency Contact

        PrefUtil.setTimerState(OtherFragment.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }

    private fun NotificationManager.createNotificationChannel(ChannelName: String, ChannelID: String){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(ChannelID, ChannelName, importance)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            this.createNotificationChannel(channel)
        }
    }
}
