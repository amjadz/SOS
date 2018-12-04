package edu.uw.acevedoj.sos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import edu.uw.acevedoj.sos.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: show notification

        PrefUtil.setTimerState(OtherFragment.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
