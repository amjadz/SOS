package edu.uw.acevedoj.sos

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER = "Timer App Timer"
        private const val TIMER_ID = 0

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
            timerState = TimerState.Stopped
            onTimerFinished()
        }

        root.findViewById<View>(R.id.set_time).setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Set Timer")
            builder.setMessage("Enter the maximum number of minutes between check ins here. If you are " +
                    "unable to check in before the timer runs out, Emergency services will be called. ")
            builder.setIcon(R.drawable.ic_timer)
            val timeInput= EditText(this.context)
            timeInput.hint = "# of Min"
            timeInput.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(timeInput)
            
            builder.setPositiveButton("OK"){
                dialog, okButton ->

                dialog.dismiss()
                var inputTime = timeInput.text.toString()
                PrefUtil.setTimerLength(inputTime.toInt(), context)
            }

            builder.setNegativeButton("Cancel"){
                dialog, cancelButton ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
        return root
    }

    override fun onResume() {
        super.onResume()

        initTimer()
        removeAlarm(this.context)
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this.context, nowSeconds, secondsRemaining)

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
        setNewTimerLength()
        progress_countdown.progress = 0
        PrefUtil.setSecondsRemaining(timerLengthSeconds, this.context)
        secondsRemaining = timerLengthSeconds
        updateButtons()
        updateCountdownUI()
        if (timerState != TimerState.Stopped)
            sendNotification()
            timerState == TimerState.Stopped
        // TODO: Send Intent to text emergency contact
    }

    private fun sendNotification() {
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