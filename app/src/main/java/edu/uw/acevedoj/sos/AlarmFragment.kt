package edu.uw.acevedoj.sos

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.telephony.SmsManager
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.HOURS
import android.content.Context.ALARM_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.content.Intent








class AlarmFragment : Fragment() {
    private val TAG = "AlarmFragment"
    private lateinit var adapter: AlarmAdapter
    private var alarmsSet = mutableSetOf<String>()
    private lateinit var prefs: SharedPreferences
    private  val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4
    val SENT: String = "SMS_Sent"
    val DELIVERED: String = "SMS_DELIVERED"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val tempAlarmsSet = prefs.getStringSet("alarms", mutableSetOf<String>())


        for (alarm in tempAlarmsSet) {
            alarmsSet.add(alarm)
        }

        val time = arguments?.getLong("timestamp")
        if (time != null) {
            alarmsSet.add("$time")
            saveAlarms()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_alarm, container, false)
        val listView = rootView.findViewById<AdapterView<AlarmAdapter>>(R.id.alarm_list_view) as ListView

        val alarms = ArrayList<String>()

        if (alarmsSet.isNotEmpty()) {
            activity!!.startService(Intent(context, AlarmService::class.java))
        }

        for (alarm in alarmsSet) {
            alarms.add(alarm)
        }
        adapter = AlarmAdapter(requireContext(), alarms)
        listView.adapter = adapter


        val handler = Handler()
        val delay = 1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {
                adapter.notifyDataSetChanged()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())


        val button = rootView.findViewById<TextView>(R.id.add_event_button)
        button.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, DatePickerFragment())
                .addToBackStack(null)
                .commit()
        }

        return rootView
    }


    fun saveAlarms() {
        Log.v(TAG, "save: ${alarmsSet.size}")
        val editor = prefs.edit()
        editor.putStringSet("alarms", alarmsSet)
        editor.commit()
        Log.v(TAG, "end: ${prefs.getStringSet("alarms", mutableSetOf<String>()).size}")
    }

    fun dateConverter(time: Long): String {
        return SimpleDateFormat("h:mm a, MMM d").format(time)
    }

    override fun onPause() {
        super.onPause()
        val aManager: AlarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (a in alarmsSet) {
            val time = a.toLong()
            val millis = time - System.currentTimeMillis()
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                aManager.setAlarmClock(AlarmManager.AlarmClockInfo(System.currentTimeMillis() + millis, sender), sender)
            } else {
                aManager.set(AlarmManager.RTC_WAKEUP, millis, sender)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(context, TimerExpiredReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am!!.cancel(sender)
    }


    inner class AlarmAdapter(context: Context, data :ArrayList<String>) : ArrayAdapter<String>(context, R.layout.alarm_list_item, data) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            lateinit var cView: View
            var holder: ViewHolder

            val time: Long = getItem(position).toLong()
            if (convertView == null) {
                holder = ViewHolder()
                cView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false)
                holder.timeText = cView.findViewById(R.id.time_text)
                holder.timeRemainText = cView.findViewById(R.id.time_remain_text)
                holder.checkInButton = cView.findViewById(R.id.check_in_button)

                cView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                cView = convertView
            }


            val millis = time - System.currentTimeMillis()

//            val btn = cView.findViewById<TextView>(R.id.check_in_button)
//            if (millis < 0 && btn.visibility == View.INVISIBLE) {
//                btn.visibility = View.VISIBLE
//
//                val sentPI = PendingIntent.getBroadcast(requireContext(), 0, Intent(SENT), 0)
//                val delivered = PendingIntent.getBroadcast(requireContext(),0, Intent(DELIVERED), 0)
//                if (millis <= -60000) {
//                    checkForCallPermission()
//                    val textContact = cView.findViewById<View>(R.id.text_contact)
//                    val sms = SmsManager.getDefault()
//                    val textPreferences = prefs.getString("text_preference", "1")
//                    val primaryContact = prefs.getString("contact_text_1", "911")
//                    val msgContent = prefs.getString("text_message_content", "I need help!")
//                    sms.sendTextMessage(primaryContact,null, msgContent, sentPI, delivered)
//                    if (textPreferences == "2") {
//                        for (i in 2..3) {
//                            val contactNumber = prefs.getString("contact_text_$i", " ")
//                            if (contactNumber != " ") {
//                                sms.sendTextMessage(contactNumber, null, msgContent, sentPI, delivered)
//                            }
//                        }
//                    }
//
//                    val intent = Intent(Intent.ACTION_CALL)
//
//                    checkForCallPermission()
//
//                    val callPreferences = prefs.getString("call_preference", "1")
//                    if (callPreferences == "1") {
//                        intent.setData(Uri.parse("tel:911"))
//                    } else {
//                        intent.setData(Uri.parse("tel:${prefs.getString("contact_text_1", "911")}"))
//                    }
//                    startActivity(intent)
//                }
//
//            }


            val hms = String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        millis
                    )
                )
            )

            holder.timeText!!.text = "${dateConverter(time)}"
            holder.timeRemainText!!.text  = "Remaining ${hms}"
            holder.checkInButton!!.setOnClickListener {
                holder.timeRemainText!!.text = "Checked"
                alarmsSet.remove("$time")
                saveAlarms()
            }
            return cView
        }

        private inner class ViewHolder {
            var timeText: TextView? = null
            var timeRemainText: TextView? = null
            var checkInButton: TextView? = null
        }
    }

    private fun checkForCallPermission() {

        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.CALL_PHONE)){

            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.CALL_PHONE, android.Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )
            }

        }

    }
}
