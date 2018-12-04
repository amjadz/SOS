package edu.uw.acevedoj.sos

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.telephony.SmsManager
import android.view.View


class TimerExpiredReceiver : BroadcastReceiver() {

    val SENT: String = "SMS_Sent"
    val DELIVERED: String = "SMS_DELIVERED"

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pIntent = PendingIntent.getActivity(context, 0, i, 0)

        val b = NotificationCompat.Builder(context)
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        b.setSound(notification)
            .setContentTitle(context.getString(R.string.timer_finished))
            .setAutoCancel(true)
            .setContentText(context.getString(R.string.timer_finished_text))
            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
            .setContentIntent(pIntent)

        val n = b.build()
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, n)



        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val sms = SmsManager.getDefault()
        val textPreferences = prefs.getString("text_preference", "1")
        val primaryContact = prefs.getString("contact_text_1", "911")
        val msgContent = prefs.getString("text_message_content", "I need help!")
        sms.sendTextMessage(primaryContact, null, msgContent, null, null)
//        if (textPreferences == "2") {
//            for (i in 2..3) {
//                val contactNumber = prefs.getString("contact_text_$i", " ")
//                if (contactNumber != " ") {
//                    sms.sendTextMessage(contactNumber, null, msgContent, null, null)
//                }
//            }
//        }
    }
}