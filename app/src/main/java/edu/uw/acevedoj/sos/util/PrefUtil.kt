package edu.uw.acevedoj.sos.util

import android.content.Context
import android.preference.PreferenceManager
import edu.uw.acevedoj.sos.OtherFragment
import edu.uw.acevedoj.sos.TimerExpiredReceiver
import java.security.AccessControlContext

class PrefUtil{
    companion object {
        fun getTimerLength(context: Context?): Int{
            //placeholder
            return 1
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "edu.uw.timer.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context?): Long{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,  0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "edu.uw.timer.timer_state"

        fun getTimerState(context: Context?): OtherFragment.TimerState{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preference.getInt(TIMER_STATE_ID, 0)
            return OtherFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: OtherFragment.TimerState, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "edu.uw.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context?): Long{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "edu.ue.timer.background_time"

        fun getAlarmSetTIme(context: Context?) :Long{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }


    }
}