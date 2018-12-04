package edu.uw.acevedoj.sos

import android.app.ActionBar
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.uw.acevedoj.sos.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_other.*
import java.util.*


class OtherFragment: Fragment() {

    companion object {
        fun setAlarm(context: Context?, nowSeconds: Long, secondsRemaining:Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds,context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context?){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_other, null)

        // On Click Listener for the start button
        root.findViewById<View>(R.id.start_btn).setOnClickListener{
            startTimer()
            timerState = TimerState.Running
            updateButtons()

        }

        // On Click Listener for Check in
        root.findViewById<View>(R.id.check_btn).setOnClickListener{
             //reset Timer startTimer()?
            timer.cancel()
           restartTimer()

        }
        root.findViewById<View>(R.id.stop_btn).setOnClickListener {
            timer.cancel()
            onTimerFinished()
        }
        return root
    }

    override fun onResume() {
        super.onResume()

        initTimer()
        removeAlarm(this.context)
        //TODO: hide notification
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this.context, nowSeconds, secondsRemaining)
            // TODO: show notification
        }
        else if(timerState == TimerState.Paused){
            // TODO: Show notification
        }
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this.context)
        PrefUtil.setSecondsRemaining(secondsRemaining, this.context)
        PrefUtil.setTimerState(timerState, this.context)

    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this.context)

        if(timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this.context)
        else
            timerLengthSeconds
        val alarmSetTime = PrefUtil.getAlarmSetTIme(this.context)
        if (alarmSetTime > 0){
            secondsRemaining -= nowSeconds - alarmSetTime
        }

        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()

    }

    private fun restartTimer(){
        setNewTimerLength()
        secondsRemaining = timerLengthSeconds
        progress_countdown.progress = 0
        PrefUtil.setSecondsRemaining(timerLengthSeconds, this.context)
        startTimer()
        timerState = TimerState.Running
        updateButtons()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped
        setNewTimerLength()
        progress_countdown.progress = 0
        PrefUtil.setSecondsRemaining(timerLengthSeconds, this.context)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()

    }

    private fun startTimer(){
        timerState = TimerState.Running
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000){
            override fun onFinish() = onTimerFinished()

            override fun onTick(p0: Long) {
                secondsRemaining = p0 / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this.context)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minUntilFinish = secondsRemaining/60
        val secondsInMinUntilFinsih = secondsRemaining - (minUntilFinish * 60)
        val secondStr = secondsInMinUntilFinsih.toString()
        countdown_view.text = "$minUntilFinish:${if (secondStr.length == 2) secondStr
        else "0" + secondStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when (timerState){
            TimerState.Running ->{
                start_btn.isEnabled = false
                check_btn.isEnabled = true
                stop_btn.isEnabled = true
            }
            TimerState.Stopped ->{
                start_btn.isEnabled = true
                check_btn.isEnabled = false
                stop_btn.isEnabled = false
            }
            TimerState.Paused ->{
                start_btn.isEnabled = true
                check_btn.isEnabled = false
                stop_btn.isEnabled = true
            }
        }
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this.context)
        progress_countdown.max= timerLengthSeconds.toInt()
    }

}