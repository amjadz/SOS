package edu.uw.acevedoj.sos


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import java.util.*



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class TimePickerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_time_picker, container, false)
        val button = rootView.findViewById<TextView>(R.id.time_picker_button)
        val timePicker = rootView.findViewById<TimePicker>(R.id.time_picker)

        button.setOnClickListener {
            val year = arguments!!.getInt("year")
            val month = arguments!!.getInt("month")
            val day = arguments!!.getInt("day")
            val calender = GregorianCalendar(year, month, day, timePicker.hour, timePicker.minute)

            val args = Bundle().apply {
                putLong("timestamp", calender.timeInMillis)
            }
            val fragment = AlarmFragment().apply {
                arguments = args
            }
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        return rootView
    }


}
