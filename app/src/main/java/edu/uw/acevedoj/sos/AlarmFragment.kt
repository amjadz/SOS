package edu.uw.acevedoj.sos

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
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






class AlarmFragment : Fragment() {
    private val TAG = "AlarmFragment"
    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmsSet: MutableSet<String>
    private lateinit var prefs: SharedPreferences
    private val removeSet = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        alarmsSet = prefs.getStringSet("alarms", mutableSetOf<String>())

        val time = arguments?.getLong("timestamp")
        if (time != null) {
            alarmsSet.add("$time")
            saveAlarms()
        }

        Log.v(TAG, "begin: ${alarmsSet.size}")




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_alarm, container, false)
        val listView = rootView.findViewById<AdapterView<AlarmAdapter>>(R.id.alarm_list_view) as ListView

        val alarms = ArrayList<String>()
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


    override fun onDestroy() {
        super.onDestroy()
        for (alarm in removeSet) {
            alarmsSet.remove(alarm)
        }
        saveAlarms()
        Log.v(TAG, "end: ${alarmsSet.size}")

    }

    fun saveAlarms() {
        val editor = prefs.edit()
        editor.putStringSet("alarms", alarmsSet)
        editor.commit()
    }

    fun dateConverter(time: Long): String {
        return SimpleDateFormat("h:mm a, MMM d").format(time)
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
                removeSet.add("$time")
            }
            return cView
        }

        private inner class ViewHolder {
            var timeText: TextView? = null
            var timeRemainText: TextView? = null
            var checkInButton: TextView? = null
        }
    }
}
