package edu.uw.acevedoj.sos

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.CountDownTimer
import android.util.Log


class AlarmService : Service() {
    private val TAG = "AlarmService"

    private var text = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "Timer Start")
        object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                text = "seconds remaining: " + millisUntilFinished / 1000
            }

            override fun onFinish() {
                text = "done!"
            }
        }.start()

        return super.onStartCommand(intent, flags, startId)
    }

    inner class LocalBinder: Binder() {
        fun getService() : AlarmService {
            return this@AlarmService
        }

        fun getText():String {
            Log.v(TAG, "get text: ${text}")
            return text
        }
    }

    private val aLocalBinder = LocalBinder()


    override fun onBind(intent: Intent): IBinder {
        return aLocalBinder
    }
}
