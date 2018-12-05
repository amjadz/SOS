package edu.uw.acevedoj.sos

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.CountDownTimer
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log


class AlarmService : Service() {
    private val TAG = "AlarmService"

    private val NOTIFICATION_CHANNEL_ID = "alarm_channel_01" //channel ID
    private val NOTIFICATION_CHANNEL_ID_2 = "alarm_channel_02" //channel ID
    private val NOTIFICATION_ID = 1
    private val timers = ArrayList<CountDownTimer>()


    //run timer based on given input
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "Timer Start")

        val alarms =  intent?.getStringArrayListExtra("alarms")

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("alarm_tab", "1")
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0,
        intent, PendingIntent.FLAG_UPDATE_CURRENT)

       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "alarm channel", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }*/

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setAutoCancel(true)
            .setContentTitle("Alarm service")
            .setContentText("Number of emergency alarm in background: ${alarms!!.size}")
            .setContentIntent(pendingIntent)
            .setOngoing(true) //cannot be dismissed by the user
            .setAutoCancel(true)
            .build()


        startForeground(NOTIFICATION_ID, notification) //make this a foreground service!


        if (alarms != null) {
            for (alarm in alarms) {
                val millis = alarm.toLong() - System.currentTimeMillis()
                if (millis >= 0) {
                    val timer = object : CountDownTimer(millis, 1000) {

                        override fun onTick(millisUntilFinished: Long) {
                            Log.v(TAG, "millis count down: ${millisUntilFinished / 1000}")

                            val secondsLeft = millisUntilFinished / 1000

                            if (secondsLeft == 300.toLong() || secondsLeft == 180.toLong() || secondsLeft == 60.toLong()) {
                                val CHANNEL_ID_TIMER = "menu_timer"
                                val CHANNEL_NAME_TIMER = "Timer App Timer"
                                val TIMER_ID = 0

                                val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                var mBuilder = NotificationCompat.Builder(this@AlarmService!!, NOTIFICATION_CHANNEL_ID_2)
                                    .setSmallIcon(R.drawable.ic_timer)
                                    .setAutoCancel(true)
                                    .setContentTitle("Please check in!")
                                    .setContentText("SOS to your emergency contact within ${secondsLeft/60} minutes")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)

                                // .setSound(notificationSound)
                                    .setAutoCancel(true)
                                   // .setDefaults(0)
                                val nManager = this@AlarmService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                                nManager.createNotificationChannel(CHANNEL_NAME_TIMER, CHANNEL_ID_TIMER)

                                nManager.notify(TIMER_ID, mBuilder.build())

                            }

                        }

                        override fun onFinish() {
                            SMSFragment.sendText(this@AlarmService)
                            SMSFragment.makeCall(this@AlarmService)
                        }
                    }.start()
                    timers.add(timer)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (timer in timers) {
            timer.cancel()
        }
    }

    //create notification channel based on given input
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

    inner class LocalBinder: Binder() {
        fun getService() : AlarmService {
            return this@AlarmService
        }

    }

    private val aLocalBinder = LocalBinder()


    override fun onBind(intent: Intent): IBinder {
        return aLocalBinder
    }
}
